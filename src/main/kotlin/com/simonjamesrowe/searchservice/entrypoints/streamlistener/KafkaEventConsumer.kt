package com.simonjamesrowe.searchservice.entrypoints.streamlistener

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.convertValue
import com.simonjamesrowe.model.cms.dto.BlogResponseDTO
import com.simonjamesrowe.model.cms.dto.Constants.TYPE_BLOG
import com.simonjamesrowe.model.cms.dto.Constants.TYPE_JOB
import com.simonjamesrowe.model.cms.dto.Constants.TYPE_SKILL
import com.simonjamesrowe.model.cms.dto.JobResponseDTO
import com.simonjamesrowe.model.cms.dto.WebhookEventDTO
import com.simonjamesrowe.searchservice.core.usecase.IndexBlogUseCase
import com.simonjamesrowe.searchservice.core.usecase.IndexSiteUseCase
import com.simonjamesrowe.searchservice.dataproviders.cms.CmsRestApi
import com.simonjamesrowe.searchservice.mapper.BlogMapper
import com.simonjamesrowe.searchservice.mapper.JobMapper
import com.simonjamesrowe.searchservice.mapper.SkillsGroupMapper
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.function.Consumer

@Configuration
class KafkaEventConsumer(
  private val indexBlogUseCase: IndexBlogUseCase,
  private val indexSiteUseCase: IndexSiteUseCase,
  private val objectMapper: ObjectMapper,
  private val cmsRestApi: CmsRestApi
) {

  companion object {
    val log = LoggerFactory.getLogger(KafkaEventConsumer::class.java)
  }

  @Bean
  fun consumeEvent(): Consumer<List<WebhookEventDTO>> =
    Consumer { events ->
      runCatching {
        log.info("Received events from kafka: ${events.map { "${it.event}-${it.model}" }}")
        updateBlogSearchIndex(events)
        updateSiteSearchIndex(events)
      }.onFailure { exception ->
        if (exception.cause?.javaClass?.packageName?.startsWith("com.fasterxml.jackson") == true) {
          log.error("Error with json deserialization", exception)
        } else {
          throw exception
        }
      }
    }

  private fun updateSiteSearchIndex(events: List<WebhookEventDTO>) {
    if (events.any { it.model == TYPE_SKILL }) {
      cmsRestApi.getAllSkillsGroups().map { SkillsGroupMapper.toSiteIndexRequests(it) }.forEach {
        indexSiteUseCase.indexSites(it)
      }
    }
    indexSiteUseCase.indexSites(events.filter { it.model == TYPE_JOB }
      .map { objectMapper.convertValue<JobResponseDTO>(it.entry) }.map { JobMapper.toIndexSiteRequest(it) })
    indexSiteUseCase.indexSites(events.filter { it.model == TYPE_BLOG }
      .map { objectMapper.convertValue<BlogResponseDTO>(it.entry) }.map { BlogMapper.toSiteIndexRequest(it) })
  }

  private fun updateBlogSearchIndex(events: List<WebhookEventDTO>) {
    events
      .filter { it.model == TYPE_BLOG }
      .map { objectMapper.convertValue<BlogResponseDTO>(it.entry) }
      .map { BlogMapper.toBlogIndexRequest(it) }
      .run {
        if (this.isNotEmpty()) {
          indexBlogUseCase.indexBlogs(this)
        }
      }
  }

}

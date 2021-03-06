package com.simonjamesrowe.searchservice.entrypoints.kafka

import brave.Tracer
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.convertValue
import com.simonjamesrowe.model.cms.dto.BlogResponseDTO
import com.simonjamesrowe.model.cms.dto.Constants.TYPE_BLOG
import com.simonjamesrowe.model.cms.dto.Constants.TYPE_JOB
import com.simonjamesrowe.model.cms.dto.Constants.TYPE_SKILL
import com.simonjamesrowe.model.cms.dto.JobResponseDTO
import com.simonjamesrowe.model.cms.dto.WebhookEventDTO
import com.simonjamesrowe.searchservice.config.runInSpan
import com.simonjamesrowe.searchservice.core.usecase.IndexBlogUseCase
import com.simonjamesrowe.searchservice.core.usecase.IndexSiteUseCase
import com.simonjamesrowe.searchservice.dataproviders.cms.ICmsRestApi
import com.simonjamesrowe.searchservice.mapper.BlogMapper
import com.simonjamesrowe.searchservice.mapper.JobMapper
import com.simonjamesrowe.searchservice.mapper.SkillsGroupMapper
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.messaging.handler.annotation.Header
import org.springframework.messaging.handler.annotation.Headers
import org.springframework.stereotype.Service

@Service
class KafkaEventConsumer(
  private val indexBlogUseCase: IndexBlogUseCase,
  private val indexSiteUseCase: IndexSiteUseCase,
  private val objectMapper: ObjectMapper,
  private val cmsRestApi: ICmsRestApi,
  private val tracer: Tracer
) : IKafkaEventConsumer {

  companion object {
    val log = LoggerFactory.getLogger(KafkaEventConsumer::class.java)
  }

  @KafkaListener(topics = ["\${namespace:LOCAL}_EVENTS"])
  override fun consumeEvents(
    events: List<WebhookEventDTO>,
    @Header(name = "b3", required = false) traceIds: List<String?>?,
    @Headers headers: Map<String, Any>,
  ) = runBlocking<Unit> {
    val traceId = traceIds?.first()?.toLong()
    log.info("Kafka Headers are : ${headers.keys}")
    runInSpan(tracer, "consumeEvents", traceId) {
      runCatching {
        log.info("Received events from kafka: $events")
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

  }

  private suspend fun updateSiteSearchIndex(events: List<WebhookEventDTO>) {
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

  private suspend fun updateBlogSearchIndex(events: List<WebhookEventDTO>) {
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

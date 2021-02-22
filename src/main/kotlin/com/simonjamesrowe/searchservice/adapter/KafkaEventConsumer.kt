package com.simonjamesrowe.searchservice.adapter

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.convertValue
import com.simonjamesrowe.model.data.Constants.TYPE_BLOG
import com.simonjamesrowe.model.data.Constants.TYPE_JOB
import com.simonjamesrowe.model.data.Constants.TYPE_SKILL
import com.simonjamesrowe.model.data.Event
import com.simonjamesrowe.searchservice.dao.BlogSearchRepository
import com.simonjamesrowe.searchservice.dao.SiteSearchRepository
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.function.Consumer

@Configuration
class KafkaEventConsumer(
  private val blogSearchRepository: BlogSearchRepository,
  private val siteSearchRepository: SiteSearchRepository,
  private val objectMapper: ObjectMapper
) {

  companion object {
    val log = LoggerFactory.getLogger(KafkaEventConsumer::class.java)
  }

  @Bean
  fun consumeEvent(): Consumer<List<Event>> =
    Consumer { events ->
      log.info("Received events from kafka: ${events.map { "${it.event}-${it.model}" }}")
      updateBlogSearchIndex(events)
      updateSiteSearchIndex(events)
    }

  private fun updateSiteSearchIndex(events: List<Event>) {
    siteSearchRepository.saveSkills(events.filter { it.model == TYPE_SKILL }
      .map { objectMapper.convertValue(it.entry) })
    siteSearchRepository.saveJobs(events.filter { it.model == TYPE_JOB }.map { objectMapper.convertValue(it.entry) })
    siteSearchRepository.saveBlogs(events.filter { it.model == TYPE_BLOG }.map { objectMapper.convertValue(it.entry) })
  }

  private fun updateBlogSearchIndex(events: List<Event>) {
    blogSearchRepository.saveAll(
      events
        .filter { it.model == TYPE_BLOG }
        .map { objectMapper.convertValue(it.entry) }
    )
  }

}

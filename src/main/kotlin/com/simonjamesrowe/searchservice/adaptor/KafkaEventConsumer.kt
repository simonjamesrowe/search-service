package com.simonjamesrowe.searchservice.adaptor

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.convertValue
import com.simonjamesrowe.model.data.Constants.TYPE_BLOG
import com.simonjamesrowe.model.data.Event
import com.simonjamesrowe.searchservice.dao.BlogSearchRepository
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.function.Consumer

@Configuration
class KafkaEventConsumer(
  private val blogSearchRepository: BlogSearchRepository,
  private val objectMapper: ObjectMapper
) {

  companion object {
    val log = LoggerFactory.getLogger(KafkaEventConsumer::class.java)
  }

  @Bean
  fun consumeEvent(): Consumer<List<Event>> =
    Consumer { events ->
      log.info("Received events from kafka: $events")
      blogSearchRepository.saveAll(
        events
          .filter { it.model == TYPE_BLOG }
          .map { objectMapper.convertValue(it.entry) }
      )
    }

}
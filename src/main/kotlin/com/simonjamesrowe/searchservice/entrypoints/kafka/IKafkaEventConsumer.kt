package com.simonjamesrowe.searchservice.entrypoints.kafka

import com.simonjamesrowe.model.cms.dto.WebhookEventDTO
import org.springframework.kafka.annotation.KafkaListener

interface IKafkaEventConsumer {
  fun consumeEvents(events: List<WebhookEventDTO>)
}

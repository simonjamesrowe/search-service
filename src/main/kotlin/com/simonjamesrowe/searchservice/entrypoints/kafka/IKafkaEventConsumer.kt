package com.simonjamesrowe.searchservice.entrypoints.kafka

import com.simonjamesrowe.model.cms.dto.WebhookEventDTO

interface IKafkaEventConsumer {
  fun consumeEvents(
    events: List<WebhookEventDTO>,
    traceIds: List<String?>? = null,
    headers: Map<String, Any> = emptyMap()
  )
}

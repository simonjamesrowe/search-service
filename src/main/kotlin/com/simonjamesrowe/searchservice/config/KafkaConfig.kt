package com.simonjamesrowe.searchservice.config

import org.apache.kafka.clients.admin.NewTopic
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
/*@TypeHint(
  types = [
    WebhookEventDeserializer::class,
    WebhookEventSerializer::class,
    WebhookEventDTO::class,
    ZonedDateTime::class,
    LocalDateTime::class,
    JsonNode::class,
    JacksonConfig::class
  ],
  access = AccessBits.FULL_REFLECTION
)
@ProxyHint(
  types = [
    Consumer::class,
    Advised::class,
    DecoratingProxy::class
  ]
)*/
class KafkaConfig {

  @Bean
  fun eventsTopic(@Value("\${namespace:LOCAL}_EVENTS") topicName: String) =
    NewTopic(topicName, 1, 1.toShort())

}

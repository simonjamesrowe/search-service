package com.simonjamesrowe.searchservice.config

import com.simonjamesrowe.model.serialization.WebhookEventDeserializer
import com.simonjamesrowe.model.serialization.WebhookEventSerializer
import org.apache.kafka.clients.admin.NewTopic
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.nativex.hint.AccessBits
import org.springframework.nativex.hint.TypeHint

@Configuration
@TypeHint(
  types = [WebhookEventDeserializer::class, WebhookEventSerializer::class],
  access = AccessBits.FULL_REFLECTION
)
class KafkaConfig {

  @Bean
  fun eventsTopic(@Value("\${namespace:LOCAL}_EVENTS") topicName: String) =
    NewTopic(topicName, 1, 1.toShort())

}

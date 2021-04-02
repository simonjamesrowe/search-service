package com.simonjamesrowe.searchservice.config

import org.apache.kafka.clients.admin.NewTopic
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class KafkaConfig {

  @Bean
  fun eventsTopic(@Value("\${namespace:LOCAL}_EVENTS") topicName: String) =
    NewTopic(topicName, 1, 1.toShort())
}

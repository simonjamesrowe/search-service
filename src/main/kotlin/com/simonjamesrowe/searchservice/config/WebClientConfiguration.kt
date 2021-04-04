package com.simonjamesrowe.searchservice.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.codec.ClientCodecConfigurer
import org.springframework.web.reactive.function.client.ExchangeStrategies
import org.springframework.web.reactive.function.client.WebClient


@Configuration
class WebClientConfiguration {

  @Bean
  fun webClient(): WebClient = WebClient.builder().exchangeStrategies(ExchangeStrategies.builder()
    .codecs { configurer: ClientCodecConfigurer ->
      configurer
        .defaultCodecs()
        .maxInMemorySize(16 * 1024 * 1024)
    }
    .build())
    .build()

}

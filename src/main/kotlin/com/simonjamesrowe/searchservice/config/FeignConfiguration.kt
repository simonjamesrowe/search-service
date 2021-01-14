package com.simonjamesrowe.searchservice.config

import com.fasterxml.jackson.databind.ObjectMapper
import feign.codec.Decoder
import feign.codec.Encoder
import feign.jackson.JacksonDecoder
import feign.jackson.JacksonEncoder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class FeignConfiguration(
  private val objectMapper: ObjectMapper
) {

  @Bean
  fun decoder() : Decoder = JacksonDecoder(objectMapper)

  @Bean
  fun encoder() : Encoder = JacksonEncoder(objectMapper)

}
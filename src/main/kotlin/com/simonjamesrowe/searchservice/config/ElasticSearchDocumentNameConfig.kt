package com.simonjamesrowe.searchservice.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ElasticSearchDocumentNameConfig {

  @Bean
  fun blogIndexName(elasticSearchIndexProperties: ElasticSearchIndexProperties): String =
    elasticSearchIndexProperties.blog
}
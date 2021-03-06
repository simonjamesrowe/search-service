package com.simonjamesrowe.searchservice.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ElasticSearchConfig {

  @Bean
  fun blogIndexName(
    elasticSearchIndexProperties: ElasticSearchIndexProperties
  ): String = elasticSearchIndexProperties.blog

  @Bean
  fun siteIndexName(
    elasticSearchIndexProperties: ElasticSearchIndexProperties
  ): String = elasticSearchIndexProperties.site
}

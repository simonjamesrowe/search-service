package com.simonjamesrowe.searchservice.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.nativex.hint.TypeHint
import java.time.LocalDate

@Configuration
@TypeHint(
  types = [LocalDate::class]
)
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

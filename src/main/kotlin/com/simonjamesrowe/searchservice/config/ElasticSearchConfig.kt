package com.simonjamesrowe.searchservice.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.domain.*
import org.springframework.data.querydsl.QPageRequest
import org.springframework.nativex.hint.AccessBits
import org.springframework.nativex.hint.TypeHint
import java.time.LocalDate

@Configuration
@TypeHint(
  types = [
    LocalDate::class,
    Pageable::class,
    AbstractPageRequest::class,
    PageRequest::class,
    QPageRequest::class,
    Sort::class,
    Slice::class,
    Page::class,
    Iterable::class,
    java.lang.Iterable::class
  ],
  typeNames = [
    "java.lang.Iterable[]"
  ],
  access = AccessBits.FULL_REFLECTION
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

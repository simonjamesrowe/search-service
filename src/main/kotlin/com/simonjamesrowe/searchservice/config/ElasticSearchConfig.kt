package com.simonjamesrowe.searchservice.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
/**@TypeHint(
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
"java.lang.Iterable[]",
"org.springframework.data.domain.Slice[]",
"org.springframework.data.domain.Streamable[]",
"org.springframework.data.domain.Sort[]",
"org.springframework.data.domain.Pageable[]",
"org.springframework.data.domain.Page[]",
"org.springframework.data.elasticsearch.core.query.Query[]",
"org.elasticsearch.index.query.QueryBuilder[]"
],
access = AccessBits.FULL_REFLECTION
)*/
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

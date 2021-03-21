package com.simonjamesrowe.searchservice

import co.elastic.apm.attach.ElasticApmAttacher
import co.elastic.apm.opentracing.ElasticApmTracer
import com.simonjamesrowe.searchservice.config.ElasticSearchIndexProperties
import com.simonjamesrowe.searchservice.core.model.BlogSearchResult
import com.simonjamesrowe.searchservice.core.model.SiteSearchResult
import com.simonjamesrowe.searchservice.dataproviders.cms.CmsProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.context.annotation.Bean
import org.springframework.nativex.hint.AccessBits
import org.springframework.nativex.hint.TypeHint
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableFeignClients
@EnableScheduling
@EnableConfigurationProperties(
  ElasticSearchIndexProperties::class,
  CmsProperties::class
)
@TypeHint(types = [BlogSearchResult::class, SiteSearchResult::class], access = AccessBits.FULL_REFLECTION)
class SearchServiceApplication {

  @Bean
  fun apmTracer() = ElasticApmTracer()
}

fun main(args: Array<String>) {
  ElasticApmAttacher.attach()
  runApplication<SearchServiceApplication>(*args)
}

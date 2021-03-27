package com.simonjamesrowe.searchservice

import com.simonjamesrowe.searchservice.config.ElasticSearchIndexProperties
import com.simonjamesrowe.searchservice.dataproviders.cms.CmsProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties(
  ElasticSearchIndexProperties::class,
  CmsProperties::class
)
/*
@TypeHint(
  types = [BlogSearchResult::class, SiteSearchResult::class],
  access = AccessBits.FULL_REFLECTION
)*/
class SearchServiceApplication

fun main(args: Array<String>) {
  //ElasticApmAttacher.attach()
  runApplication<SearchServiceApplication>(*args)
}

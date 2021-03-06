package com.simonjamesrowe.searchservice

import co.elastic.apm.attach.ElasticApmAttacher
import co.elastic.apm.opentracing.ElasticApmTracer
import com.simonjamesrowe.searchservice.config.CmsProperties
import com.simonjamesrowe.searchservice.config.ElasticSearchIndexProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.context.annotation.Bean
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableFeignClients
@EnableScheduling
@EnableConfigurationProperties(
	ElasticSearchIndexProperties::class,
	CmsProperties::class
)
class SearchServiceApplication {

	@Bean
	fun apmTracer() = ElasticApmTracer()
}

fun main(args: Array<String>) {
	ElasticApmAttacher.attach()
	runApplication<SearchServiceApplication>(*args)
}

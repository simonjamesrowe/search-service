package com.simonjamesrowe.searchservice

import com.simonjamesrowe.searchservice.config.CmsProperties
import com.simonjamesrowe.searchservice.config.ElasticSearchIndexProperties
import org.springframework.boot.autoconfigure.ImportAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableFeignClients
@EnableScheduling
@EnableConfigurationProperties(
	ElasticSearchIndexProperties::class,
	CmsProperties::class
)
class SearchServiceApplication

fun main(args: Array<String>) {
	runApplication<SearchServiceApplication>(*args)
}

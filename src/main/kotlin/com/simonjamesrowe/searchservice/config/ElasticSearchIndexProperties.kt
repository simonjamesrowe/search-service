package com.simonjamesrowe.searchservice.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties("elasticsearch.index")
data class ElasticSearchIndexProperties(
  val blog: String
)

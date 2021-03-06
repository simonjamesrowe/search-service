package com.simonjamesrowe.searchservice.dataproviders.cms

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties("cms")
data class CmsProperties(
  val url: String
)

package com.simonjamesrowe.searchservice.core.model

import org.springframework.data.annotation.Id
import org.springframework.data.elasticsearch.annotations.Document
import org.springframework.data.elasticsearch.annotations.Field

data class IndexSiteRequest(
  val id: String,
  val siteUrl: String,
  val name: String,
  val type: String,
  val image: String,
  val shortDescription: String,
  val longDescription: String
)

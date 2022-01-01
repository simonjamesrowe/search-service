package com.simonjamesrowe.searchservice.core.model

data class IndexSiteRequest(
  val id: String,
  val siteUrl: String,
  val name: String,
  val type: String,
  val image: String,
  val shortDescription: String,
  val longDescription: String
)

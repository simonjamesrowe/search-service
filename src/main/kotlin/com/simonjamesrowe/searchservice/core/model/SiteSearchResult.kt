package com.simonjamesrowe.searchservice.core.model

data class SiteSearchResult(
  val type: String,
  val hits: List<Hit>
) {
  data class Hit(
    val name: String,
    val imageUrl: String,
    val link: String
  )
}

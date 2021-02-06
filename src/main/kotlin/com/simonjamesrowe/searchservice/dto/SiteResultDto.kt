package com.simonjamesrowe.searchservice.dto

data class SiteResultDto(
  val type: String,
  val hits: List<Hit>
) {
  data class Hit (
    val name: String,
    val imageUrl: String,
    val link: String
  )
}

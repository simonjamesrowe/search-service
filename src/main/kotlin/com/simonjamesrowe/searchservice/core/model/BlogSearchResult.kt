package com.simonjamesrowe.searchservice.core.model

import java.time.LocalDate

data class BlogSearchResult(
  val id: String,
  val title: String,
  val shortDescription: String,
  val tags: List<String>,
  val thumbnailImage: String,
  val smallImage: String?,
  val mediumImage: String?,
  val createdDate: LocalDate
)

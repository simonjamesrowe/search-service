package com.simonjamesrowe.searchservice.core.model

import java.time.LocalDate

data class IndexBlogRequest(
  val id: String,
  val title: String,
  val shortDescription: String,
  val content: String?,
  val tags: List<String>,
  val skills: List<String>,
  val thumbnailImage: String,
  val smallImage: String?,
  val mediumImage: String?,
  val createdDate: LocalDate,
  val published: Boolean
)

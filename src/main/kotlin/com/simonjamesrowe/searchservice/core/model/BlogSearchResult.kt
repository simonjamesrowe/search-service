package com.simonjamesrowe.searchservice.core.model

import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.data.annotation.Id
import org.springframework.data.elasticsearch.annotations.DateFormat
import org.springframework.data.elasticsearch.annotations.Document
import org.springframework.data.elasticsearch.annotations.Field
import org.springframework.data.elasticsearch.annotations.FieldType
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

data class BlogSearchResult(
  val id : String,
  val title: String,
  val shortDescription: String,
  val tags: List<String>,
  val thumbnailImage: String,
  val smallImage: String?,
  val mediumImage: String?,
  val createdDate: LocalDate
)

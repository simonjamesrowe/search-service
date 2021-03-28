package com.simonjamesrowe.searchservice.dataproviders.elasticsearch.blog

import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.data.annotation.Id
import org.springframework.data.elasticsearch.annotations.DateFormat
import org.springframework.data.elasticsearch.annotations.Document
import org.springframework.data.elasticsearch.annotations.Field
import org.springframework.data.elasticsearch.annotations.FieldType
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

@Document(indexName = "#{@blogIndexName}", createIndex = false)
data class BlogDocument(

  @Id
  var id : String,

  @Field
  val title: String,

  @Field
  val shortDescription: String,

  @Field
  @JsonIgnore
  val content: String?,

  @Field
  val tags: List<String>,

  @Field
  val skills: List<String>,

  @Field
  val thumbnailImage: String,

  @Field
  val smallImage: String?,

  @Field
  val mediumImage: String?,

  @Field(
    type = FieldType.Date,
    format = DateFormat.custom,
    pattern = "uuuu-MM-dd"
  )
  val createdDate: LocalDate

)

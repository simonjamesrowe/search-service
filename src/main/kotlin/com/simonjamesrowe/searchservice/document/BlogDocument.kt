package com.simonjamesrowe.searchservice.document

import org.springframework.data.annotation.Id
import org.springframework.data.elasticsearch.annotations.DateFormat
import org.springframework.data.elasticsearch.annotations.Document
import org.springframework.data.elasticsearch.annotations.Field
import org.springframework.data.elasticsearch.annotations.FieldType
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

@Document(indexName = "#{@blogIndexName}")
data class BlogDocument(

  @Id
  val id : String,

  @Field(type = FieldType.Search_As_You_Type, store = true, index = true)
  val title: String,

  @Field(type = FieldType.Text, store = false, index = true)
  val content: String?,

  @Field(type = FieldType.Keyword, store = true, index = true)
  val tags: List<String>,

  @Field(type = FieldType.Keyword, store = true, index = true)
  val skills: List<String>,

  @Field(type = FieldType.Keyword, store = true, index = false)
  val thumbnailImage: String,

  @Field(type = FieldType.Keyword, store = true, index = false)
  val smallImage: String?,

  @Field(type = FieldType.Keyword, store = true, index = false)
  val mediumImage: String?,

  @Field(
    type = FieldType.Date,
    store = true,
    index = true,
    format = DateFormat.custom,
    pattern = "uuuu-MM-dd"
  )
  val createdDate: LocalDate

)

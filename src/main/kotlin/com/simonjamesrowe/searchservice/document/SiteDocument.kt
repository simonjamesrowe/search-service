package com.simonjamesrowe.searchservice.document

import org.springframework.data.annotation.Id
import org.springframework.data.elasticsearch.annotations.Document
import org.springframework.data.elasticsearch.annotations.Field

@Document(indexName = "#{@siteIndexName}", createIndex = false)
data class SiteDocument(
  @Id
  val id: String,

  @Field
  val siteUrl: String,

  @Field
  val name: String,

  @Field
  val type: String,

  @Field
  val image: String,

  @Field
  val shortDescription: String,

  @Field
  val longDescription: String

)

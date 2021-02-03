package com.simonjamesrowe.searchservice.document

import com.simonjamesrowe.component.test.BaseComponentTest
import com.simonjamesrowe.component.test.elasticsearch.WithElasticsearchContainer
import com.simonjamesrowe.component.test.kafka.WithKafkaContainer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates

@WithKafkaContainer
@WithElasticsearchContainer
internal class SiteDocumentIndexConfigTest : BaseComponentTest() {

  @Autowired
  private lateinit var elasticsearchRestTemplate: ElasticsearchRestTemplate

  @Test
  fun `site index should have expected mappings and settings`() {
    val indexOps = elasticsearchRestTemplate.indexOps(IndexCoordinates.of("site_local"))
    val mapping = indexOps.mapping
    val properties = mapping["properties"] as Map<String, Any>
    println(properties)
    assertThat(properties["name"]).isEqualTo(
      mapOf(
        "doc_values" to false,
        "max_shingle_size" to 3,
        "store" to true,
        "type" to "search_as_you_type"
      )
    )
    assertThat(properties["siteUrl"]).isEqualTo(
      mapOf(
        "index" to false,
        "store" to true,
        "type" to "keyword"
      )
    )
    assertThat(properties["image"]).isEqualTo(
      mapOf(
        "index" to false,
        "store" to true,
        "type" to "keyword"
      )
    )
    assertThat(properties["shortDescription"]).isEqualTo(
      mapOf(
        "analyzer" to "markdown_text",
        "type" to "text"
      )
    )
    assertThat(properties["longDescription"]).isEqualTo(
      mapOf(
        "analyzer" to "markdown_text",
        "type" to "text"
      )
    )
  }

}

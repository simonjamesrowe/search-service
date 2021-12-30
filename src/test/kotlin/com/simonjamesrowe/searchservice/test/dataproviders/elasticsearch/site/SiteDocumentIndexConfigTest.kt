package com.simonjamesrowe.searchservice.test.dataproviders.elasticsearch.site

import com.simonjamesrowe.component.test.elasticsearch.WithElasticsearchContainer
import com.simonjamesrowe.searchservice.config.ElasticSearchConfig
import com.simonjamesrowe.searchservice.config.ElasticSearchIndexProperties
import com.simonjamesrowe.searchservice.dataproviders.elasticsearch.site.SiteDocumentIndexConfig
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.ImportAutoConfiguration
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchDataAutoConfiguration
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchRepositoriesAutoConfiguration
import org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchRestClientAutoConfiguration
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.test.autoconfigure.json.JsonTest
import org.springframework.context.annotation.Import
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates


@WithElasticsearchContainer
@JsonTest
@ImportAutoConfiguration(
  ElasticsearchDataAutoConfiguration::class,
  ElasticsearchRepositoriesAutoConfiguration::class,
  ElasticsearchRestClientAutoConfiguration::class
)
@EnableConfigurationProperties(ElasticSearchIndexProperties::class)
@Import(SiteDocumentIndexConfig::class, ElasticSearchConfig::class)
internal class SiteDocumentIndexConfigTest {

  @Autowired
  private lateinit var elasticsearchRestTemplate: ElasticsearchRestTemplate

  @Test
  fun `site index should have expected mappings and settings`() {
    val indexOps = elasticsearchRestTemplate.indexOps(IndexCoordinates.of("site_local"))
    val mapping = indexOps.mapping
    val properties = mapping["properties"] as Map<String, Any>
    Assertions.assertThat(properties["name"]).isEqualTo(
      mapOf(
        "type" to "text",
        "fields" to mapOf(
          "search" to mapOf(
            "doc_values" to false,
            "max_shingle_size" to 3,
            "type" to "search_as_you_type"
          ),
          "raw" to mapOf(
            "type" to "keyword"
          )
        )
      )
    )
    Assertions.assertThat(properties["siteUrl"]).isEqualTo(
      mapOf(
        "index" to false,
        "store" to true,
        "type" to "keyword"
      )
    )
    Assertions.assertThat(properties["image"]).isEqualTo(
      mapOf(
        "index" to false,
        "store" to true,
        "type" to "keyword"
      )
    )
    Assertions.assertThat(properties["shortDescription"]).isEqualTo(
      mapOf(
        "analyzer" to "markdown_text",
        "type" to "text"
      )
    )
    Assertions.assertThat(properties["longDescription"]).isEqualTo(
      mapOf(
        "analyzer" to "markdown_text",
        "type" to "text"
      )
    )
  }

}

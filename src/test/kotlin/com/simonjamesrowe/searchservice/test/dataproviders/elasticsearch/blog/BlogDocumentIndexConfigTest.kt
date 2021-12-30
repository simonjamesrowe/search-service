package com.simonjamesrowe.searchservice.test.dataproviders.elasticsearch.blog

import com.simonjamesrowe.component.test.elasticsearch.WithElasticsearchContainer
import com.simonjamesrowe.searchservice.config.ElasticSearchConfig
import com.simonjamesrowe.searchservice.config.ElasticSearchIndexProperties
import com.simonjamesrowe.searchservice.dataproviders.elasticsearch.blog.BlogDocumentIndexConfig
import org.assertj.core.api.Assertions.assertThat
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
@Import(BlogDocumentIndexConfig::class, ElasticSearchConfig::class)
internal class BlogDocumentIndexConfigTest {

  @Autowired
  private lateinit var elasticsearchRestTemplate: ElasticsearchRestTemplate

  @Test
  fun `blog index should have expected mappings and settings`() {
    val indexOps = elasticsearchRestTemplate.indexOps(IndexCoordinates.of("blog_local"))
    val mapping = indexOps.mapping
    val properties = mapping["properties"] as Map<String, Any>
    assertThat(properties["skills"]).isEqualTo(
      mapOf(
        "analyzer" to "lowercase_keyword",
        "store" to true,
        "type" to "text"
      )
    )
    assertThat(properties["shortDescription"]).isEqualTo(
      mapOf(
        "store" to true,
        "type" to "text"
      )
    )
    assertThat(properties["tags"]).isEqualTo(
      mapOf(
        "analyzer" to "lowercase_keyword",
        "store" to true,
        "type" to "text"
      )
    )
    assertThat(properties["createdDate"]).isEqualTo(
      mapOf(
        "format" to "uuuu-MM-dd",
        "store" to true,
        "type" to "date"
      )
    )
    assertThat(properties["smallImage"]).isEqualTo(
      mapOf(
        "index" to false,
        "store" to true,
        "type" to "keyword"
      )
    )
    assertThat(properties["mediumImage"]).isEqualTo(
      mapOf(
        "index" to false,
        "store" to true,
        "type" to "keyword"
      )
    )
    assertThat(properties["thumbnailImage"]).isEqualTo(
      mapOf(
        "index" to false,
        "store" to true,
        "type" to "keyword"
      )
    )
    assertThat(properties["title"]).isEqualTo(
      mapOf(
        "store" to true,
        "doc_values" to false,
        "max_shingle_size" to 3,
        "type" to "search_as_you_type"
      )
    )
    assertThat(properties["content"]).isEqualTo(
      mapOf(
        "analyzer" to "markdown_text",
        "type" to "text"
      )
    )
  }

}

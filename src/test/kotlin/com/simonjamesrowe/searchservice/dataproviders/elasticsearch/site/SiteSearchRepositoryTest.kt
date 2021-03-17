package com.simonjamesrowe.searchservice.dataproviders.elasticsearch.site

import com.simonjamesrowe.component.test.TestContainersExtension
import com.simonjamesrowe.component.test.elasticsearch.WithElasticsearchContainer
import com.simonjamesrowe.searchservice.config.ElasticSearchDocumentNameConfig
import com.simonjamesrowe.searchservice.config.ElasticSearchIndexProperties
import com.simonjamesrowe.searchservice.core.model.IndexSiteRequest
import com.tyro.oss.arbitrater.arbitrary
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
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

@JsonTest
@ExtendWith(TestContainersExtension::class)
@WithElasticsearchContainer
@ImportAutoConfiguration(
  ElasticsearchDataAutoConfiguration::class,
  ElasticsearchRepositoriesAutoConfiguration::class,
  ElasticsearchRestClientAutoConfiguration::class
)
@EnableConfigurationProperties(ElasticSearchIndexProperties::class)
@Import(SiteDocumentIndexConfig::class, SiteSearchRepository::class, ElasticSearchDocumentNameConfig::class)
internal class SiteSearchRepositoryTest {

  @Autowired
  private lateinit var siteSearchRepository: SiteSearchRepository

  @Autowired
  private lateinit var siteDocumentRepository: SiteDocumentRepository

  @BeforeEach
  fun setupData() {
    siteDocumentRepository.deleteAll()
    val indexRequests = listOf(
      IndexSiteRequest(
        id = "blog_1",
        name = "My first Blog",
        shortDescription = "jenkins",
        longDescription = "",
        image = "http://image1",
        type = "Blog",
        siteUrl = "/blogs/1"
      ),
      IndexSiteRequest(
        id = "blog_2",
        name = "My second Blog",
        shortDescription = "spring",
        longDescription = "",
        image = "http://image2",
        type = "Blog",
        siteUrl = "/blogs/2"
      ),
      IndexSiteRequest(
        id = "jobs_1",
        name = "Senior Developer (Some Company)",
        shortDescription = "spring",
        longDescription = "",
        image = "http://image3",
        type = "Job",
        siteUrl = "/jobs/1"
      ),
      IndexSiteRequest(
        id = "skills_1",
        name = "Spring Boot",
        shortDescription = "spring boot",
        longDescription = "",
        image = "http://image4",
        type = "Skill",
        siteUrl = "skills/1"
      )
    )
    siteSearchRepository.indexSites(indexRequests)
  }

  @Test
  fun `should return relevant results`() {
    val results = siteSearchRepository.search("spring")
    assertThat(results).hasSize(3)
    assertThat(results[0].type).isEqualTo("Blog")
    assertThat(results[1].type).isEqualTo("Job")
    assertThat(results[2].type).isEqualTo("Skill")
  }

  @Test
  fun `should convert to SiteDocument`() {
    val input : IndexSiteRequest = arbitrary()
    val expected = SiteDocument(
      id = input.id,
      siteUrl = input.siteUrl,
      shortDescription = input.shortDescription,
      longDescription = input.longDescription,
      name = input.name,
      type = input.type,
      image = input.image
    )
    assertThat(siteSearchRepository.toSiteDocument(input)).isEqualTo(expected)
  }

}

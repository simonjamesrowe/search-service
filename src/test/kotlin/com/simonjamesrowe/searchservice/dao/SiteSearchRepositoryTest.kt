package com.simonjamesrowe.searchservice.dao

import com.simonjamesrowe.component.test.BaseComponentTest
import com.simonjamesrowe.component.test.elasticsearch.WithElasticsearchContainer
import com.simonjamesrowe.component.test.kafka.WithKafkaContainer
import com.simonjamesrowe.searchservice.document.SiteDocument
import com.simonjamesrowe.searchservice.dto.SiteResultDto
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

@WithElasticsearchContainer
@WithKafkaContainer
internal class SiteSearchRepositoryTest : BaseComponentTest() {

  @Autowired
  lateinit var siteSearchRepository: SiteSearchRepository

  @Autowired
  lateinit var siteDocumentRepository: SiteDocumentRepository

  @BeforeEach
  @AfterEach
  fun initData() {
    siteDocumentRepository.deleteAll()
  }

  @Test
  fun `should return grouped results`() {
    siteDocumentRepository.saveAll(
      listOf(
        SiteDocument(
          id = "blog_1",
          name = "My first Blog",
          shortDescription = "jenkins",
          longDescription = "",
          image = "http://image1",
          type = "Blog",
          siteUrl = "/blogs/1"
        ),
        SiteDocument(
          id = "blog_2",
          name = "My second Blog",
          shortDescription = "spring",
          longDescription = "",
          image = "http://image2",
          type = "Blog",
          siteUrl = "/blogs/2"
        ),
        SiteDocument(
          id = "jobs_1",
          name = "Senior Developer (Some Company)",
          shortDescription = "spring",
          longDescription = "",
          image = "http://image3",
          type = "Job",
          siteUrl = "/jobs/1"
        ),
        SiteDocument(
          id = "skills_1",
          name = "Spring Boot",
          shortDescription = "spring boot",
          longDescription = "",
          image = "http://image4",
          type = "Skill",
          siteUrl = "skills/1"
        )
      )
    )

    val expectedResults = listOf(
      SiteResultDto(
        type = "Blog",
        hits = listOf(
          SiteResultDto.Hit(
            name = "My second Blog",
            imageUrl = "http://image2",
            link = "/blogs/2"
          )
        )
      ),
      SiteResultDto(
        type = "Job",
        hits = listOf(
          SiteResultDto.Hit(
            name = "Senior Developer (Some Company)",
            imageUrl = "http://image3",
            link = "/jobs/1"
          )
        )
      ),
      SiteResultDto(
        type = "Skill",
        hits = listOf(
          SiteResultDto.Hit(
            name = "Spring Boot",
            imageUrl = "http://image4",
            link = "skills/1"
          )
        )
      )
    )
    val results = siteSearchRepository.searchSite("spring")
    assertThat(results).isEqualTo(expectedResults)
  }
}

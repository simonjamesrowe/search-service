package com.simonjamesrowe.searchservice.adapter

import com.simonjamesrowe.component.test.BaseComponentTest
import com.simonjamesrowe.component.test.elasticsearch.WithElasticsearchContainer
import com.simonjamesrowe.component.test.kafka.WithKafkaContainer
import com.simonjamesrowe.searchservice.dao.SiteDocumentRepository
import com.simonjamesrowe.searchservice.document.SiteDocument
import io.restassured.RestAssured.given
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.equalTo
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.annotation.DirtiesContext

@WithElasticsearchContainer
@WithKafkaContainer
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
internal class SiteControllerTest : BaseComponentTest() {

  @Autowired
  lateinit var siteDocumentRepository: SiteDocumentRepository

  @BeforeEach
  @AfterEach
  fun initData() {
    siteDocumentRepository.deleteAll()
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
  }


  @Test
  fun `site search should return expected results`() {
    given()
      .contentType("application/json")
      .param("q", "spring")
      .get("/site")
      .then()
      .statusCode(200)
      .log()
      .all()
      .body(
        "size()", `is`(3),
        "[0].type", equalTo("Blog"),
        "[0].hits.size()", `is`(1),
        "[1].type", equalTo("Job"),
        "[1].hits.size()", `is`(1),
        "[2].type", equalTo("Skill"),
        "[2].hits.size()", `is`(1),
      )
  }


}

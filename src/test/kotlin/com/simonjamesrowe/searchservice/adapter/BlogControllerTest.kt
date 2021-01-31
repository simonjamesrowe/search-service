package com.simonjamesrowe.searchservice.adapter

import com.simonjamesrowe.component.test.BaseComponentTest
import com.simonjamesrowe.component.test.elasticsearch.WithElasticsearchContainer
import com.simonjamesrowe.component.test.kafka.WithKafkaContainer
import com.simonjamesrowe.searchservice.dao.BlogDocumentRepository
import com.simonjamesrowe.searchservice.document.BlogDocument
import io.restassured.RestAssured.given
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.equalTo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.annotation.DirtiesContext
import java.time.LocalDate

@WithElasticsearchContainer
@WithKafkaContainer
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
internal class BlogControllerTest : BaseComponentTest() {

  @Autowired
  lateinit var blogDocumentRepository: BlogDocumentRepository

  @BeforeEach
  fun setupData() {
    blogDocumentRepository.deleteAll()
    val blog1 = BlogDocument(
      id = "1",
      title = "My first blog on kotlin",
      content = "This contains <b> test on </b> kotlin jvm",
      tags = listOf("kotlin", "jvm"),
      skills = listOf("kotlin", "spring"),
      createdDate = LocalDate.of(2021, 1, 1),
      mediumImage = "/uploads/blog1-med.jpg",
      smallImage = "/uploads/blog1-small.jpg",
      thumbnailImage = "/uploads/blog2-thumb.jpg",
      shortDescription = "Short description 1"
    )
    val blog2 = BlogDocument(
      id = "2",
      title = "Pact for contract testing",
      content = "This contains <b> test on </b> pact spring contract tests broker with kotlin",
      tags = listOf("pact", "jvm", "contract-testing"),
      skills = listOf("junit", "jenkins", "spring", "kotlin"),
      createdDate = LocalDate.of(2021, 1, 2),
      mediumImage = "/uploads/blog2-med.jpg",
      smallImage = "/uploads/blog2-small.jpg",
      thumbnailImage = "/uploads/blog2-thumb.jpg",
      shortDescription = "shortDescription2"
    )
    val blog3 = BlogDocument(
      id = "3",
      title = "Continuous Integration with Jenkins X",
      content = "Jenkins X is the best, uses helm and docker and kubernetes",
      tags = listOf("jenkins", "ci", "cd"),
      skills = listOf("helm", "jenkins", "kubernetes", "docker"),
      createdDate = LocalDate.of(2021, 1, 3),
      mediumImage = "/uploads/blog3-med.jpg",
      smallImage = "/uploads/blog3-small.jpg",
      thumbnailImage = "/uploads/blog3-thumb.jpg",
      shortDescription = "shortDescription3"
    )
    blogDocumentRepository.saveAll(listOf(blog1, blog2, blog3))
  }

  @Test
  fun `search blogs by query should return expected results`() {
    given()
      .contentType("application/json")
      .param("q", "kotlin")
      .get("/blogs")
      .then()
      .statusCode(200)
      .log()
      .all()
      .body(
        "size()", `is`(2),
        "[0].id", equalTo("1"),
        "[1].id", equalTo("2")
      )
  }

  @Test
  fun `search all should return all blogs sorted by created date desc`() {
    given()
      .contentType("application/json")
      .get("/blogs")
      .then()
      .statusCode(200)
      .log()
      .all()
      .body(
        "size()", `is`(3),
        "[0].id", equalTo("3"),
        "[1].id", equalTo("2"),
        "[2].id", equalTo("1")
      )
  }

  @Test
  fun `search all should return all blogs for given type sorted by created date desc`() {
    given()
      .contentType("application/json")
      .get("/tags/JVM/blogs")
      .then()
      .statusCode(200)
      .log()
      .all()
      .body(
        "size()", `is`(2),
        "[0].id", equalTo("2"),
        "[1].id", equalTo("1")
      )
  }

}

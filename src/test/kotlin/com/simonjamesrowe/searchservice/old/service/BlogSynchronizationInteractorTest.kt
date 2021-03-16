package com.simonjamesrowe.searchservice.old.service

import com.github.tomakehurst.wiremock.client.WireMock.*
import com.simonjamesrowe.component.test.BaseComponentTest
import com.simonjamesrowe.component.test.elasticsearch.WithElasticsearchContainer
import com.simonjamesrowe.component.test.kafka.WithKafkaContainer
import com.simonjamesrowe.searchservice.dataproviders.cms.CmsRestApi
import com.simonjamesrowe.searchservice.dataproviders.elasticsearch.blog.BlogDocumentRepository
import com.simonjamesrowe.searchservice.dataproviders.elasticsearch.blog.BlogRepository
import com.simonjamesrowe.searchservice.dataproviders.elasticsearch.site.SiteDocumentRepository
import com.simonjamesrowe.searchservice.entrypoints.scheduled.CmsSynchronization
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.ClassPathResource
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.TestPropertySource
import java.nio.file.Files
import java.util.stream.Collectors

@WithKafkaContainer
@WithElasticsearchContainer
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@TestPropertySource("classpath:cms.properties")
internal class BlogSynchronizationInteractorTest : BaseComponentTest() {

  @Autowired
  private lateinit var blogDocumentRepository: BlogDocumentRepository

  @Autowired
  private lateinit var blogRepository: BlogRepository

  @Autowired
  private lateinit var siteDocumentRepository: SiteDocumentRepository

  @Autowired
  private lateinit var blogRestRestApi: CmsRestApi

  private lateinit var cmsSynchronization: CmsSynchronization

  @BeforeEach
  @AfterEach
  fun clearAllDocuments() {
    blogDocumentRepository.deleteAll()
  }

  @BeforeEach
  fun createTestInstance() {
   // cmsSynchronization =
   //   CmsSynchronization(blogRestRestApi, blogRepository, siteDocumentRepository)
  }

  @BeforeEach
  fun setupWiremock() {
    wireMockServer.addStubMapping(
      stubFor(
        get(urlEqualTo("/blogs"))
          .willReturn(
            aResponse()
              .withHeader("Content-Type", "application/json")
              .withBody(
                Files.lines(
                  ClassPathResource("getAllBlogs.json").file.toPath()
                ).collect(Collectors.joining(System.lineSeparator()))
              )
          )
      )
    )
  }

  @Test
  fun `should synchronize all blogs from cms into elasticsearch`() {
    cmsSynchronization.syncBlogDocuments()
    assertThat(blogDocumentRepository.count()).isEqualTo(10)
  }

}

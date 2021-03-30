package com.simonjamesrowe.searchservice.test.dataproviders.cms

import com.github.tomakehurst.wiremock.WireMockServer
import com.simonjamesrowe.searchservice.config.WebClientConfiguration
import com.simonjamesrowe.searchservice.dataproviders.cms.CmsProperties
import com.simonjamesrowe.searchservice.dataproviders.cms.CmsRestApi
import com.simonjamesrowe.searchservice.dataproviders.cms.ICmsRestApi
import com.simonjamesrowe.searchservice.test.TestUtils.mockGet
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.test.autoconfigure.json.JsonTest
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDate

@JsonTest
@AutoConfigureWireMock(port = 0)
@ActiveProfiles("cms")
@EnableConfigurationProperties(CmsProperties::class)
@Import(CmsRestApi::class, WebClientConfiguration::class)
internal class CmsRestApiTest {

  @Autowired
  private lateinit var cmsRestApi: ICmsRestApi

  @Autowired
  private lateinit var wireMockServer: WireMockServer

  @BeforeEach
  fun setupWiremock() {
    mockGet(wireMockServer, "/blogs", "getAllBlogs.json")
    mockGet(wireMockServer, "/skills-groups", "getAllSkillsGroups.json")
    mockGet(wireMockServer, "/jobs", "getAllJobs.json")
  }

  @Test
  fun `should return all blogs from cms`() = runBlocking<Unit> {
    val result = cmsRestApi.getAllBlogs()
    assertThat(result).hasSize(10)
    assertThat(result[0]).hasFieldOrPropertyWithValue("id", "5f0215c69d8081001fd38fa1")
    assertThat(result[0]).hasFieldOrPropertyWithValue("published", true)
    assertThat(result[0].tags.map { it.name }).contains("Kubernetes", "Jenkins", "Strapi", "TLS", "MongoDB", "React")
    assertThat(result[0].skills.map { it.name }).contains("Jenkins Pipeline")
    assertThat(result[0]).hasFieldOrPropertyWithValue("title", "Creating a rich web app that can be hosted from home")
    assertThat(result[0].content).isNotNull
    assertThat(result[0].shortDescription).isNotNull
  }

  @Test
  fun `should return all jobs from cms`() = runBlocking<Unit> {
    val result = cmsRestApi.getAllJobs()
    assertThat(result).hasSize(9)
    assertThat(result[0]).hasFieldOrPropertyWithValue("id", "5e53704f11c196001d06f914")
    assertThat(result[0]).hasFieldOrPropertyWithValue("title", "Software Engineering Lead")
    assertThat(result[0]).hasFieldOrPropertyWithValue("company", "Upp Technologies")
    assertThat(result[0]).hasFieldOrPropertyWithValue("companyUrl", "https://upp.ai")
    assertThat(result[0]).hasFieldOrPropertyWithValue("startDate", LocalDate.parse("2019-04-15"))
    assertThat(result[0]).hasFieldOrPropertyWithValue("endDate", LocalDate.parse("2020-05-01"))
    assertThat(result[0]).hasFieldOrPropertyWithValue("includeOnResume", true)
    assertThat(result[0]).hasFieldOrPropertyWithValue("education", false)
  }

  @Test
  fun `should return all skills groups from cms`() = runBlocking<Unit> {
    val result = cmsRestApi.getAllSkillsGroups()
    assertThat(result).hasSize(9)
    assertThat(result[0]).hasFieldOrPropertyWithValue("name", "Java / Kotlin")
    assertThat(result[0]).hasFieldOrPropertyWithValue("rating", 9.2)
    assertThat(result[0].skills).hasSize(3)
  }


}

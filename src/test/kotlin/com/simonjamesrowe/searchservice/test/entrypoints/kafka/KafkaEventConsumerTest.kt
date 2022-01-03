package com.simonjamesrowe.searchservice.test.entrypoints.kafka

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.simonjamesrowe.model.cms.dto.*
import com.simonjamesrowe.searchservice.core.model.IndexBlogRequest
import com.simonjamesrowe.searchservice.core.model.IndexSiteRequest
import com.simonjamesrowe.searchservice.core.usecase.IndexBlogUseCase
import com.simonjamesrowe.searchservice.core.usecase.IndexSiteUseCase
import com.simonjamesrowe.searchservice.dataproviders.cms.ICmsRestApi
import com.simonjamesrowe.searchservice.entrypoints.kafka.KafkaEventConsumer
import com.simonjamesrowe.searchservice.mapper.BlogMapper
import com.simonjamesrowe.searchservice.mapper.JobMapper
import com.simonjamesrowe.searchservice.mapper.SkillsGroupMapper
import com.simonjamesrowe.searchservice.test.TestUtils.image
import com.simonjamesrowe.searchservice.test.TestUtils.randomObject
import io.mockk.*
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
internal class KafkaEventConsumerTest {

  @RelaxedMockK
  private lateinit var cmsRestApi: ICmsRestApi

  @RelaxedMockK
  private lateinit var indexBlogUseCase: IndexBlogUseCase

  @RelaxedMockK
  private lateinit var indexSiteUseCase: IndexSiteUseCase

  private val objectMapper = jacksonObjectMapper().findAndRegisterModules()

  private lateinit var kafkaEventConsumer: KafkaEventConsumer

  @BeforeEach
  fun beforeEach() {
    mockkObject(BlogMapper)
    mockkObject(JobMapper)
    mockkObject(SkillsGroupMapper)
    kafkaEventConsumer = KafkaEventConsumer(indexBlogUseCase, indexSiteUseCase, objectMapper, cmsRestApi)
  }

  @AfterEach
  fun afterEach() {
    clearAllMocks()
    unmockkAll()
  }

  @Test
  fun `consuming webhook blog events will index blogs and site`() = runBlocking<Unit> {
    val blog1 = randomObject<BlogResponseDTO>(mapOf("image" to image("blog1", 200)))
    val blog2 = randomObject<BlogResponseDTO>(mapOf("image" to image("blog2", 200)))
    val webhookEvent1 = randomObject<WebhookEventDTO>(
      mapOf(
        "model" to "blog",
        "entry" to objectMapper.convertValue(blog1, JsonNode::class.java)
      )
    )
    val webhookEvent2 = randomObject<WebhookEventDTO>(
      mapOf(
        "model" to "blog",
        "entry" to objectMapper.convertValue(blog2, JsonNode::class.java)
      )
    )
    val webhookEvents = listOf(webhookEvent1, webhookEvent2)
    val indexBlogRequest1 = randomObject<IndexBlogRequest>()
    val indexBlogRequest2 = randomObject<IndexBlogRequest>()

    every { BlogMapper.toBlogIndexRequest(any()) } returnsMany listOf(indexBlogRequest1, indexBlogRequest2)
    val siteIndexRequest1 = randomObject<IndexSiteRequest>()
    val siteIndexRequest2 = randomObject<IndexSiteRequest>()

    every { BlogMapper.toSiteIndexRequest(any()) } returnsMany listOf(siteIndexRequest1, siteIndexRequest2)
    kafkaEventConsumer.consumeEvents(webhookEvents)

    verifyOrder {
      indexBlogUseCase.indexBlogs(listOf(indexBlogRequest1, indexBlogRequest2))
      indexSiteUseCase.indexSites(listOf(siteIndexRequest1, siteIndexRequest2))
    }
  }

  @Test
  fun `consuming webhook job events will site`() = runBlocking {
    val job1 = randomObject<JobResponseDTO>(mapOf("companyImage" to image("blog1", 200)))
    val job2 = randomObject<JobResponseDTO>(mapOf("companyImage" to image("blog2", 200)))
    val webhookEvent1 = randomObject<WebhookEventDTO>(
      mapOf(
        "model" to "job",
        "entry" to objectMapper.convertValue(job1, JsonNode::class.java)
      )
    )
    val webhookEvent2 = randomObject<WebhookEventDTO>(
      mapOf(
        "model" to "job",
        "entry" to objectMapper.convertValue(job2, JsonNode::class.java)
      )
    )
    val siteIndexRequest1 = randomObject<IndexSiteRequest>()
    val siteIndexRequest2 = randomObject<IndexSiteRequest>()

    every { JobMapper.toIndexSiteRequest(any()) } returnsMany listOf(siteIndexRequest1, siteIndexRequest2)
    kafkaEventConsumer.consumeEvents(listOf(webhookEvent1, webhookEvent2))

    verifyOrder {
      indexSiteUseCase.indexSites(listOf(siteIndexRequest1, siteIndexRequest2))
    }
  }

  @Test
  fun `consuming webhook skill events will site`() = runBlocking<Unit> {
    val skill1 = randomObject<SkillResponseDTO>(mapOf("image" to image("blog1", 200)))
    val skill2 = randomObject<SkillResponseDTO>(mapOf("image" to image("blog2", 200)))
    val webhookEvent1 = randomObject<WebhookEventDTO>(
      mapOf(
        "model" to "skills",
        "entry" to objectMapper.convertValue(skill1, JsonNode::class.java)
      )
    )
    val webhookEvent2 = randomObject<WebhookEventDTO>(
      mapOf(
        "model" to "skills",
        "entry" to objectMapper.convertValue(skill1, JsonNode::class.java)
      )
    )
    val skillsGroup = randomObject<SkillsGroupResponseDTO>(
      mapOf(
        "skills" to listOf(skill1, skill2),
        "image" to image("skillGroup", 300)
      )
    )
    coEvery { cmsRestApi.getAllSkillsGroups() } returns listOf(skillsGroup)

    val siteIndexRequest1 = randomObject<IndexSiteRequest>()
    val siteIndexRequest2 = randomObject<IndexSiteRequest>()

    every { SkillsGroupMapper.toSiteIndexRequests(skillsGroup) } returns listOf(siteIndexRequest1, siteIndexRequest2)
    kafkaEventConsumer.consumeEvents(listOf(webhookEvent1, webhookEvent2))

    verify {
      indexSiteUseCase.indexSites(listOf(siteIndexRequest1, siteIndexRequest2))
    }
  }

}

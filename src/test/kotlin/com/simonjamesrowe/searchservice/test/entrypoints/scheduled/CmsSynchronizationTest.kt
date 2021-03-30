package com.simonjamesrowe.searchservice.test.entrypoints.scheduled

import com.simonjamesrowe.model.cms.dto.*
import com.simonjamesrowe.searchservice.test.TestUtils
import com.simonjamesrowe.searchservice.core.model.IndexBlogRequest
import com.simonjamesrowe.searchservice.core.model.IndexSiteRequest
import com.simonjamesrowe.searchservice.core.usecase.IndexBlogUseCase
import com.simonjamesrowe.searchservice.core.usecase.IndexSiteUseCase
import com.simonjamesrowe.searchservice.dataproviders.cms.CmsRestApi
import com.simonjamesrowe.searchservice.entrypoints.scheduled.CmsSynchronization
import com.simonjamesrowe.searchservice.mapper.BlogMapper
import com.simonjamesrowe.searchservice.mapper.JobMapper
import com.simonjamesrowe.searchservice.mapper.SkillsGroupMapper
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.core.env.Environment
import org.springframework.core.env.Profiles

@ExtendWith(MockKExtension::class)
internal class CmsSynchronizationTest {

  @RelaxedMockK
  private lateinit var cmsRestApi: CmsRestApi

  @RelaxedMockK
  private lateinit var indexSiteUseCase: IndexSiteUseCase

  @RelaxedMockK
  private lateinit var indexBlogUseCase: IndexBlogUseCase

  @RelaxedMockK
  private lateinit var environment: Environment

  @InjectMockKs
  private lateinit var cmsSynchronization: CmsSynchronization

  @BeforeEach
  fun beforeEach() {
    mockkObject(BlogMapper)
    mockkObject(JobMapper)
    mockkObject(SkillsGroupMapper)
    every { environment.acceptsProfiles(Profiles.of("cloud"))} returns true
  }

  @AfterEach
  fun afterEach() {
    clearAllMocks()
    unmockkAll()
  }

  @Test
  fun `should index blog documents`() = runBlocking<Unit>{
    val blog1 = TestUtils.randomObject<BlogResponseDTO>(mapOf("image" to TestUtils.image("blog1", 200)))
    val blog2 = TestUtils.randomObject<BlogResponseDTO>(mapOf("image" to TestUtils.image("blog2", 200)))

    coEvery { cmsRestApi.getAllBlogs() } returns listOf(blog1, blog2)

    val indexBlogRequest1 = TestUtils.randomObject<IndexBlogRequest>()
    val indexBlogRequest2 = TestUtils.randomObject<IndexBlogRequest>()

    every { BlogMapper.toBlogIndexRequest(any()) } returnsMany listOf(indexBlogRequest1, indexBlogRequest2)

    cmsSynchronization.syncBlogDocuments().join()

    verifyOrder {
      indexBlogUseCase.indexBlogs(listOf(indexBlogRequest1, indexBlogRequest2))
    }
  }

  @Test
  fun `should index site documents`() = runBlocking{

    val blog1 = TestUtils.randomObject<BlogResponseDTO>(mapOf("image" to TestUtils.image("blog1", 200)))
    val blog2 = TestUtils.randomObject<BlogResponseDTO>(mapOf("image" to TestUtils.image("blog2", 200)))
    coEvery { cmsRestApi.getAllBlogs() } returns listOf(blog1, blog2)

    val siteIndexRequest1 = TestUtils.randomObject<IndexSiteRequest>()
    val siteIndexRequest2 = TestUtils.randomObject<IndexSiteRequest>()
    every { BlogMapper.toSiteIndexRequest(any()) } returnsMany listOf(siteIndexRequest1, siteIndexRequest2)

    val job1 = TestUtils.randomObject<JobResponseDTO>(mapOf("companyImage" to TestUtils.image("blog1", 200)))
    val job2 = TestUtils.randomObject<JobResponseDTO>(mapOf("companyImage" to TestUtils.image("blog2", 200)))
    coEvery { cmsRestApi.getAllJobs() } returns listOf(job1, job2)

    val siteIndexRequest3 = TestUtils.randomObject<IndexSiteRequest>()
    val siteIndexRequest4 = TestUtils.randomObject<IndexSiteRequest>()
    every { JobMapper.toIndexSiteRequest(any()) } returnsMany listOf(siteIndexRequest3, siteIndexRequest4)

    val skill1 = TestUtils.randomObject<SkillResponseDTO>(mapOf("image" to TestUtils.image("blog1", 200)))
    val skill2 = TestUtils.randomObject<SkillResponseDTO>(mapOf("image" to TestUtils.image("blog2", 200)))
    val skill3 = TestUtils.randomObject<SkillResponseDTO>(mapOf("image" to TestUtils.image("blog2", 200)))
    val skillsGroup1 = TestUtils.randomObject<SkillsGroupResponseDTO>(
      mapOf(
        "skills" to listOf(skill1, skill2),
        "image" to TestUtils.image("skillGroup1", 300)
      )
    )
    val skillsGroup2 = TestUtils.randomObject<SkillsGroupResponseDTO>(
      mapOf(
        "skills" to listOf(skill3),
        "image" to TestUtils.image("skillGroup2", 300)
      )
    )
    coEvery { cmsRestApi.getAllSkillsGroups() } returns listOf(skillsGroup1, skillsGroup2)
    val siteIndexRequest5 = TestUtils.randomObject<IndexSiteRequest>()
    val siteIndexRequest6 = TestUtils.randomObject<IndexSiteRequest>()
    val siteIndexRequest7 = TestUtils.randomObject<IndexSiteRequest>()
    every { SkillsGroupMapper.toSiteIndexRequests(any()) } returnsMany listOf(
      listOf(
        siteIndexRequest5,
        siteIndexRequest6
      ), listOf(siteIndexRequest7)
    )

    cmsSynchronization.syncSiteDocuments().join()

    verifyOrder {
      indexSiteUseCase.indexSites(
        listOf(
          siteIndexRequest1,
          siteIndexRequest2,
          siteIndexRequest3,
          siteIndexRequest4,
          siteIndexRequest5,
          siteIndexRequest6,
          siteIndexRequest7
        )
      )
    }

  }
}

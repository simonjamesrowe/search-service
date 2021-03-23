package com.simonjamesrowe.searchservice.entrypoints.scheduled

import com.simonjamesrowe.searchservice.core.model.IndexSiteRequest
import com.simonjamesrowe.searchservice.core.usecase.IndexBlogUseCase
import com.simonjamesrowe.searchservice.core.usecase.IndexSiteUseCase
import com.simonjamesrowe.searchservice.dataproviders.cms.CmsRestApi
import com.simonjamesrowe.searchservice.mapper.BlogMapper
import com.simonjamesrowe.searchservice.mapper.BlogMapper.toBlogIndexRequest
import com.simonjamesrowe.searchservice.mapper.JobMapper
import com.simonjamesrowe.searchservice.mapper.SkillsGroupMapper
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Profile
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Profile("cloud")
@Service
class CmsSynchronization(
  private val cmsRestApi: CmsRestApi,
  private val indexBlogUseCase: IndexBlogUseCase,
  private val indexSiteUseCase: IndexSiteUseCase
) {

  companion object {
    val log = LoggerFactory.getLogger(CmsSynchronization::class.java)
    const val ONE_MINUTE: Long = 60 * 1000
    const val FOUR_HOURS: Long = 4 * 60 * 60 * 1000
  }

  @Scheduled(initialDelay = ONE_MINUTE, fixedDelay = FOUR_HOURS)
  fun syncBlogDocuments() {
    log.info("Synchronising blog documents from cms")
    val allBlogs = cmsRestApi.getAllBlogs()
    indexBlogUseCase.indexBlogs(allBlogs.map(::toBlogIndexRequest))
  }

  @Scheduled(initialDelay = ONE_MINUTE, fixedDelay = FOUR_HOURS)
  fun syncSiteDocuments() {
    log.info("Synchronising site documents from cms")
    val allBlogs = cmsRestApi.getAllBlogs()
    val allJobs = cmsRestApi.getAllJobs()
    val allSkillsGroupStore = cmsRestApi.getAllSkillsGroups()
    val allSiteDocuments = mutableListOf<IndexSiteRequest>()
    allSiteDocuments.addAll(allBlogs.map { BlogMapper.toSiteIndexRequest(it) })
    allSiteDocuments.addAll(allJobs.map { JobMapper.toIndexSiteRequest(it) })
    allSkillsGroupStore.forEach { skillsGroup ->
      allSiteDocuments.addAll(SkillsGroupMapper.toSiteIndexRequests(skillsGroup))
    }
    log.info("Indexing ${allSiteDocuments.size} site documents")
    indexSiteUseCase.indexSites(allSiteDocuments)
  }

}
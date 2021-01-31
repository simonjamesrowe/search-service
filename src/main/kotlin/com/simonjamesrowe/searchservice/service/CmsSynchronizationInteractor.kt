package com.simonjamesrowe.searchservice.service

import com.simonjamesrowe.searchservice.adapter.CmsRestApi
import com.simonjamesrowe.searchservice.dao.BlogSearchRepository
import com.simonjamesrowe.searchservice.dao.SiteDocumentRepository
import com.simonjamesrowe.searchservice.document.SiteDocument
import com.simonjamesrowe.searchservice.mapper.BlogMapper
import com.simonjamesrowe.searchservice.mapper.JobMapper
import com.simonjamesrowe.searchservice.mapper.SkillsGroupMapper
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Profile
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Profile("cloud")
@Service
class CmsSynchronizationInteractor(
  private val cmsRestApi: CmsRestApi,
  private val blogSearchRepository: BlogSearchRepository,
  private val siteDocumentRepository: SiteDocumentRepository
) {

  companion object {
    val log = LoggerFactory.getLogger(CmsSynchronizationInteractor::class.java)
    const val ONE_MINUTE: Long = 60 * 1000
    const val FOUR_HOURS: Long = 4 * 60 * 60 * 1000
  }

  @Scheduled(initialDelay = ONE_MINUTE, fixedDelay = FOUR_HOURS)
  fun syncBlogDocuments() {
    log.info("Synchronising blog documents from cms")
    val allBlogs = cmsRestApi.getAllBlogs()
    blogSearchRepository.saveAll(allBlogs)
  }

  @Scheduled(initialDelay = ONE_MINUTE, fixedDelay = FOUR_HOURS)
  fun syncSiteDocuments() {
    log.info("Synchronising site documents from cms")
    val allBlogs = cmsRestApi.getAllBlogs()
    val allJobs = cmsRestApi.getAllJobs()
    val allSkillsGroupStore = cmsRestApi.getAllSkillsGroups()
    val allSiteDocuments = mutableListOf<SiteDocument>()
    allSiteDocuments.addAll(allBlogs.map{BlogMapper.toSiteDocument(it)})
    allSiteDocuments.addAll(allJobs.map { JobMapper.toSiteDocument(it)})
    allSkillsGroupStore.forEach{skillsGroup ->
      allSiteDocuments.addAll(SkillsGroupMapper.toSiteDocuments(skillsGroup))
    }
    log.info("Indexing ${allSiteDocuments.size} site documents")
    siteDocumentRepository.saveAll(allSiteDocuments)
  }

}

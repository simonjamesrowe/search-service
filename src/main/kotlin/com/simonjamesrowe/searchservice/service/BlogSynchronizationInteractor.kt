package com.simonjamesrowe.searchservice.service

import com.simonjamesrowe.searchservice.adaptor.BlogRestApi
import com.simonjamesrowe.searchservice.dao.BlogDocumentRepository
import com.simonjamesrowe.searchservice.mapper.BlogMapper
import org.springframework.context.annotation.Profile
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Profile("cloud")
@Service
class BlogSynchronizationInteractor(
  private val blogRestApi: BlogRestApi,
  private val blogDocumentRepository: BlogDocumentRepository
) {

  companion object {
    const val ONE_MINUTE: Long = 60 * 1000
    const val FOUR_HOURS: Long = 4 * 60 * 60 * 1000
  }

  @Scheduled(initialDelay = ONE_MINUTE, fixedDelay = FOUR_HOURS)
  fun syncBlogDocuments() {
    blogRestApi
      .getAllBlogs()
      .filter { it.published }
      .map(BlogMapper::map)
      .run {
        if (isNotEmpty()) {
          blogDocumentRepository.saveAll(this)
        }
      }
  }

}
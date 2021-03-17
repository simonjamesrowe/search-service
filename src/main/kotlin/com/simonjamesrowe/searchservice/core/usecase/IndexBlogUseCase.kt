package com.simonjamesrowe.searchservice.core.usecase

import com.simonjamesrowe.searchservice.core.model.IndexBlogRequest
import com.simonjamesrowe.searchservice.core.repository.BlogIndexRepository
import org.springframework.stereotype.Service

@Service
class IndexBlogUseCase(
  private val blogIndexRepository: BlogIndexRepository
) {

  fun indexBlogs(indexBlogRequests: Collection<IndexBlogRequest>) {
    if (indexBlogRequests.any { it.published })
      blogIndexRepository.indexBlogs(indexBlogRequests.filter { it.published })
    if (indexBlogRequests.any { !it.published })
      blogIndexRepository.deleteBlogs(indexBlogRequests.filter { !it.published }.map { it.id })
  }

}

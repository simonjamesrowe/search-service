package com.simonjamesrowe.searchservice.core.usecase

import com.simonjamesrowe.searchservice.core.repository.BlogSearchRepository
import org.springframework.stereotype.Service

@Service
class SearchBlogsUseCase(
  private val blogSearchRepository: BlogSearchRepository
) {
  suspend fun search(q: String) = blogSearchRepository.search(q)
  suspend fun getAll() = blogSearchRepository.getAll()
}

package com.simonjamesrowe.searchservice.core.usecase

import com.simonjamesrowe.searchservice.core.repository.BlogSearchRepository
import org.springframework.stereotype.Service

@Service
class SearchBlogsUseCase(
  private val blogSearchRepository: BlogSearchRepository
) {
  fun search(q: String) = blogSearchRepository.search(q)
  fun getAll() = blogSearchRepository.getAll()
}

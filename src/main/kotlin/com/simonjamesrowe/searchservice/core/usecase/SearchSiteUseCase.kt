package com.simonjamesrowe.searchservice.core.usecase

import com.simonjamesrowe.searchservice.core.repository.SiteSearchRepository
import org.springframework.stereotype.Service

@Service
class SearchSiteUseCase(
  private val siteSearchRepository: SiteSearchRepository
) {
  suspend fun search(q: String) = siteSearchRepository.search(q)
  suspend fun getAll() = siteSearchRepository.getAll()
}

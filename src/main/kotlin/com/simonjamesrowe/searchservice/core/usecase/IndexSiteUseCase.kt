package com.simonjamesrowe.searchservice.core.usecase

import com.simonjamesrowe.searchservice.core.model.IndexSiteRequest
import com.simonjamesrowe.searchservice.core.repository.SiteIndexRepository
import org.springframework.stereotype.Service

@Service
class IndexSiteUseCase(
  private val siteIndexRepository: SiteIndexRepository
) {

  fun indexSites(requests: Collection<IndexSiteRequest>) =
    siteIndexRepository.indexSites(requests)

}

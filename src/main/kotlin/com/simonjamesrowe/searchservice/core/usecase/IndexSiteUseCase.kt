package com.simonjamesrowe.searchservice.core.usecase

import com.simonjamesrowe.searchservice.core.model.IndexSiteRequest
import com.simonjamesrowe.searchservice.dataproviders.elasticsearch.site.SiteSearchRepository
import org.springframework.stereotype.Service

@Service
class IndexSiteUseCase(
  private val siteSearchRepository: SiteSearchRepository
) {

  fun indexSites(requests: Collection<IndexSiteRequest>) =
    siteSearchRepository.indexSites(requests)

}

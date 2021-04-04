package com.simonjamesrowe.searchservice.core.repository

import com.simonjamesrowe.searchservice.core.model.IndexSiteRequest
import org.springframework.cloud.sleuth.annotation.NewSpan

interface


SiteIndexRepository {
  @NewSpan("indexSites")
  fun indexSites(requests: Collection<IndexSiteRequest>)
}

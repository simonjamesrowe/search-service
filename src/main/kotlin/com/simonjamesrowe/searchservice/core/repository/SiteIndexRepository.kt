package com.simonjamesrowe.searchservice.core.repository

import com.simonjamesrowe.searchservice.core.model.IndexSiteRequest

interface SiteIndexRepository {
  fun indexSites(requests: Collection<IndexSiteRequest>)
}

package com.simonjamesrowe.searchservice.core.repository

import com.simonjamesrowe.searchservice.core.model.SiteSearchResult

interface SiteSearchRepository {
  suspend fun search(q: String) : List<SiteSearchResult>
  suspend fun getAll(): List<SiteSearchResult>
}

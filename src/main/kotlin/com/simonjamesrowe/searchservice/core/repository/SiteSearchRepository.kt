package com.simonjamesrowe.searchservice.core.repository

import com.simonjamesrowe.searchservice.core.model.SiteSearchResult

interface SiteSearchRepository {
  fun search(q: String) : List<SiteSearchResult>
  fun getAll(): List<SiteSearchResult>
}

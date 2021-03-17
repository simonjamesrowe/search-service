package com.simonjamesrowe.searchservice.core.repository

import com.simonjamesrowe.searchservice.core.model.BlogSearchResult

interface BlogSearchRepository {
  fun search(q: String) : Collection<BlogSearchResult>
  fun getAll() : Collection<BlogSearchResult>
}

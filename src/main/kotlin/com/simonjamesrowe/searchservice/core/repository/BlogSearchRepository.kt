package com.simonjamesrowe.searchservice.core.repository

import com.simonjamesrowe.searchservice.core.model.BlogSearchResult

interface BlogSearchRepository {
  suspend fun search(q: String): Collection<BlogSearchResult>
  suspend fun getAll(): Collection<BlogSearchResult>
}

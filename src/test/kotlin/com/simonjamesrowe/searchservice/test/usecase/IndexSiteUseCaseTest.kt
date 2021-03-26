package com.simonjamesrowe.searchservice.test.usecase

import com.simonjamesrowe.searchservice.test.TestUtils.randomObject
import com.simonjamesrowe.searchservice.core.model.IndexSiteRequest
import com.simonjamesrowe.searchservice.core.usecase.IndexSiteUseCase
import com.simonjamesrowe.searchservice.dataproviders.elasticsearch.site.SiteSearchRepository
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
internal class IndexSiteUseCaseTest {

  @RelaxedMockK
  private lateinit var siteSearchRepository: SiteSearchRepository

  @InjectMockKs
  private lateinit var indexSiteUseCase: IndexSiteUseCase

  @Test
  fun `should index site requests`() {
    val indexSiteRequest1 = randomObject<IndexSiteRequest>()
    val indexSiteRequest2 = randomObject<IndexSiteRequest>()

    indexSiteUseCase.indexSites(listOf(indexSiteRequest1, indexSiteRequest2))

    verify { siteSearchRepository.indexSites(listOf(indexSiteRequest1, indexSiteRequest2)) }
  }
}

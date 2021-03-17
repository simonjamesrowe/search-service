package com.simonjamesrowe.searchservice.core.usecase

import com.simonjamesrowe.searchservice.TestUtils.randomObject
import com.simonjamesrowe.searchservice.core.model.SiteSearchResult
import com.simonjamesrowe.searchservice.core.repository.SiteSearchRepository
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
internal class SearchSiteUseCaseTest {

  @InjectMockKs
  private lateinit var searchSiteUseCase: SearchSiteUseCase

  @RelaxedMockK
  private lateinit var siteSearchRepository: SiteSearchRepository

  @Test
  fun `should return site search results`() {
    val result1 = randomObject<SiteSearchResult>(mapOf("type" to "Job"))
    val result2 = randomObject<SiteSearchResult>(mapOf("type" to "Blog"))

    every { siteSearchRepository.search("kotlin") } returns listOf(result1, result2)

    assertThat(searchSiteUseCase.search("kotlin")).isEqualTo(listOf(result1, result2))
  }
}

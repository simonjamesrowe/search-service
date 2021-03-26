package com.simonjamesrowe.searchservice.test.usecase

import com.simonjamesrowe.searchservice.test.TestUtils.randomObject
import com.simonjamesrowe.searchservice.core.model.SiteSearchResult
import com.simonjamesrowe.searchservice.core.repository.SiteSearchRepository
import com.simonjamesrowe.searchservice.core.usecase.SearchSiteUseCase
import io.mockk.coEvery
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.runBlocking
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
  fun `should return site search results`() = runBlocking<Unit>{
    val result1 = randomObject<SiteSearchResult>(mapOf("type" to "Job"))
    val result2 = randomObject<SiteSearchResult>(mapOf("type" to "Blog"))

    coEvery { siteSearchRepository.search("kotlin") } returns listOf(result1, result2)

    assertThat(searchSiteUseCase.search("kotlin")).isEqualTo(listOf(result1, result2))
  }
}

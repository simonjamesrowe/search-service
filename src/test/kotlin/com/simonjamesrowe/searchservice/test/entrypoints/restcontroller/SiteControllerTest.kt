package com.simonjamesrowe.searchservice.test.entrypoints.restcontroller

import com.simonjamesrowe.searchservice.core.model.SiteSearchResult
import com.simonjamesrowe.searchservice.core.usecase.SearchBlogsUseCase
import com.simonjamesrowe.searchservice.core.usecase.SearchSiteUseCase
import com.simonjamesrowe.searchservice.entrypoints.restcontroller.SiteController
import com.tyro.oss.arbitrater.arbitraryInstance
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockkClass
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient

@WebFluxTest(controllers = [SiteController::class])
@Import(SiteControllerTest.MockBeanConfiguration::class)
internal class SiteControllerTest {

  @Autowired
  lateinit var searchSiteUseCase: SearchSiteUseCase

  @Autowired
  private lateinit var webClient: WebTestClient

  @Test
  fun `search results should be correct`() {
    val searchResults = listOf(
      SiteSearchResult::class.arbitraryInstance(),
      SiteSearchResult::class.arbitraryInstance(),
    )
    coEvery { searchSiteUseCase.search("Universal") } returns searchResults

    webClient.get().uri("/site?q={q}", "Universal").accept(MediaType.APPLICATION_JSON)
      .exchange()
      .expectStatus().isOk
      .expectBody()
      .jsonPath("$.length()").isEqualTo(2)
      .jsonPath("$[0].type").isEqualTo(searchResults[0].type)
      .jsonPath("$[0].hits.length()").isEqualTo(searchResults[0].hits.size)
      .jsonPath("$[0].hits[0].imageUrl").isEqualTo(searchResults[0].hits[0].imageUrl)
      .jsonPath("$[0].hits[0].link").isEqualTo(searchResults[0].hits[0].link)
      .jsonPath("$[0].hits[0].name").isEqualTo(searchResults[0].hits[0].name)

    coVerify { searchSiteUseCase.search("Universal") }
  }

  class MockBeanConfiguration {

    @Bean
    fun searchSiteUseCase() = mockkClass(SearchSiteUseCase::class);

  }
}

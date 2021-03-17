package com.simonjamesrowe.searchservice.entrypoints.restcontroller

import com.ninjasquad.springmockk.MockkBean
import com.simonjamesrowe.searchservice.core.model.SiteSearchResult
import com.simonjamesrowe.searchservice.core.usecase.SearchSiteUseCase
import com.tyro.oss.arbitrater.arbitraryInstance
import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient

@WebFluxTest(controllers = [SiteController::class])
internal class SiteControllerTest {

  @MockkBean
  lateinit var searchSiteUseCase: SearchSiteUseCase

  @Autowired
  private lateinit var webClient: WebTestClient

  @Test
  fun `search results should be correct`() {
    val searchResults = listOf(
      SiteSearchResult::class.arbitraryInstance(),
      SiteSearchResult::class.arbitraryInstance(),
    )
    every { searchSiteUseCase.search("Universal") } returns searchResults

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

    verify { searchSiteUseCase.search("Universal") }
  }
}

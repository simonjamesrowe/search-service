package com.simonjamesrowe.searchservice.test.entrypoints.restcontroller

import com.ninjasquad.springmockk.MockkBean
import com.simonjamesrowe.searchservice.core.model.BlogSearchResult
import com.simonjamesrowe.searchservice.core.usecase.SearchBlogsUseCase
import com.simonjamesrowe.searchservice.entrypoints.restcontroller.BlogController
import com.tyro.oss.arbitrater.arbitraryInstance
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import java.time.format.DateTimeFormatter


@WebFluxTest(controllers = [BlogController::class])
internal class BlogControllerTest {

  @MockkBean
  lateinit var searchBlogsUseCase: SearchBlogsUseCase

  @Autowired
  private lateinit var webClient: WebTestClient

  @Test
  fun `search should return expected results`() {
    val searchResults = listOf(
      BlogSearchResult::class.arbitraryInstance(),
      BlogSearchResult::class.arbitraryInstance(),
      BlogSearchResult::class.arbitraryInstance()
    )
    coEvery { searchBlogsUseCase.search("kotlin") } returns searchResults

    webClient.get().uri("/blogs?q={q}", "kotlin").accept(MediaType.APPLICATION_JSON)
      .exchange()
      .expectStatus().isOk
      .expectBody()
      .jsonPath("$.length()").isEqualTo(3)
      .jsonPath("$[0].id").isEqualTo(searchResults[0].id)
      .jsonPath("$[0].createdDate").isEqualTo(searchResults[0].createdDate.format(DateTimeFormatter.ISO_DATE))
      .jsonPath("$[0].shortDescription").isEqualTo(searchResults[0].shortDescription)
      .jsonPath("$[0].thumbnailImage").isEqualTo(searchResults[0].thumbnailImage)
      .jsonPath("$[0].title").isEqualTo(searchResults[0].title)

    coVerify { searchBlogsUseCase.search("kotlin") }
  }

  @Test
  fun `get all should return expected results`() {
    val searchResults = listOf(
      BlogSearchResult::class.arbitraryInstance(),
      BlogSearchResult::class.arbitraryInstance(),
      BlogSearchResult::class.arbitraryInstance()
    )
    coEvery { searchBlogsUseCase.getAll() } returns searchResults

    webClient.get().uri("/blogs").accept(MediaType.APPLICATION_JSON)
      .exchange()
      .expectStatus().isOk
      .expectBody()
      .jsonPath("$.length()").isEqualTo(3)
      .jsonPath("$[0].id").isEqualTo(searchResults[0].id)
      .jsonPath("$[0].createdDate").isEqualTo(searchResults[0].createdDate.format(DateTimeFormatter.ISO_DATE))
      .jsonPath("$[0].shortDescription").isEqualTo(searchResults[0].shortDescription)
      .jsonPath("$[0].thumbnailImage").isEqualTo(searchResults[0].thumbnailImage)
      .jsonPath("$[0].title").isEqualTo(searchResults[0].title)

    coVerify { searchBlogsUseCase.getAll() }
  }

}

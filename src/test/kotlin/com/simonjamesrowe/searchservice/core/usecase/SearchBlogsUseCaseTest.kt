package com.simonjamesrowe.searchservice.core.usecase

import com.simonjamesrowe.searchservice.TestUtils.randomObject
import com.simonjamesrowe.searchservice.core.model.BlogSearchResult
import com.simonjamesrowe.searchservice.core.repository.BlogSearchRepository
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
internal class SearchBlogsUseCaseTest {
  @InjectMockKs
  private lateinit var searchBlogUseCase: SearchBlogsUseCase

  @RelaxedMockK
  private lateinit var blogSearchRepository: BlogSearchRepository

  @Test
  fun `should search for blogs with query`() {
    val blogSearchResult1 = randomObject<BlogSearchResult>()
    val blogSearchResult2 = randomObject<BlogSearchResult>()

    every { blogSearchRepository.search("kotlin") } returns listOf(blogSearchResult1, blogSearchResult2)

    assertThat(searchBlogUseCase.search("kotlin")).isEqualTo(listOf(blogSearchResult1, blogSearchResult2))
  }

  @Test
  fun `should return all blogs`() {
    val blogSearchResult1 = randomObject<BlogSearchResult>()
    val blogSearchResult2 = randomObject<BlogSearchResult>()

    every { blogSearchRepository.getAll() } returns listOf(blogSearchResult1, blogSearchResult2)

    assertThat(searchBlogUseCase.getAll()).isEqualTo(listOf(blogSearchResult1, blogSearchResult2))
  }
}

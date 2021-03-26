package com.simonjamesrowe.searchservice.test.usecase

import com.simonjamesrowe.searchservice.test.TestUtils.randomObject
import com.simonjamesrowe.searchservice.core.model.IndexBlogRequest
import com.simonjamesrowe.searchservice.core.repository.BlogIndexRepository
import com.simonjamesrowe.searchservice.core.usecase.IndexBlogUseCase
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
internal class IndexBlogUseCaseTest {

  @RelaxedMockK
  private lateinit var blogIndexRepository: BlogIndexRepository

  @InjectMockKs
  private lateinit var indexBlogUseCase: IndexBlogUseCase

  @Test
  fun `should index published blogs`() {
    val indexBlogRequest1 = randomObject<IndexBlogRequest>(mapOf("published" to true))
    val indexBlogRequest2 = randomObject<IndexBlogRequest>(mapOf("published" to true))

    indexBlogUseCase.indexBlogs(listOf(indexBlogRequest1, indexBlogRequest2))

    verify { blogIndexRepository.indexBlogs(listOf(indexBlogRequest1, indexBlogRequest2)) }
    verify(exactly = 0) { blogIndexRepository.deleteBlogs(any()) }
  }

  @Test
  fun `should delete blogs that are not published`() {
    val indexBlogRequest1 = randomObject<IndexBlogRequest>(mapOf("published" to false))
    val indexBlogRequest2 = randomObject<IndexBlogRequest>(mapOf("published" to false))

    indexBlogUseCase.indexBlogs(listOf(indexBlogRequest1, indexBlogRequest2))

    verify(exactly = 0) { blogIndexRepository.indexBlogs(any()) }
    verify { blogIndexRepository.deleteBlogs(listOf(indexBlogRequest1.id, indexBlogRequest2.id)) }

  }
}

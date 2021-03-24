package com.simonjamesrowe.searchservice.dataproviders.elasticsearch.blog

import com.simonjamesrowe.component.test.TestContainersExtension
import com.simonjamesrowe.component.test.elasticsearch.WithElasticsearchContainer
import com.simonjamesrowe.searchservice.config.ElasticSearchConfig
import com.simonjamesrowe.searchservice.config.ElasticSearchIndexProperties
import com.simonjamesrowe.searchservice.core.model.BlogSearchResult
import com.simonjamesrowe.searchservice.core.model.IndexBlogRequest
import com.tyro.oss.arbitrater.arbitrary
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.ImportAutoConfiguration
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchDataAutoConfiguration
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchRepositoriesAutoConfiguration
import org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchRestClientAutoConfiguration
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.test.autoconfigure.json.JsonTest
import org.springframework.context.annotation.Import
import java.time.LocalDate


@JsonTest
@ExtendWith(TestContainersExtension::class)
@WithElasticsearchContainer
@ImportAutoConfiguration(
  ElasticsearchDataAutoConfiguration::class,
  ElasticsearchRepositoriesAutoConfiguration::class,
  ElasticsearchRestClientAutoConfiguration::class
)
@EnableConfigurationProperties(ElasticSearchIndexProperties::class)
@Import(BlogDocumentIndexConfig::class, BlogRepository::class, ElasticSearchConfig::class)
internal class BlogRepositoryTest {

  @Autowired
  private lateinit var blogRepository: BlogRepository

  @Autowired
  private lateinit var blogDocumentRepository: BlogDocumentRepository

  @BeforeEach
  fun setupData() {
    blogDocumentRepository.deleteAll()
    val blog1 = IndexBlogRequest(
      id = "1",
      title = "My first blog on kotlin",
      content = "This contains <b> test on </b> kotlin jvm",
      tags = listOf("kotlin", "jvm"),
      skills = listOf("kotlin", "spring"),
      createdDate = LocalDate.of(2021, 1, 1),
      mediumImage = "/uploads/blog1-med.jpg",
      smallImage = "/uploads/blog1-small.jpg",
      thumbnailImage = "/uploads/blog2-thumb.jpg",
      shortDescription = "Short description 1",
      published = false
    )
    val blog2 = IndexBlogRequest(
      id = "2",
      title = "Pact for contract testing",
      content = "This contains <b> test on </b> pact spring contract tests broker with kotlin",
      tags = listOf("pact", "jvm", "contract-testing"),
      skills = listOf("junit", "jenkins", "spring", "kotlin"),
      createdDate = LocalDate.of(2021, 1, 2),
      mediumImage = "/uploads/blog2-med.jpg",
      smallImage = "/uploads/blog2-small.jpg",
      thumbnailImage = "/uploads/blog2-thumb.jpg",
      shortDescription = "shortDescription2",
      published = true
    )
    val blog3 = IndexBlogRequest(
      id = "3",
      title = "Continuous Integration with Jenkins X",
      content = "Jenkins X is the best, uses helm and docker and kubernetes",
      tags = listOf("jenkins", "ci", "cd"),
      skills = listOf("helm", "jenkins", "kubernetes", "docker"),
      createdDate = LocalDate.of(2021, 1, 3),
      mediumImage = "/uploads/blog3-med.jpg",
      smallImage = "/uploads/blog3-small.jpg",
      thumbnailImage = "/uploads/blog3-thumb.jpg",
      shortDescription = "shortDescription3",
      published = true
    )
    blogRepository.indexBlogs(listOf(blog1, blog2, blog3))
  }

  @Test
  fun `should return all blogs`() = runBlocking<Unit> {
    assertThat(blogRepository.getAll()).hasSize(3)
  }

  @Test
  fun `should return correct search results`() = runBlocking<Unit> {
    assertThat(blogRepository.search("kotlin")).hasSize(2)
  }

  @Test
  fun `should delete document`() = runBlocking<Unit> {
    blogRepository.deleteBlog("2")
    assertThat(blogRepository.getAll()).hasSize(2)
  }

  @Test
  fun `should delete documents`() = runBlocking<Unit> {
    blogRepository.deleteBlogs(listOf("1", "3"))
    assertThat(blogRepository.getAll()).hasSize(1)
  }

  @Test
  fun `should convert to BlogDocument`() {
    val input: IndexBlogRequest = arbitrary()
    val expected = BlogDocument(
      id = input.id,
      title = input.title,
      content = input.content,
      tags = input.tags.map { it },
      skills = input.skills.map { it },
      thumbnailImage = input.thumbnailImage,
      smallImage = input.smallImage,
      mediumImage = input.mediumImage,
      createdDate = input.createdDate,
      shortDescription = input.shortDescription
    )
    assertThat(blogRepository.toBlogDocument(input)).isEqualTo(expected)
  }

  @Test
  fun `should convert to BlogSearchResult`() {
    val input: BlogDocument = arbitrary()
    val expected = BlogSearchResult(
      id = input.id,
      title = input.title,
      tags = input.tags.map { it },
      thumbnailImage = input.thumbnailImage,
      smallImage = input.smallImage,
      mediumImage = input.mediumImage,
      createdDate = input.createdDate,
      shortDescription = input.shortDescription
    )
    assertThat(blogRepository.toBlogSearchResult(input)).isEqualTo(expected)
  }

}

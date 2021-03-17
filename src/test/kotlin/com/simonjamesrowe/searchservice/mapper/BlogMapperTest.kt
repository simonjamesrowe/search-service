package com.simonjamesrowe.searchservice.mapper

import com.simonjamesrowe.model.cms.dto.BlogResponseDTO
import com.simonjamesrowe.searchservice.TestUtils.image
import com.simonjamesrowe.searchservice.TestUtils.randomObject
import com.simonjamesrowe.searchservice.core.model.IndexBlogRequest
import com.simonjamesrowe.searchservice.core.model.IndexSiteRequest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test


internal class BlogMapperTest {

  @Test
  fun `should convert toBlogIndexRequest`() {
    val input = randomObject<BlogResponseDTO>(mapOf("image" to image("test", 400)))
    val expected = IndexBlogRequest(
      id = input.id,
      title = input.title,
      content = input.content,
      tags = input.tags.map { it.name },
      skills = input.skills.map { it.name },
      thumbnailImage = "uploads/test-thumb.jpg",
      smallImage = "uploads/test-sml.jpg",
      mediumImage = "uploads/test-med.jpg",
      createdDate = input.createdAt.toLocalDate(),
      shortDescription = input.shortDescription,
      published = input.published
    )
    assertThat(BlogMapper.toBlogIndexRequest(input)).isEqualTo(expected)
  }

  @Test
  fun `should convert to index site request`() {
    val input = randomObject<BlogResponseDTO>(mapOf("image" to image("test", 400)))
    val expected = IndexSiteRequest(
      id = "blog_${input.id}",
      siteUrl = "/blogs/${input.id}",
      shortDescription = input.shortDescription,
      longDescription = input.content,
      name = input.title,
      type = "Blogs",
      image = "uploads/test-thumb.jpg"
    )
    assertThat(BlogMapper.toSiteIndexRequest(input)).isEqualTo(expected)
  }


}

package com.simonjamesrowe.searchservice.mapper

import com.simonjamesrowe.model.cms.dto.BlogResponseDTO
import com.simonjamesrowe.searchservice.core.model.IndexBlogRequest
import com.simonjamesrowe.searchservice.core.model.IndexSiteRequest

object BlogMapper {
  fun toBlogIndexRequest(blog: BlogResponseDTO) =
    IndexBlogRequest(
      id = blog.id,
      title = blog.title,
      content = blog.content,
      tags = blog.tags.map { it.name },
      skills = blog.skills.map { it.name },
      thumbnailImage = blog.image.formats?.thumbnail?.url ?: "",
      smallImage = blog.image.formats?.small?.url,
      mediumImage = blog.image.formats?.medium?.url,
      createdDate = blog.createdAt.toLocalDate(),
      shortDescription = blog.shortDescription,
      published = blog.published
    )

  fun toSiteIndexRequest(blog: BlogResponseDTO) =
    IndexSiteRequest(
      id = "blog_${blog.id}",
      siteUrl = "/blogs/${blog.id}",
      shortDescription = blog.shortDescription,
      longDescription = blog.content,
      name = blog.title,
      type = "Blogs",
      image = blog.image.formats?.thumbnail?.url ?: ""
    )
}

package com.simonjamesrowe.searchservice.mapper

import com.simonjamesrowe.model.data.Blog
import com.simonjamesrowe.searchservice.document.BlogDocument
import com.simonjamesrowe.searchservice.document.SiteDocument

object BlogMapper {
  fun toBlogDocument(blog: Blog) =
    BlogDocument(
      id = blog.id,
      title = blog.title,
      content = blog.content,
      tags = blog.tags.map { it.name },
      skills = blog.skills.map { it.name },
      thumbnailImage = blog.image.formats?.thumbnail?.url ?: "",
      smallImage = blog.image.formats?.small?.url,
      mediumImage = blog.image.formats?.medium?.url,
      createdDate = blog.createdAt.toLocalDate(),
      shortDescription = blog.shortDescription
    )

  fun toSiteDocument(blog: Blog) =
    SiteDocument(
      id = "blog_${blog.id}",
      siteUrl = "/blogs/${blog.id}",
      shortDescription = blog.shortDescription,
      longDescription = blog.content,
      name = blog.title,
      type = "Blogs",
      image = blog.image.formats?.thumbnail?.url ?: ""
    )
}

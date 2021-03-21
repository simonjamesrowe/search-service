package com.simonjamesrowe.searchservice.dataproviders.elasticsearch.blog

import com.simonjamesrowe.searchservice.core.model.BlogSearchResult
import com.simonjamesrowe.searchservice.core.model.IndexBlogRequest
import com.simonjamesrowe.searchservice.core.repository.BlogIndexRepository
import com.simonjamesrowe.searchservice.core.repository.BlogSearchRepository
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository

@Component
class BlogRepository(
  private val blogDocumentRepository: BlogDocumentRepository
) : BlogSearchRepository, BlogIndexRepository {

  override suspend fun search(q: String) =
    blogDocumentRepository.getBlogsByQuery(q).map(::toBlogSearchResult)

  override suspend fun getAll() = blogDocumentRepository.findAll(Sort.by(Sort.Direction.DESC, BlogDocument::createdDate.name))
    .map(::toBlogSearchResult)

  override fun indexBlog(request: IndexBlogRequest) {
    blogDocumentRepository.save(toBlogDocument(request))
  }

  override fun indexBlogs(requests: Collection<IndexBlogRequest>) {
    blogDocumentRepository.saveAll(requests.map(::toBlogDocument))
  }

  override fun deleteBlog(id: String) {
    blogDocumentRepository.findById(id).ifPresent { blogDocumentRepository.delete(it) }
  }

  override fun deleteBlogs(ids: Collection<String>) {
    blogDocumentRepository.findAllById(ids).forEach { blogDocumentRepository.delete(it) }
  }

  fun toBlogSearchResult(blogDocument: BlogDocument) =
    BlogSearchResult(
      id = blogDocument.id,
      title = blogDocument.title,
      tags = blogDocument.tags.map { it },
      thumbnailImage = blogDocument.thumbnailImage,
      smallImage = blogDocument.smallImage,
      mediumImage = blogDocument.mediumImage,
      createdDate = blogDocument.createdDate,
      shortDescription = blogDocument.shortDescription
    )

  fun toBlogDocument(indexBlogRequest: IndexBlogRequest) =
    BlogDocument(
      id = indexBlogRequest.id,
      title = indexBlogRequest.title,
      content = indexBlogRequest.content,
      tags = indexBlogRequest.tags.map { it },
      skills = indexBlogRequest.skills.map { it },
      thumbnailImage = indexBlogRequest.thumbnailImage,
      smallImage = indexBlogRequest.smallImage,
      mediumImage = indexBlogRequest.mediumImage,
      createdDate = indexBlogRequest.createdDate,
      shortDescription = indexBlogRequest.shortDescription
    )


}



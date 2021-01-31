package com.simonjamesrowe.searchservice.dao

import com.simonjamesrowe.model.data.Blog
import com.simonjamesrowe.searchservice.mapper.BlogMapper
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class BlogSearchRepository(
  private val blogDocumentRepository: BlogDocumentRepository
) {

  companion object {
    val log = LoggerFactory.getLogger(BlogSearchRepository::class.java)
  }

  fun saveAll(blogs: List<Blog>) {
    saveDocuments(blogs.filter { it.published })
    deleteDocuments(blogs.filter{ !it.published})
  }

  private fun deleteDocuments(blogs: List<Blog>) {
    blogs.map{it.id}
      .map { blogDocumentRepository.findById(it) }
      .forEach {
        it.ifPresent { doc ->
          log.info("deleting document ${doc.id}, ${doc.title}")
          blogDocumentRepository.deleteById(doc.id)
        }
      }
  }

  private fun saveDocuments(blogs: List<Blog>) {
    blogs.map(BlogMapper::toBlogDocument)
      .run {
        if (isNotEmpty()) {
          log.info("Indexing documents ${this.map { it.title }}")
          blogDocumentRepository.saveAll(this)
        }
      }
  }


}

package com.simonjamesrowe.searchservice.adaptor

import com.simonjamesrowe.searchservice.dao.BlogDocumentRepository
import com.simonjamesrowe.searchservice.document.BlogDocument
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.web.bind.annotation.*

@CrossOrigin
@RestController
class BlogController(
  private val blogDocumentRepository: BlogDocumentRepository
) {

  @GetMapping(value = ["/blogs"], params = ["q"])
  fun blogsQuery(@RequestParam q: String) : List<BlogDocument> =
    blogDocumentRepository.getBlogsByQuery(q)

  @GetMapping(value = ["/blogs"], params = ["!q"])
  fun allBlogs() : List<BlogDocument> =
    blogDocumentRepository.findAll(
      PageRequest.of(
        0, 10,
        Sort.by(Sort.Direction.DESC, "createdDate")
      )
    ).content

  @GetMapping("/tags/{tag}/blogs")
  fun blogsForTag(@PathVariable tag: String) : List<BlogDocument> =
    blogDocumentRepository.getBlogsByTag(
      tag.toLowerCase().trim(),
      PageRequest.of(
        0, 10,
        Sort.by(Sort.Direction.DESC, "createdDate")
      )
    )

}

package com.simonjamesrowe.searchservice.entrypoints.restcontroller

import com.simonjamesrowe.searchservice.core.usecase.SearchBlogsUseCase
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@CrossOrigin
@RestController
class BlogController(
  private val searchBlogsUseCase: SearchBlogsUseCase
) {

  @GetMapping(value = ["/blogs"], params = ["q"])
  suspend fun search(@RequestParam q: String) = searchBlogsUseCase.search(q)

  @GetMapping(value = ["/blogs"], params = ["!q"])
  suspend fun getAll() = searchBlogsUseCase.getAll()


}

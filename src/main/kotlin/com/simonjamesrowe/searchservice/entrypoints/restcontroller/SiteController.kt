package com.simonjamesrowe.searchservice.entrypoints.restcontroller

import com.simonjamesrowe.searchservice.core.usecase.SearchSiteUseCase
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@CrossOrigin
@RestController
class SiteController(
  private val searchSiteUseCase: SearchSiteUseCase
) {

  @GetMapping("/site")
  suspend fun siteSearch(@RequestParam q: String) = searchSiteUseCase.search(q)

}

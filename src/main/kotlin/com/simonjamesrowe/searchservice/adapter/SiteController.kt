package com.simonjamesrowe.searchservice.adapter

import com.simonjamesrowe.searchservice.dao.SiteSearchRepository
import com.simonjamesrowe.searchservice.dto.SiteResultDto
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@CrossOrigin
@RestController
class SiteController(
  private val siteSearchRepository: SiteSearchRepository
) {

  @GetMapping("/site")
  fun siteSearch(@RequestParam q: String): List<SiteResultDto> =
    siteSearchRepository.searchSite(q)


}

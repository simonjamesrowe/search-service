package com.simonjamesrowe.searchservice.adaptor

import com.simonjamesrowe.model.data.Blog
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping

@FeignClient(name = "blogRestApi", url = "\${cms.url}")
interface BlogRestApi {

  @GetMapping("/blogs")
  fun getAllBlogs() : List<Blog>

}
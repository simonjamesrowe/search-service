package com.simonjamesrowe.searchservice.adapter

import com.simonjamesrowe.model.data.Blog
import com.simonjamesrowe.model.data.Job
import com.simonjamesrowe.model.data.SkillsGroup
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam

@FeignClient(name = "blogRestApi", url = "\${cms.url}")
interface CmsRestApi {

  @GetMapping("/blogs")
  fun getAllBlogs(): List<Blog>

  @GetMapping("/jobs")
  fun getAllJobs(): List<Job>

  @GetMapping("/skills-groups")
  fun getAllSkillsGroups(): List<SkillsGroup>

  @GetMapping("/skills-groups?skill._id={id}")
  fun getSkillsGroupBySkillId(
    @PathVariable("id") skillId: String
  ): List<SkillsGroup>

}

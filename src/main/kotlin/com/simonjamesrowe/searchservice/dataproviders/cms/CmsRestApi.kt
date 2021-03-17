package com.simonjamesrowe.searchservice.dataproviders.cms

import com.simonjamesrowe.model.cms.dto.BlogResponseDTO
import com.simonjamesrowe.model.cms.dto.JobResponseDTO
import com.simonjamesrowe.model.cms.dto.SkillsGroupResponseDTO

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@FeignClient(name = "blogRestApi", url = "\${cms.url}")
interface CmsRestApi {

  @GetMapping("/blogs")
  fun getAllBlogs(): List<BlogResponseDTO>

  @GetMapping("/jobs")
  fun getAllJobs(): List<JobResponseDTO>

  @GetMapping("/skills-groups")
  fun getAllSkillsGroups(): List<SkillsGroupResponseDTO>

  @GetMapping("/skills-groups?skill._id={id}")
  fun getSkillsGroupBySkillId(
    @PathVariable("id") skillId: String
  ): List<SkillsGroupResponseDTO>

}

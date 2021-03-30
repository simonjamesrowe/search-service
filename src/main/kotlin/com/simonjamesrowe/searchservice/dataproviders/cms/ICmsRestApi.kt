package com.simonjamesrowe.searchservice.dataproviders.cms

import com.simonjamesrowe.model.cms.dto.BlogResponseDTO
import com.simonjamesrowe.model.cms.dto.JobResponseDTO
import com.simonjamesrowe.model.cms.dto.SkillsGroupResponseDTO

interface ICmsRestApi {
  suspend fun getAllBlogs(): List<BlogResponseDTO>

  suspend fun getAllJobs(): List<JobResponseDTO>

  suspend fun getAllSkillsGroups(): List<SkillsGroupResponseDTO>

  suspend fun getSkillsGroupBySkillId(skillId: String): List<SkillsGroupResponseDTO>
}

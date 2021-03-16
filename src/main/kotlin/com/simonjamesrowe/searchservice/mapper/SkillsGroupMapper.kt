package com.simonjamesrowe.searchservice.mapper

import com.simonjamesrowe.model.cms.dto.SkillsGroupResponseDTO
import com.simonjamesrowe.searchservice.core.model.IndexSiteRequest

object SkillsGroupMapper {
  fun toSiteIndexRequests(skillsGroup: SkillsGroupResponseDTO) =
    skillsGroup.skills.map { skill ->
      IndexSiteRequest(
        id = "skill_${skill.id}",
        siteUrl = "/skills-groups/${skillsGroup.id}#${skill.id}",
        shortDescription = skill.description ?: "",
        longDescription = skillsGroup.description,
        name = skill.name,
        type = "Skills",
        image = skill.image.formats?.thumbnail?.url ?: ""
      )
    }
}

package com.simonjamesrowe.searchservice.mapper

import com.simonjamesrowe.model.data.SkillsGroup
import com.simonjamesrowe.searchservice.document.SiteDocument

object SkillsGroupMapper {
  fun toSiteDocuments(skillsGroup: SkillsGroup) =
    skillsGroup.skills.map { skill ->
      SiteDocument(
        id = "skill_${skill.id}",
        siteUrl = "/skills-groups/${skillsGroup.id}",
        shortDescription = skill.description ?: "",
        longDescription = skillsGroup.description,
        name = skill.name,
        type = "Skill",
        image = skill.image.formats?.thumbnail?.url ?: ""
      )
    }
}

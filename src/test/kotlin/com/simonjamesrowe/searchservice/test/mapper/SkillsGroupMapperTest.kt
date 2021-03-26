package com.simonjamesrowe.searchservice.test.mapper

import com.simonjamesrowe.model.cms.dto.SkillResponseDTO
import com.simonjamesrowe.model.cms.dto.SkillsGroupResponseDTO
import com.simonjamesrowe.searchservice.test.TestUtils.image
import com.simonjamesrowe.searchservice.test.TestUtils.randomObject
import com.simonjamesrowe.searchservice.core.model.IndexSiteRequest
import com.simonjamesrowe.searchservice.mapper.SkillsGroupMapper
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class SkillsGroupMapperTest {

  @Test
  fun `should convert to index site requests`() {
    val skill1 = randomObject<SkillResponseDTO>(mapOf("image" to image("skill1", 200)))
    val skill2 = randomObject<SkillResponseDTO>(mapOf("image" to image("skill2", 200)))
    val skillsGroup = randomObject<SkillsGroupResponseDTO>(
      mapOf(
        "skills" to listOf(skill1, skill2),
        "image" to image("skillGroup", 300)
      )
    )
    val expected = listOf(
      IndexSiteRequest(
        id = "skill_${skill1.id}",
        siteUrl = "/skills-groups/${skillsGroup.id}#${skill1.id}",
        shortDescription = skill1.description ?: "",
        longDescription = skillsGroup.description,
        name = skill1.name,
        type = "Skills",
        image = "uploads/skill1-thumb.jpg"
      ),
      IndexSiteRequest(
        id = "skill_${skill2.id}",
        siteUrl = "/skills-groups/${skillsGroup.id}#${skill2.id}",
        shortDescription = skill2.description ?: "",
        longDescription = skillsGroup.description,
        name = skill2.name,
        type = "Skills",
        image = "uploads/skill2-thumb.jpg"
      )
    )
    assertThat(SkillsGroupMapper.toSiteIndexRequests(skillsGroup)).isEqualTo(expected)
  }

}


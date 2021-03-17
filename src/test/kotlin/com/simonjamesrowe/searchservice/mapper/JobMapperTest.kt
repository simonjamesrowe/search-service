package com.simonjamesrowe.searchservice.mapper

import com.simonjamesrowe.model.cms.dto.JobResponseDTO
import com.simonjamesrowe.searchservice.TestUtils.image
import com.simonjamesrowe.searchservice.TestUtils.randomObject
import com.simonjamesrowe.searchservice.core.model.IndexSiteRequest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class JobMapperTest {

  @Test
  fun `should convert to index site request`() {
    val input = randomObject<JobResponseDTO>(mapOf("companyImage" to image("companyImg", 200)))
    val expected = IndexSiteRequest(
      id = "job_${input.id}",
      siteUrl = "/jobs/${input.id}",
      shortDescription = input.shortDescription,
      longDescription = input.longDescription ?: "",
      name = "${input.title} (${input.company}) - ${input.startDate.year} - ${input.endDate?.year ?: "Present"}",
      type = "Jobs",
      image = "uploads/companyImg-thumb.jpg"
    )
    assertThat(JobMapper.toIndexSiteRequest(input)).isEqualTo(expected)
  }

}

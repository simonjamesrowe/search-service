package com.simonjamesrowe.searchservice.mapper

import com.simonjamesrowe.model.cms.dto.JobResponseDTO
import com.simonjamesrowe.searchservice.core.model.IndexSiteRequest

object JobMapper {
  fun toIndexSiteRequest(job: JobResponseDTO) =
    IndexSiteRequest(
      id = "job_${job.id}",
      siteUrl = "/jobs/${job.id}",
      shortDescription = job.shortDescription,
      longDescription = job.longDescription ?: "",
      name = "${job.title} (${job.company}) - ${job.startDate.year} - ${job.endDate?.year ?: "Present"}",
      type = "Jobs",
      image = job.companyImage.formats?.thumbnail?.url ?: job.companyImage.formats?.small?.url
      ?: job.companyImage.formats?.medium?.url ?: job.companyImage.formats?.large?.url ?: job.companyImage.url
    )
}

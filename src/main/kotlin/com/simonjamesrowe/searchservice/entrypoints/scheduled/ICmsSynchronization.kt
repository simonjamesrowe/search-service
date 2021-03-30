package com.simonjamesrowe.searchservice.entrypoints.scheduled

import kotlinx.coroutines.Job
import org.springframework.scheduling.annotation.Scheduled

interface ICmsSynchronization {
  @Scheduled(initialDelay = CmsSynchronization.ONE_MINUTE, fixedDelay = CmsSynchronization.FOUR_HOURS)
  fun syncBlogDocuments(): Job

  @Scheduled(initialDelay = CmsSynchronization.ONE_MINUTE, fixedDelay = CmsSynchronization.FOUR_HOURS)
  fun syncSiteDocuments(): Job
}

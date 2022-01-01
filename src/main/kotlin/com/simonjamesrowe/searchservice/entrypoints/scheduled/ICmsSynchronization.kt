package com.simonjamesrowe.searchservice.entrypoints.scheduled

import org.springframework.scheduling.annotation.Scheduled

interface ICmsSynchronization {
  @Scheduled(initialDelay = CmsSynchronization.ONE_MINUTE, fixedDelay = CmsSynchronization.FOUR_HOURS)
  fun syncBlogDocuments()

  @Scheduled(initialDelay = CmsSynchronization.ONE_MINUTE, fixedDelay = CmsSynchronization.FOUR_HOURS)
  fun syncSiteDocuments()
}

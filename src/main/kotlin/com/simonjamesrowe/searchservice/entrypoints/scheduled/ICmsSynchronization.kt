package com.simonjamesrowe.searchservice.entrypoints.scheduled

interface ICmsSynchronization {
  fun syncBlogDocuments()

  fun syncSiteDocuments()
}

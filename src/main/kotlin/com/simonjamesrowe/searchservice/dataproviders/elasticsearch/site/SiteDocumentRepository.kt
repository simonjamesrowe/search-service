package com.simonjamesrowe.searchservice.dataproviders.elasticsearch.site

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository
import org.springframework.stereotype.Repository

@Repository
interface SiteDocumentRepository : ElasticsearchRepository<SiteDocument, String>

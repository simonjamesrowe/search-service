package com.simonjamesrowe.searchservice.dao

import com.simonjamesrowe.searchservice.document.SiteDocument
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository

interface SiteDocumentRepository : ElasticsearchRepository<SiteDocument, String>

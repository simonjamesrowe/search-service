package com.simonjamesrowe.searchservice.dataproviders.elasticsearch.site

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository

interface SiteDocumentRepository : ElasticsearchRepository<SiteDocument, String>

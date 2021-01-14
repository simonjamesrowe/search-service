package com.simonjamesrowe.searchservice.dao

import com.simonjamesrowe.searchservice.document.BlogDocument
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository

interface BlogDocumentRepository : ElasticsearchRepository<BlogDocument, String>
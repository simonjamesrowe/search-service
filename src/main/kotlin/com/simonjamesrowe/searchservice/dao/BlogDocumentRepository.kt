package com.simonjamesrowe.searchservice.dao

import com.simonjamesrowe.searchservice.document.BlogDocument
import org.springframework.data.domain.Pageable
import org.springframework.data.elasticsearch.annotations.Query
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository

interface BlogDocumentRepository : ElasticsearchRepository<BlogDocument, String> {

  @Query("""
      {
        "bool": {
          "should": [
            {
              "function_score": {
                "query": {
                  "multi_match": {
                    "query": "?0",
                    "type": "bool_prefix",
                    "fields": [
                      "title"
                    ]
                  }
                },
                "boost": 5
              }
            },
            {
              "multi_match": {
                "query": "?0",
                "fields": [
                  "title",
                  "content",
                  "skills^2",
                  "tags^2"
                ]
              }
            }
          ]
        }
      }
  """)
  fun getBlogsByQuery(q: String): List<BlogDocument>

  @Query("""
    {
      "bool": {
        "must": {
          "match_all": {}
        },
        "filter": [
          { "term" : { "tags" : "?0"}}
        ]
      }
    }
  """
  )
  fun getBlogsByTag(tag: String, pageable: Pageable) : List<BlogDocument>

}

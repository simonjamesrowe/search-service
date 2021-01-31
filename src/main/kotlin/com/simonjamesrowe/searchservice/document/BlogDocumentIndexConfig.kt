package com.simonjamesrowe.searchservice.document

import com.simonjamesrowe.searchservice.config.ElasticSearchIndexProperties
import org.elasticsearch.client.Request
import org.elasticsearch.client.RestHighLevelClient
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component

@Component
class BlogDocumentIndexConfig(
  elasticSearchClient: RestHighLevelClient,
  elasticSearchIndexProperties: ElasticSearchIndexProperties
) : AbstractIndexConfig(
  elasticSearchClient,
  elasticSearchIndexProperties.blog
) {

  override val settings = """
      {
            "analysis" : {
              "analyzer": {
                "lowercase_keyword":{
                  "tokenizer":"keyword",
                  "filter": ["lowercase"]
                },
                "markdown_text" : {
                  "char_filter": [
                    "html_strip"
                  ],
                  "tokenizer" : "lowercase",
                  "filter" : ["stop", "unique", "porter_stem"]
                }
              }
            }
          }
    """
  override val mappings = """
      {
            "properties" : {
              "title" : {
                "type" : "search_as_you_type",
                "store" : true,
                "index" : true
              },
              "content" : {
                "type": "text",
                "store": false,
                "index": true,
                "analyzer": "markdown_text"
              },
              "shortDescription" :{
                "type" : "text",
                "store" : true
              },
              "tags": {
                "type" : "text",
                "store": true,
                "analyzer": "lowercase_keyword"
              },
              "skills": {
                "type" : "text",
                "store": true,
                "analyzer": "lowercase_keyword"
              },
              "thumbnailImage" : {
                "type" : "keyword",
                "store": true,
                "index" : false
              },
              "smallImage" : {
                "type" : "keyword",
                "store": true,
                "index" : false
              },
              "mediumImage" : {
                "type" : "keyword",
                "store": true,
                "index" : false
              },
              "createdDate" : {
                "type" : "date",
                "store": true,
                "index" : true,
                "format": "uuuu-MM-dd"
              }
            }
          }
        }
    """

}

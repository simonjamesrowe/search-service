package com.simonjamesrowe.searchservice.dataproviders.elasticsearch.site

import com.simonjamesrowe.searchservice.config.ElasticSearchIndexProperties
import com.simonjamesrowe.searchservice.dataproviders.elasticsearch.AbstractIndexConfig
import org.elasticsearch.client.RestHighLevelClient
import org.springframework.stereotype.Component

@Component
class SiteDocumentIndexConfig(
  elasticSearchClient: RestHighLevelClient,
  elasticSearchIndexProperties: ElasticSearchIndexProperties
) : AbstractIndexConfig(
  elasticSearchClient,
  elasticSearchIndexProperties.site
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
            "name" : {
              "type" : "text",
              "fields": {
                "raw": { 
                  "type":  "keyword"
                },
                "search": {
                  "type" : "search_as_you_type"
                }
              }
            },
            "shortDescription" : {
              "type": "text",
              "store": false,
              "index": true,
              "analyzer": "markdown_text"
            },
            "longDescription" : {
              "type": "text",
              "store": false,
              "index": true,
              "analyzer": "markdown_text"
            },
            "siteUrl" :{
              "type" : "keyword",
              "store" : true,
              "index" : false
            },
            "image" :{
              "type" : "keyword",
              "store" : true,
              "index" : false
           }
         }
       }
    """

}

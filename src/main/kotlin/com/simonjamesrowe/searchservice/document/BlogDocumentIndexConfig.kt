package com.simonjamesrowe.searchservice.document

import com.simonjamesrowe.searchservice.config.ElasticSearchIndexProperties
import org.elasticsearch.client.Request
import org.elasticsearch.client.RestHighLevelClient
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component

@Component
class BlogDocumentIndexConfig(
  private val elasticSearchClient: RestHighLevelClient,
  private val elasticSearchIndexProperties: ElasticSearchIndexProperties
) : ApplicationRunner {

  companion object {
    const val settings = """
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
    const val mappings = """
      {
            "properties" : {
              "title" : {
                "type" : "text",
                "store" : true,
                "index" : true
              },
              "content" : {
                "type": "text",
                "store": false,
                "index": true,
                "analyzer": "markdown_text"
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

  override fun run(args: ApplicationArguments) {
    runCatching { closeIndex() }
    createOrUpdateIndex()
    openIndex()
  }

  private fun openIndex() {
    elasticSearchClient.lowLevelClient.performRequest(
      Request("POST", "${elasticSearchIndexProperties.blog}/_open")
    )
  }

  private fun createOrUpdateIndex() {
    val body =
      """
        {
          "settings" : $settings,
          "mappings" : $mappings
      """.trimIndent()
    runCatching {
      elasticSearchClient.lowLevelClient.performRequest(
        Request("PUT", "${elasticSearchIndexProperties.blog}").also {
          it.setJsonEntity(body)
        }
      )
    }.onFailure {
      elasticSearchClient.lowLevelClient.performRequest(
        Request("PUT", "${elasticSearchIndexProperties.blog}/_settings").also {
          it.setJsonEntity(settings)
        }
      )
      elasticSearchClient.lowLevelClient.performRequest(
        Request("PUT", "${elasticSearchIndexProperties.blog}/_mappings").also {
          it.setJsonEntity(mappings)
        }
      )
    }
  }

  private fun closeIndex() {
    elasticSearchClient.lowLevelClient.performRequest(
      Request("POST", "${elasticSearchIndexProperties.blog}/_close")
    )
  }
}
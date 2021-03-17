package com.simonjamesrowe.searchservice.dataproviders.elasticsearch

import org.elasticsearch.client.Request
import org.elasticsearch.client.RestHighLevelClient
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner

abstract class AbstractIndexConfig(
  private val elasticSearchClient: RestHighLevelClient,
  private val indexName: String
) : ApplicationRunner {

  abstract val settings: String
  abstract val mappings: String

  override fun run(args: ApplicationArguments) {
    runCatching { closeIndex() }
    createOrUpdateIndex()
    openIndex()
  }

  private fun createOrUpdateIndex() {
    val body =
      """
        {
          "settings" : $settings,
          "mappings" : $mappings
        }
      """.trimIndent()
    runCatching {
      elasticSearchClient.lowLevelClient.performRequest(
        Request("PUT", indexName).also {
          it.setJsonEntity(body)
        }
      )
    }.onFailure {
      elasticSearchClient.lowLevelClient.performRequest(
        Request("PUT", "$indexName/_settings").also {
          it.setJsonEntity(settings)
        }
      )
      elasticSearchClient.lowLevelClient.performRequest(
        Request("PUT", "$indexName/_mappings").also {
          it.setJsonEntity(mappings)
        }
      )
    }
  }

  private fun openIndex() {
    elasticSearchClient.lowLevelClient.performRequest(
      Request("POST", "$indexName/_open")
    )
  }

  private fun closeIndex() {
    elasticSearchClient.lowLevelClient.performRequest(
      Request("POST", "$indexName/_close")
    )
  }

}

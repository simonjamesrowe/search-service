package com.simonjamesrowe.searchservice

import com.simonjamesrowe.component.test.BaseComponentTest
import com.simonjamesrowe.component.test.elasticsearch.WithElasticsearchContainer
import com.simonjamesrowe.component.test.kafka.WithKafkaContainer
import org.junit.jupiter.api.Test

@WithElasticsearchContainer
@WithKafkaContainer
class SearchServiceApplicationTests : BaseComponentTest() {

  init {
    partitionCount = 1
  }

  @Test
  fun contextLoads() {
  }

}

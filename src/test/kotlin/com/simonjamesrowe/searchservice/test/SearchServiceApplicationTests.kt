package com.simonjamesrowe.searchservice.test

import com.simonjamesrowe.component.test.BaseComponentTest
import com.simonjamesrowe.component.test.elasticsearch.WithElasticsearchContainer
import com.simonjamesrowe.component.test.kafka.WithKafkaContainer
import org.junit.jupiter.api.Test
import org.springframework.test.context.ActiveProfiles

@WithElasticsearchContainer
@WithKafkaContainer
@ActiveProfiles("cms")
class SearchServiceApplicationTests : BaseComponentTest() {

  init {
    partitionCount = 1
  }

  @Test
  fun contextLoads() {
  }

}

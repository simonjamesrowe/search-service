package com.simonjamesrowe.searchservice.adapter

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.convertValue
import com.simonjamesrowe.component.test.BaseComponentTest
import com.simonjamesrowe.component.test.elasticsearch.WithElasticsearchContainer
import com.simonjamesrowe.component.test.kafka.WithKafkaContainer
import com.simonjamesrowe.model.data.*
import com.simonjamesrowe.searchservice.TestUtils.image
import com.simonjamesrowe.searchservice.dao.BlogDocumentRepository
import org.assertj.core.api.Assertions.assertThat
import org.awaitility.Awaitility.await
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cloud.stream.function.StreamBridge
import org.springframework.test.annotation.DirtiesContext
import java.time.Duration
import java.time.LocalDate
import java.time.ZonedDateTime

@WithKafkaContainer
@WithElasticsearchContainer
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
internal class KafkaEventConsumerTest : BaseComponentTest() {

  @Autowired
  private lateinit var streamBridge: StreamBridge

  @Autowired
  private lateinit var objectMapper: ObjectMapper

  @Autowired
  private lateinit var blogDocumentRepository: BlogDocumentRepository

  @BeforeEach
  @AfterEach
  fun clearElasticSearchDocuments() {
    blogDocumentRepository.deleteAll()
  }

  @Test
  fun `blog events from kafka should be indexed`() {
    val blog = Blog(
      id = "1",
      title = "My first blog",
      published = true,
      content = "Some awesome content",
      createdAt = ZonedDateTime.now(),
      updatedAt = ZonedDateTime.now(),
      shortDescription = "Short description",
      tags = listOf(
        Tag(
          id = "1",
          name = "Kubernetes",
          createdAt = ZonedDateTime.now(),
          updatedAt = ZonedDateTime.now(),
          version = 1
        ),
        Tag(
          id = "2",
          name = "Jenkins",
          createdAt = ZonedDateTime.now(),
          updatedAt = ZonedDateTime.now(),
          version = 1
        )
      ),
      skills = listOf(
        Skill(
          id = "1",
          name = "Jenkins",
          description = "Jenkins Description",
          createdAt = ZonedDateTime.now(),
          updatedAt = ZonedDateTime.now(),
          rating = 7.8,
          order = 2,
          image = image("jenkins", 10)
        )
      ),
      image = image("image1", 10)
    )
    val blog2 = Blog(
      id = "2",
      title = "My second blog",
      published = true,
      content = "Some awesome content again",
      createdAt = ZonedDateTime.now(),
      updatedAt = ZonedDateTime.now(),
      shortDescription = "Short description 2",
      tags = listOf(
        Tag(
          id = "3",
          name = "Spring",
          createdAt = ZonedDateTime.now(),
          updatedAt = ZonedDateTime.now(),
          version = 1
        ),
        Tag(
          id = "4",
          name = "Kotlin",
          createdAt = ZonedDateTime.now(),
          updatedAt = ZonedDateTime.now(),
          version = 1
        )
      ),
      skills = listOf(
        Skill(
          id = "5",
          name = "TestContainers",
          description = "TestContainers Description",
          createdAt = ZonedDateTime.now(),
          updatedAt = ZonedDateTime.now(),
          rating = 4.0,
          order = 1,
          image = image("test-containers",5 )
        )
      ),
      image = image("image2", 6)
    )
    val blog3 = Blog(
      id = "3",
      title = "My 3rd blog",
      published = false,
      content = "Some awesome content again",
      createdAt = ZonedDateTime.now(),
      updatedAt = ZonedDateTime.now(),
      shortDescription = "Short description 2",
      tags = listOf(
        Tag(
          id = "3",
          name = "Spring",
          createdAt = ZonedDateTime.now(),
          updatedAt = ZonedDateTime.now(),
          version = 1
        ),
        Tag(
          id = "4",
          name = "Kotlin",
          createdAt = ZonedDateTime.now(),
          updatedAt = ZonedDateTime.now(),
          version = 1
        )
      ),
      skills = listOf(
        Skill(
          id = "5",
          name = "TestContainers",
          description = "TestContainers Description",
          createdAt = ZonedDateTime.now(),
          updatedAt = ZonedDateTime.now(),
          rating = 4.0,
          order = 1,
          image = image("testcontainers", 8)
        )
      ),
      image = image("image2", 9)
    )
    val event1 = Event(
      event = "created",
      createdAt = ZonedDateTime.now(),
      model = "blog",
      entry = objectMapper.convertValue(blog)
    )
    val event2 = Event(
      event = "created",
      createdAt = ZonedDateTime.now(),
      model = "car",
      entry = objectMapper.convertValue(listOf("vals"))
    )
    val event3 = Event(
      event = "updated",
      createdAt = ZonedDateTime.now(),
      model = "blog",
      entry = objectMapper.convertValue(blog2)
    )
    val event4 = Event(
      event = "updated",
      createdAt = ZonedDateTime.now(),
      model = "blog",
      entry = objectMapper.convertValue(blog3)
    )
    streamBridge.send("output", event1)
    streamBridge.send("output", event2)
    streamBridge.send("output", event3)
    streamBridge.send("output", event4)

    await().atMost(Duration.ofSeconds(30)).until {
      blogDocumentRepository.count() == 2L
    }

    var doc2 = blogDocumentRepository.findById("2").get()
    assertThat(doc2.id).isEqualTo("2")
    assertThat(doc2.title).isEqualTo("My second blog")
    assertThat(doc2.content).isEqualTo("Some awesome content again")
    assertThat(doc2.tags).isEqualTo(listOf("Spring", "Kotlin"))
    assertThat(doc2.skills).isEqualTo(listOf("TestContainers"))
    assertThat(doc2.thumbnailImage).isEqualTo("uploads/image2-thumb.jpg")
    assertThat(doc2.smallImage).isEqualTo("uploads/image2-sml.jpg")
    assertThat(doc2.mediumImage).isEqualTo("uploads/image2-med.jpg")
    assertThat(doc2.createdDate).isEqualTo(LocalDate.now())
  }

}

package com.simonjamesrowe.searchservice.dataproviders.cms

import com.simonjamesrowe.model.cms.dto.BlogResponseDTO
import com.simonjamesrowe.model.cms.dto.JobResponseDTO
import com.simonjamesrowe.model.cms.dto.SkillsGroupResponseDTO
import kotlinx.coroutines.reactive.awaitFirst
import org.springframework.beans.factory.annotation.Value
import org.springframework.cloud.sleuth.annotation.NewSpan
import org.springframework.cloud.sleuth.annotation.SpanTag
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient

@Component
class CmsRestApi(
  private val webClient: WebClient,
  @Value("\${cms.url}")
  private val cmsUrl: String
) : ICmsRestApi {

  @NewSpan("http-getAllBlogs-cms")
  override suspend fun getAllBlogs(): List<BlogResponseDTO> =
    webClient.get()
      .uri("$cmsUrl/blogs")
      .accept(MediaType.APPLICATION_JSON)
      .retrieve()
      .bodyToMono(object : ParameterizedTypeReference<List<BlogResponseDTO>>() {})
      .awaitFirst()


  @NewSpan("http-getAllJobs-cms")
  override suspend fun getAllJobs(): List<JobResponseDTO> = webClient.get()
    .uri("$cmsUrl/jobs")
    .accept(MediaType.APPLICATION_JSON)
    .retrieve()
    .bodyToMono(object : ParameterizedTypeReference<List<JobResponseDTO>>() {})
    .awaitFirst()

  @NewSpan("http-getAllSkillsGroups-cms")
  override suspend fun getAllSkillsGroups(): List<SkillsGroupResponseDTO> =
    webClient.get()
      .uri("$cmsUrl/skills-groups")
      .accept(MediaType.APPLICATION_JSON)
      .retrieve()
      .bodyToMono(object : ParameterizedTypeReference<List<SkillsGroupResponseDTO>>() {})
      .awaitFirst()

  @NewSpan("http-getSkillsGroups-cms")
  override suspend fun getSkillsGroupBySkillId(@SpanTag("id") skillId: String): List<SkillsGroupResponseDTO> =
    webClient.get()
      .uri("$cmsUrl/skills-groups?skill._id={id}", skillId)
      .accept(MediaType.APPLICATION_JSON)
      .retrieve()
      .bodyToMono(object : ParameterizedTypeReference<List<SkillsGroupResponseDTO>>() {})
      .awaitFirst()

}

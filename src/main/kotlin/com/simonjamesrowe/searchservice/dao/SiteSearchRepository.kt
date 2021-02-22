package com.simonjamesrowe.searchservice.dao

import com.simonjamesrowe.model.data.Blog
import com.simonjamesrowe.model.data.Job
import com.simonjamesrowe.model.data.Skill
import com.simonjamesrowe.searchservice.adapter.CmsRestApi
import com.simonjamesrowe.searchservice.config.ElasticSearchIndexProperties
import com.simonjamesrowe.searchservice.dto.SiteResultDto
import com.simonjamesrowe.searchservice.mapper.BlogMapper
import com.simonjamesrowe.searchservice.mapper.JobMapper.toSiteDocument
import com.simonjamesrowe.searchservice.mapper.SiteResultMapper
import com.simonjamesrowe.searchservice.mapper.SkillsGroupMapper
import org.elasticsearch.action.search.SearchRequest
import org.elasticsearch.client.RequestOptions
import org.elasticsearch.client.RestHighLevelClient
import org.elasticsearch.index.query.QueryBuilders
import org.elasticsearch.script.Script
import org.elasticsearch.search.aggregations.AggregationBuilders
import org.elasticsearch.search.aggregations.AggregatorFactories
import org.elasticsearch.search.aggregations.BucketOrder
import org.elasticsearch.search.builder.SearchSourceBuilder
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Repository


@Repository
class SiteSearchRepository(
  private val highLevelClient: RestHighLevelClient,
  private val elasticSearchIndexProperties: ElasticSearchIndexProperties,
  private val siteDocumentRepository: SiteDocumentRepository,
  private val cmsRestApi: CmsRestApi
) {

  companion object {
    val log = LoggerFactory.getLogger(SiteSearchRepository::class.java)
  }

  fun searchSite(q: String): List<SiteResultDto> {
    val boolQuery = QueryBuilders
      .boolQuery()
      .should(
        QueryBuilders.functionScoreQuery(
          QueryBuilders.prefixQuery("name.search", q)
        ).boost(5f)
      )
      .should(
        QueryBuilders.multiMatchQuery(q, "name.search", "shortDescription", "longDescription")
      )
    val sourceBuilder = SearchSourceBuilder().also {
      it.query(boolQuery)
      it.size(0)
      it.aggregation(
        AggregationBuilders.terms("type").field("type.keyword").size(5)
          .subAggregation(
            AggregationBuilders.terms("id").field("_id").size(100)
              .order(BucketOrder.aggregation("score", false))
              .subAggregations(
                AggregatorFactories.Builder()
                  .addAggregator(AggregationBuilders.terms("url").field("siteUrl"))
                  .addAggregator(AggregationBuilders.terms("imageUrl").field("image"))
                  .addAggregator(AggregationBuilders.terms("name").field("name.raw"))
                  .addAggregator(AggregationBuilders.max("score").script(Script.parse("_score")))
              )
          )
      )
    }
    val searchRequest = SearchRequest(elasticSearchIndexProperties.site)
      .also {
        it.source(sourceBuilder)
      }

    val results = highLevelClient.search(searchRequest, RequestOptions.DEFAULT)
    return SiteResultMapper.mapList(results)
  }

  fun saveSkills(skills: List<Skill>) {
    if (skills.isNotEmpty()) {
      log.info("Updating site document with ${skills.size} skill documents")
      val skillsGroups = cmsRestApi.getAllSkillsGroups()
      skillsGroups.forEach { siteDocumentRepository.saveAll(SkillsGroupMapper.toSiteDocuments(it)) }
    }
  }

  fun saveJobs(jobs: List<Job>) {
    log.info("Updating site document with ${jobs.size} job documents")
    jobs.map(::toSiteDocument).run { siteDocumentRepository.saveAll(this) }
  }

  fun saveBlogs(blogs: List<Blog>) {
    log.info("Updating site document with ${blogs.size} blog documents")
    blogs.map { BlogMapper.toSiteDocument(it) }.run { siteDocumentRepository.saveAll(this) }
  }
}

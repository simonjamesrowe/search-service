package com.simonjamesrowe.searchservice.mapper

import com.simonjamesrowe.searchservice.dto.SiteResultDto
import org.elasticsearch.action.search.SearchResponse
import org.elasticsearch.search.aggregations.bucket.terms.Terms

object SiteResultMapper {
  fun mapList(results: SearchResponse): List<SiteResultDto> {
    val type = results.aggregations.get<Terms>("type")
    return type.buckets.map { bucket ->
      SiteResultDto(
        type = bucket.keyAsString,
        hits = bucket.aggregations.get<Terms>("id").buckets.map {idBucket ->
          SiteResultDto.Hit (
            imageUrl = idBucket.aggregations.get<Terms>("imageUrl").buckets[0].keyAsString,
            name = idBucket.aggregations.get<Terms>("name").buckets[0].keyAsString,
            link = idBucket.aggregations.get<Terms>("url").buckets[0].keyAsString
          )
        }
      )
    }
  }
}

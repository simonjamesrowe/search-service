package com.simonjamesrowe.searchservice.dataproviders.elasticsearch.site

import com.simonjamesrowe.searchservice.core.model.SiteSearchResult
import org.elasticsearch.action.search.SearchResponse
import org.elasticsearch.search.aggregations.bucket.terms.Terms

object SiteResultMapper {
  fun mapList(results: SearchResponse): List<SiteSearchResult> {
    val type = results.aggregations.get<Terms>("type")
    return type.buckets.map { bucket ->
      SiteSearchResult(
        type = bucket.keyAsString,
        hits = bucket.aggregations.get<Terms>("id").buckets.map {idBucket ->
          SiteSearchResult.Hit (
            imageUrl = idBucket.aggregations.get<Terms>("imageUrl").buckets[0].keyAsString,
            name = idBucket.aggregations.get<Terms>("name").buckets[0].keyAsString,
            link = idBucket.aggregations.get<Terms>("url").buckets[0].keyAsString
          )
        }
      )
    }
  }
}

package com.simonjamesrowe.searchservice.core.repository

import com.simonjamesrowe.searchservice.core.model.IndexBlogRequest

interface BlogIndexRepository {

  fun indexBlog(request: IndexBlogRequest)

  fun indexBlogs(requests: Collection<IndexBlogRequest>)

  fun deleteBlog(id: String)

  fun deleteBlogs(id: Collection<String>)
}

package com.mavilon.mongomovies.data.remote.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.mavilon.mongomovies.data.mapper.toDomain
import com.mavilon.mongomovies.data.remote.api.TmdbApi
import com.mavilon.mongomovies.domain.model.Movie

class NowPlayingPagingSource(
    private val api: TmdbApi
) : PagingSource<Int, Movie>() {
    
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Movie> {
        return try {
            val page = params.key ?: 1
            val response = api.getNowPlaying(page)
            
            LoadResult.Page(
                data = response.results.map { it.toDomain() },
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (page < response.totalPages) page + 1 else null
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
    
    override fun getRefreshKey(state: PagingState<Int, Movie>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}

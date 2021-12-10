package com.ronnie.data.api

import com.ronnie.domain.ImageResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface PixaBayApi {

    @GET("api/")
    suspend fun searchImages(
        @Query("q") searchString: String?,
        @Query("per_page") per_page: Int? = null,
        @Query("page") page: Int? = null,
    ):ImageResponse
}
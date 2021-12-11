package com.ronnie.domain.usecases

import androidx.paging.ExperimentalPagingApi

interface BaseUseCase<in Payload, out Result> {
    @ExperimentalPagingApi
    suspend operator fun invoke(payload: Payload): Result
}
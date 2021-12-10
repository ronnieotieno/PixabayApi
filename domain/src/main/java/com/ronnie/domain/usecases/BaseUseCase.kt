package com.ronnie.domain.usecases

interface BaseUseCase<in Payload, out Result> {
    suspend operator fun invoke(payload: Payload): Result
}
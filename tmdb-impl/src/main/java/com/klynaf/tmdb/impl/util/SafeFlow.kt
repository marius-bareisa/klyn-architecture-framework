package com.klynaf.tmdb.impl.util

import com.klynaf.core.domain.util.Result
import com.klynaf.tmdb.impl.mapper.toResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import retrofit2.Response

fun <T> safeFlow(block: suspend FlowCollector<Result<T>>.() -> Unit): Flow<Result<T>> =
    flow(block).catch { e -> emit(Result.Error(e)) }

fun <T, R> fetchFlow(
    call: suspend () -> Response<T>,
    transform: (T) -> R,
): Flow<Result<R>> = safeFlow {
    emit(Result.Loading)
    when (val response = call().toResult()) {
        is Result.Success -> emit(Result.Success(transform(response.data)))
        is Result.Error -> emit(response)
        is Result.Loading -> Unit
    }
}

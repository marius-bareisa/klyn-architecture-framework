package com.klynaf.core.domain.util

inline fun <T, R> Result<T>.mapResult(transform: (T) -> R): Result<R> {
    return when (this) {
        is Result.Success -> Result.Success(transform(data))
        is Result.Error -> Result.Error(throwable)
        is Result.Loading -> Result.Loading
    }
}

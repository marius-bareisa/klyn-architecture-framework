package com.klynaf.tmdb.impl.mapper

import com.klynaf.core.domain.error.AuthException
import com.klynaf.core.domain.error.HttpException
import com.klynaf.core.domain.error.NetworkException
import com.klynaf.core.domain.error.NotFoundException
import com.klynaf.core.domain.error.ServerException
import com.klynaf.core.domain.util.Result
import retrofit2.Response
import java.io.IOException

fun <T> Response<T>.toResult(): Result<T> {
    return try {
        when {
            isSuccessful && body() != null -> Result.Success(body()!!)
            code() == 401 -> Result.Error(AuthException())
            code() == 404 -> Result.Error(NotFoundException())
            code() >= 500 -> Result.Error(ServerException(code()))
            else -> Result.Error(HttpException(code()))
        }
    } catch (e: IOException) {
        Result.Error(NetworkException(e))
    }
}

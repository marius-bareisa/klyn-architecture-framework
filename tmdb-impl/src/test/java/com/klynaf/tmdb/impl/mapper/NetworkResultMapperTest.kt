package com.klynaf.tmdb.impl.mapper

import com.klynaf.core.domain.error.AuthException
import com.klynaf.core.domain.error.HttpException
import com.klynaf.core.domain.error.NotFoundException
import com.klynaf.core.domain.error.ServerException
import com.klynaf.core.domain.util.Result
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import retrofit2.Response

internal class NetworkResultMapperTest {

    @Test
    fun `toResult with successful response and non-null body returns Success`() {
        val body = "Test Body"
        val response = Response.success(body)

        val result = response.toResult()

        assertTrue(result is Result.Success)
        assertEquals(body, (result as Result.Success).data)
    }

    @Test
    fun `toResult with successful response and null body returns Error`() {
        val response = Response.success<String>(null)

        val result = response.toResult()

        assertTrue(result is Result.Error)
        assertTrue((result as Result.Error).throwable is HttpException)
    }

    @Test
    fun `toResult with 401 response returns AuthException`() {
        val response = Response.error<String>(401, "".toResponseBody(null))

        val result = response.toResult()

        assertTrue(result is Result.Error)
        assertTrue((result as Result.Error).throwable is AuthException)
    }

    @Test
    fun `toResult with 404 response returns NotFoundException`() {
        val response = Response.error<String>(404, "".toResponseBody(null))

        val result = response.toResult()

        assertTrue(result is Result.Error)
        assertTrue((result as Result.Error).throwable is NotFoundException)
    }

    @Test
    fun `toResult with 500 response returns ServerException`() {
        val response = Response.error<String>(500, "".toResponseBody(null))

        val result = response.toResult()

        assertTrue(result is Result.Error)
        val exception = (result as Result.Error).throwable
        assertTrue(exception is ServerException)
        assertEquals(500, (exception as ServerException).code)
    }

    @Test
    fun `toResult with 503 response returns ServerException`() {
        val response = Response.error<String>(503, "".toResponseBody(null))

        val result = response.toResult()

        assertTrue(result is Result.Error)
        val exception = (result as Result.Error).throwable
        assertTrue(exception is ServerException)
        assertEquals(503, (exception as ServerException).code)
    }

    @Test
    fun `toResult with 400 response returns HttpException`() {
        val response = Response.error<String>(400, "".toResponseBody(null))

        val result = response.toResult()

        assertTrue(result is Result.Error)
        val exception = (result as Result.Error).throwable
        assertTrue(exception is HttpException)
        assertEquals(400, (exception as HttpException).code)
    }

    @Test
    fun `toResult with 429 response returns HttpException`() {
        val response = Response.error<String>(429, "".toResponseBody(null))

        val result = response.toResult()

        assertTrue(result is Result.Error)
        val exception = (result as Result.Error).throwable
        assertTrue(exception is HttpException)
        assertEquals(429, (exception as HttpException).code)
    }
}

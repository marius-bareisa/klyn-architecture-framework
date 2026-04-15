package com.klynaf.tmdb.impl.util

import app.cash.turbine.test
import com.klynaf.core.domain.error.NetworkException
import com.klynaf.core.domain.error.NotFoundException
import com.klynaf.core.domain.error.ServerException
import com.klynaf.core.domain.util.Result
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import retrofit2.Response
import java.io.IOException

@ExtendWith(MockKExtension::class)
internal class FetchFlowTest {

    @Nested
    inner class WhenCallSucceeds {

        @Test
        fun `GIVEN successful response WHEN fetchFlow THEN first emission is Loading`() = runTest {
            val call: suspend () -> Response<String> = { Response.success("data") }
            fetchFlow(call) { it.uppercase() }.test {
                assertTrue(awaitItem() is Result.Loading)
                cancelAndIgnoreRemainingEvents()
            }
        }

        @Test
        fun `GIVEN successful response WHEN fetchFlow THEN second emission is Success with transformed value`() =
            runTest {
                val call: suspend () -> Response<String> = { Response.success("hello") }
                fetchFlow(call) { it.uppercase() }.test {
                    awaitItem()
                    val result = awaitItem()
                    assertTrue(result is Result.Success)
                    assertEquals("HELLO", (result as Result.Success).data)
                    awaitComplete()
                }
            }

        @Test
        fun `GIVEN successful response WHEN fetchFlow THEN transform is applied to the response body`() =
            runTest {
                val call: suspend () -> Response<Int> = { Response.success(7) }
                fetchFlow(call) { it * 2 }.test {
                    awaitItem() // Loading
                    val result = awaitItem()
                    assertEquals(14, (result as Result.Success).data)
                    awaitComplete()
                }
            }
    }

    @Nested
    inner class WhenCallReturnsErrorResponse {

        @Test
        fun `GIVEN 401 response WHEN fetchFlow THEN emits Loading then Error`() = runTest {
            val call: suspend () -> Response<String> = {
                Response.error(401, "".toResponseBody(null))
            }
            fetchFlow(call) { it }.test {
                assertTrue(awaitItem() is Result.Loading)
                assertTrue(awaitItem() is Result.Error)
                awaitComplete()
            }
        }

        @Test
        fun `GIVEN 404 response WHEN fetchFlow THEN emits Loading then Error with NotFoundException`() =
            runTest {
                val call: suspend () -> Response<String> = {
                    Response.error(404, "".toResponseBody(null))
                }
                fetchFlow(call) { it }.test {
                    awaitItem()
                    val result = awaitItem() as Result.Error
                    assertTrue(result.throwable is NotFoundException)
                    awaitComplete()
                }
            }

        @Test
        fun `GIVEN 500 response WHEN fetchFlow THEN emits Loading then Error with ServerException`() =
            runTest {
                val call: suspend () -> Response<String> = {
                    Response.error(500, "".toResponseBody(null))
                }
                fetchFlow(call) { it }.test {
                    awaitItem()
                    val result = awaitItem() as Result.Error
                    assertTrue(result.throwable is ServerException)
                    awaitComplete()
                }
            }
    }

    @Nested
    inner class WhenCallThrows {

        @Test
        fun `GIVEN IOException thrown WHEN fetchFlow THEN emits Loading then Error with NetworkException`() =
            runTest {
                val call: suspend () -> Response<String> = {
                    throw IOException("no network")
                }
                fetchFlow(call) { it }.test {
                    assertTrue(awaitItem() is Result.Loading)
                    val result = awaitItem() as Result.Error
                    assertTrue(result.throwable is NetworkException)
                    awaitComplete()
                }
            }

        @Test
        fun `GIVEN IOException thrown WHEN fetchFlow THEN NetworkException wraps the original cause`() =
            runTest {
                val cause = IOException("timeout")
                val call: suspend () -> Response<String> = { throw cause }
                fetchFlow(call) { it }.test {
                    awaitItem()
                    val result = awaitItem() as Result.Error
                    val networkException = result.throwable as NetworkException
                    assertEquals(cause, networkException.cause)
                    awaitComplete()
                }
            }

        @Test
        fun `GIVEN non-IOException thrown WHEN fetchFlow THEN emits Loading then Error with original exception`() =
            runTest {
                val exception = RuntimeException("unexpected")
                val call: suspend () -> Response<String> = { throw exception }
                fetchFlow(call) { it }.test {
                    assertTrue(awaitItem() is Result.Loading)
                    val result = awaitItem() as Result.Error
                    assertEquals(exception, result.throwable)
                    awaitComplete()
                }
            }
    }
}

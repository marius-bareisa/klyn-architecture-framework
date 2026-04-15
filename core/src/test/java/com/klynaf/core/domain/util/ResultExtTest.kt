package com.klynaf.core.domain.util

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class ResultExtTest {

    @Test
    fun `GIVEN Result Success WHEN mapResult THEN transform is applied and returns Success`() {
        val result: Result<Int> = Result.Success(5)
        val mapped = result.mapResult { it * 3 }
        assertTrue(mapped is Result.Success)
        assertEquals(15, (mapped as Result.Success).data)
    }

    @Test
    fun `GIVEN Result Error WHEN mapResult THEN returns Error with same throwable`() {
        val throwable = RuntimeException("error")
        val result: Result<Int> = Result.Error(throwable)
        val mapped = result.mapResult { it * 3 }
        assertTrue(mapped is Result.Error)
        assertEquals(throwable, (mapped as Result.Error).throwable)
    }

    @Test
    fun `GIVEN Result Loading WHEN mapResult THEN returns Loading`() {
        val result: Result<Int> = Result.Loading
        val mapped = result.mapResult { it * 3 }
        assertTrue(mapped is Result.Loading)
    }

    @Test
    fun `GIVEN Result Error WHEN mapResult THEN transform is never applied`() {
        var transformCalled = false
        val result: Result<Int> = Result.Error(RuntimeException())
        result.mapResult { transformCalled = true; it }
        assertFalse(transformCalled)
    }

    @Test
    fun `GIVEN Result Loading WHEN mapResult THEN transform is never applied`() {
        var transformCalled = false
        val result: Result<Int> = Result.Loading
        result.mapResult { transformCalled = true; it }
        assertFalse(transformCalled)
    }
}

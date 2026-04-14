package com.klynaf.core.domain.error

class AuthException : Exception()
class NotFoundException : Exception()
class ServerException(val code: Int) : Exception()
class NetworkException(cause: Throwable) : Exception(cause)
class HttpException(val code: Int) : Exception()

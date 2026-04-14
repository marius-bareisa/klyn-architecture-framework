package com.klynaf.uicore.util

import android.content.Context
import com.klynaf.core.domain.error.AuthException
import com.klynaf.core.domain.error.HttpException
import com.klynaf.core.domain.error.NetworkException
import com.klynaf.core.domain.error.NotFoundException
import com.klynaf.core.domain.error.ServerException
import com.klynaf.uicore.R

fun Throwable.toUserMessage(context: Context): String = when (this) {
    is AuthException -> context.getString(R.string.core_error_unauthorized)
    is NotFoundException -> context.getString(R.string.core_error_not_found)
    is ServerException -> context.getString(R.string.core_error_server, this.code)
    is NetworkException -> context.getString(R.string.core_error_network)
    is HttpException -> context.getString(R.string.core_error_http, this.code)
    else -> context.getString(R.string.core_error_unknown)
}

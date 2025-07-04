package com.bunbeauty.tvstreamer.common.extension

import com.bunbeauty.tvstreamer.common.ApiError
import com.bunbeauty.tvstreamer.common.ApiResult

suspend fun <T, R> ApiResult<T>.getNullableResult(
    onError: (suspend (ApiError) -> R?)? = null,
    onSuccess: (suspend (T) -> R?)
): R? = when (this) {
    is ApiResult.Success -> {
        data?.let {
            onSuccess(data)
        } ?: onError?.invoke(ApiError.DATA_IS_NULL)
    }
    is ApiResult.Error -> {
        onError?.invoke(apiError)
    }
}

suspend fun <T> ApiResult<T>.onSuccess(
    block: (suspend (T) -> Unit)
) {
    if (this is ApiResult.Success) {
        block(this.data)
    }
}

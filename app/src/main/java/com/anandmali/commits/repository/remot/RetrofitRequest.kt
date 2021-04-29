package com.anandmali.commits.repository.remot

import androidx.annotation.StringRes
import com.anandmali.commits.R
import retrofit2.Response
import java.net.SocketTimeoutException
import java.net.UnknownHostException

fun <R> makeApiCall(response: Response<R>): NetworkResponse<R> {
    return try {
        handleNetworkResponse(response)
    } catch (e: Exception) {
        Either.Left(e.getError())
    }
}

private fun <R> handleNetworkResponse(
    response: Response<R>
): Either<NetworkError, R> {
    return try {
        if (response.isSuccessful) {
            when (response.code()) {
                in 200..202 -> {
                    response.body()?.run {
                        Either.Right(this)
                    } ?: Either.Left(NetworkError.ResponseParseError(response.code()))
                }
                else -> Either.Left(NetworkError.ResponseParseError(response.code()))
            }
        } else handleErrors(response)

    } catch (exception: Throwable) {
        exception.printStackTrace()
        Either.Left(exception.getError())
    }
}

private fun <R> handleErrors(
    response: Response<R>
): Either.Left<NetworkError> {
    return Either.Left(
        when (response.code()) {
            in 300..308 ->
                NetworkError.RedirectionError(response.code())
            in 400..417 ->
                NetworkError.ClientError(response.code())
            in 500..510 ->
                NetworkError.ServerError(response.code())
            else ->
                NetworkError.ClientError(response.code())
        }
    )
}


sealed class NetworkError(
    open val errorCode: Int,
    open val message: String? = null,
    @StringRes open val messageId: Int
) {

    data class ExceptionError(
        override val errorCode: Int = DEFAULT_ERROR_CODE,
        override val message: String? = null,
        override val messageId: Int = R.string.exception_err
    ) : NetworkError(errorCode, message, messageId)

    data class ResponseParseError(
        override val errorCode: Int = DEFAULT_ERROR_CODE,
        override val message: String? = null,
        override val messageId: Int = R.string.repsponse_err
    ) : NetworkError(errorCode, message, messageId)

    data class RedirectionError(
        override val errorCode: Int = DEFAULT_ERROR_CODE,
        override val message: String? = null,
        override val messageId: Int = R.string.redirection_err
    ) : NetworkError(errorCode, message, messageId)

    data class ClientError(
        override val errorCode: Int = DEFAULT_ERROR_CODE,
        override val message: String? = null,
        override val messageId: Int = R.string.client_err
    ) : NetworkError(errorCode, message, messageId)

    data class ServerError(
        override val errorCode: Int = DEFAULT_ERROR_CODE,
        override val message: String? = null,
        override val messageId: Int = R.string.server_err
    ) : NetworkError(errorCode, message, messageId)
}

fun Throwable.getError() =
    NetworkError.ExceptionError(
        errorCode = this.getErrorCode(),
        message = this.message
    )

const val UNKNOWN_HOST_EXCEPTION_ERROR_CODE = 1

const val SOCKET_TIME_OUT_EXCEPTION_ERROR_CODE = 2

const val DEFAULT_ERROR_CODE = 0

private fun Throwable.getErrorCode(): Int {
    return when (this) {
        is UnknownHostException -> UNKNOWN_HOST_EXCEPTION_ERROR_CODE
        is SocketTimeoutException -> SOCKET_TIME_OUT_EXCEPTION_ERROR_CODE
        else -> DEFAULT_ERROR_CODE
    }
}

typealias NetworkResponse<R> = Either<NetworkError, R>






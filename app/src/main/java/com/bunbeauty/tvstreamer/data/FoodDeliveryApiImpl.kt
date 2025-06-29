package com.bunbeauty.tvstreamer.data

import android.util.Log
import com.bunbeauty.tvstreamer.common.ApiError
import com.bunbeauty.tvstreamer.common.ApiResult
import com.bunbeauty.tvstreamer.common.Constants.WEB_SOCKET_TAG
import com.bunbeauty.tvstreamer.data.model.OrderServer
import com.bunbeauty.tvstreamer.data.model.ServerList
import com.bunbeauty.tvstreamer.data.model.user.UserAuthorizationRequest
import com.bunbeauty.tvstreamer.data.model.user.UserAuthorizationResponse
import com.bunbeauty.tvstreamer.data.model.user.UserResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ResponseException
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.WebSocketException
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpHeaders.Authorization
import io.ktor.http.HttpMethod
import io.ktor.http.path
import io.ktor.websocket.CloseReason
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import io.ktor.websocket.readText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.net.SocketException

class FoodDeliveryApiImpl(
    private val client: HttpClient,
    private val json: Json
) : FoodDeliveryApi {

    private var webSocketSession: DefaultClientWebSocketSession? = null

    private val mutableUpdatedOrderFlow = MutableSharedFlow<ApiResult<OrderServer>>()

    private val mutex = Mutex()

    private var webSocketSessionOpened = false

    override suspend fun login(
        userAuthorizationRequest: UserAuthorizationRequest
    ): ApiResult<UserAuthorizationResponse> {
        return post(
            path = "user/login",
            body = userAuthorizationRequest
        )
    }

    override suspend fun getUser(token: String): ApiResult<UserResponse> {
        return get(
            path = "user",
            token = token
        )
    }

    override suspend fun getOrderListByCafeUuid(
        token: String,
        cafeUuid: String
    ): ApiResult<ServerList<OrderServer>> {
        return get(
            path = "order",
            parameters = mapOf("cafeUuid" to cafeUuid),
            token = token
        )
    }

    override suspend fun getUpdatedOrderFlowByCafeUuid(
        token: String,
        cafeUuid: String
    ): Flow<ApiResult<OrderServer>> {
        mutex.withLock {
            if (!webSocketSessionOpened) {
                subscribeOnOrderUpdates(token, cafeUuid)
            }
        }
        return mutableUpdatedOrderFlow.asSharedFlow()
    }

    private fun subscribeOnOrderUpdates(token: String, cafeUuid: String) {
        CoroutineScope(Job() + IO).launch {
            try {
                webSocketSessionOpened = true
                client.webSocket(
                    HttpMethod.Get,
                    path = "/user/order/pickup/subscribe",
                    port = 80,
                    request = {
                        header("Authorization", "Bearer $token")
                        parameter("cafeUuid", cafeUuid)
                    }
                ) {
                    Log.d(WEB_SOCKET_TAG, "WebSocket connected")
                    webSocketSession = this
                    while (true) {
                        val message = incoming.receive() as? Frame.Text ?: continue
                        Log.d(WEB_SOCKET_TAG, "Message: ${message.readText()}")
                        val serverModel =
                            json.decodeFromString(OrderServer.serializer(), message.readText())
                        mutableUpdatedOrderFlow.emit(ApiResult.Success(serverModel))
                    }
                }
            } catch (exception: WebSocketException) {
                Log.e(WEB_SOCKET_TAG, "WebSocketException: ${exception.message}")
                mutableUpdatedOrderFlow.emit(ApiResult.Error(ApiError(message = exception.message.toString())))
            } catch (exception: SocketException) {
                Log.e(WEB_SOCKET_TAG, "SocketException: ${exception.message}")
                mutableUpdatedOrderFlow.emit(ApiResult.Error(ApiError(message = exception.message.toString())))
            } catch (exception: ClosedReceiveChannelException) {
                Log.d(WEB_SOCKET_TAG, "ClosedReceiveChannelException: ${exception.message}")
                // Nothing
            } catch (exception: Exception) {
                val stackTrace = exception.stackTrace.joinToString("\n") {
                    "${it.className} ${it.methodName} ${it.lineNumber}"
                }
                Log.e(WEB_SOCKET_TAG, "Exception: $exception \n$stackTrace")
                mutableUpdatedOrderFlow.emit(ApiResult.Error(ApiError(message = exception.message.toString())))
            } finally {
                webSocketSessionOpened = false
            }
        }
    }

    override suspend fun unsubscribeOnOrderList(message: String) {
        if (webSocketSession != null) {
            webSocketSession?.close(CloseReason(CloseReason.Codes.NORMAL, message))
            webSocketSession = null

            Log.d(WEB_SOCKET_TAG, "webSocketSession closed ($message)")
        }
    }

    private suspend inline fun <reified T> get(
        path: String,
        parameters: Map<String, String?> = mapOf(),
        token: String? = null
    ): ApiResult<T> {
        return safeCall {
            client.get {
                buildRequest(
                    path = path,
                    body = null,
                    parameters = parameters,
                    token = token
                )
            }
        }
    }

    private suspend inline fun <reified T> post(
        path: String,
        body: Any,
        parameters: Map<String, String> = mapOf(),
        token: String? = null
    ): ApiResult<T> {
        return safeCall {
            client.post {
                buildRequest(
                    path = path,
                    body = body,
                    parameters = parameters,
                    token = token
                )
            }
        }
    }

    private suspend inline fun <reified T> put(
        path: String,
        body: Any,
        parameters: Map<String, String> = mapOf(),
        token: String? = null
    ): ApiResult<T> {
        return safeCall {
            client.put {
                buildRequest(
                    path = path,
                    body = body,
                    parameters = parameters,
                    token = token
                )
            }
        }
    }

    private suspend inline fun <reified T> patch(
        path: String,
        body: Any,
        parameters: Map<String, String?> = mapOf(),
        token: String? = null
    ): ApiResult<T> {
        return safeCall {
            client.patch {
                buildRequest(
                    path = path,
                    body = body,
                    parameters = parameters,
                    token = token
                )
            }
        }
    }

    private suspend inline fun <reified T> delete(
        path: String,
        parameters: Map<String, String?> = mapOf(),
        token: String? = null
    ): ApiResult<T> {
        return safeCall {
            client.delete {
                buildRequest(
                    path = path,
                    body = null,
                    parameters = parameters,
                    token = token
                )
            }
        }
    }

    private fun HttpRequestBuilder.buildRequest(
        path: String,
        body: Any?,
        parameters: Map<String, String?> = mapOf(),
        token: String? = null
    ) {
        if (body != null) {
            setBody(body)
        }
        url {
            path(path)
        }
        parameters.forEach { parameterMap ->
            parameter(parameterMap.key, parameterMap.value)
        }
        header(Authorization, "Bearer $token")
    }

    private suspend inline fun <reified R> safeCall(
        crossinline networkCall: suspend () -> HttpResponse
    ): ApiResult<R> {
        return try {
            withContext(IO) {
                ApiResult.Success(networkCall().body())
            }
        } catch (exception: ResponseException) {
            ApiResult.Error(ApiError(exception.response.status.value, exception.message ?: ""))
        } catch (exception: Throwable) {
            ApiResult.Error(ApiError(0, exception.message ?: "Bad Internet"))
        }
    }
}

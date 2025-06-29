package com.bunbeauty.tvstreamer.data.di

import android.util.Log
import com.bunbeauty.tvstreamer.data.FoodDeliveryApi
import com.bunbeauty.tvstreamer.data.FoodDeliveryApiImpl
import com.bunbeauty.tvstreamer.data.ServerOrderMapper
import com.bunbeauty.tvstreamer.data.repository.datastore.DataStoreRepository
import com.bunbeauty.tvstreamer.presentation.repo.OrderRepo
import com.bunbeauty.tvstreamer.data.repository.order.OrderRepository
import com.bunbeauty.tvstreamer.data.repository.user.UserRepoImpl
import com.bunbeauty.tvstreamer.presentation.repo.DataStoreRepo
import com.bunbeauty.tvstreamer.presentation.repo.UserRepo
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.URLProtocol
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.dsl.module

fun dataModule() = module {
    single<OrderRepo> {
        OrderRepository(
            foodDeliveryApi = get(),
            serverOrderMapper = get()
        )
    }
    single<UserRepo> {
        UserRepoImpl(
            foodDeliveryApi = get(),
            dataStoreRepo = get(),
        )
    }

    single<FoodDeliveryApi> {
        FoodDeliveryApiImpl(
            client = get(),
            json = get()
        )
    }

    factory {
        ServerOrderMapper()
    }
}

fun dataSourceModule() = module {
    single {
        Json {
            isLenient = false
            ignoreUnknownKeys = true
        }
    }
    single<DataStoreRepo> {
        DataStoreRepository(
            context = get()
        )
    }
    single {
        HttpClient(OkHttp.create()) {
            install(ContentNegotiation) {
                json(
                    Json {
                        prettyPrint = true
                        isLenient = true
                        ignoreUnknownKeys = true
                        encodeDefaults = false
                    }
                )
            }
            install(WebSockets)
            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        Log.d("Ktor", message)
                    }
                }
                level = LogLevel.ALL
            }
            install(HttpTimeout) {
                requestTimeoutMillis = 10000
            }
            install(DefaultRequest) {
                host = "food-delivery-api-bunbeauty.herokuapp.com"
                header(HttpHeaders.ContentType, ContentType.Application.Json)
                contentType(ContentType.Application.Json)

                url {
                    protocol = URLProtocol.HTTPS
                }
            }
        }
    }
}

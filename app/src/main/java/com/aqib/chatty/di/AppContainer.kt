package com.aqib.chatty.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.aqib.chatty.BuildConfig
import com.aqib.chatty.data.AccessTokenPreferencesRepository
import com.aqib.chatty.data.NetworkUserRepository
import com.aqib.chatty.data.UserRespository
import com.aqib.chatty.network.ChattyApiService
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit

/**
 * AppContainer is an interface that defines the contract for the application's dependency container.
 * It declares properties for user repository and access token preferences repository.
 */
interface AppContainer {
    val userRespository: UserRespository
    val accessTokenPreferencesRepository: AccessTokenPreferencesRepository
}

// Define a constant for the name of the access token
private const val ACCESS_TOKEN_NAME = "access_token"

// Define a property delegate for the data store of the context
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = ACCESS_TOKEN_NAME
)

/**
 * DefaultContainer is a class that implements the AppContainer interface.
 * It uses a network API service to perform the user-related operations and manages access tokens and usernames in a data store.
 *
 * @property context The context of the application.
 */
class DefaultContainer(
    private val context: Context
) : AppContainer {
    val baseUrl = BuildConfig.SERVER_URL
    private val json = Json { ignoreUnknownKeys = true }

    // Define a Retrofit instance for making network requests
    private val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()

    // Define a lazy property for the network API service
    private val retrofitService by lazy {
        retrofit.create(ChattyApiService::class.java)
    }

    // Define a lazy property for the user repository
    override val userRespository: UserRespository by lazy {
        NetworkUserRepository(retrofitService)
    }

    // Define a lazy property for the access token preferences repository
    override val accessTokenPreferencesRepository: AccessTokenPreferencesRepository by lazy {
        AccessTokenPreferencesRepository(this.context.dataStore)
    }
}
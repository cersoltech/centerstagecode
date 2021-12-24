package com.digitalsignage.androidplayer.repository

import androidx.annotation.MainThread
import com.digitalsignage.androidplayer.utils.State
import com.digitalsignage.androidplayer.utils.State.Companion.STATE_CODE_SERVER_UNREACHABLE
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import retrofit2.Response


/**
 * A repository which provides resource from local database as well as remote end point.
 * Currently no database as such is used so we'll just retrieve & send data from network
 *
 * [REQUEST] represents the type for network.
 */
@ExperimentalCoroutinesApi
abstract class NetworkBoundRepository<REQUEST> {

    fun asFlow() = flow<State<REQUEST>> {

        // Emit Loading State
        emit(State.loading())

        // Fetch latest posts from remote
        val apiResponse = fetchFromRemote()

        // Parse body
        val remotePosts = apiResponse.body()

        // Check for response validation
        if (apiResponse.isSuccessful && remotePosts != null) {
            // Retrieve data from network & emit
            emit(State.Success(apiResponse.code(),remotePosts))
        } else {
            // Something went wrong! Emit Error state.
            emit(State.error(apiResponse.code(),apiResponse.message()))
        }

    }.catch { e ->
        // Exception occurred! Emit error
        emit(State.error(STATE_CODE_SERVER_UNREACHABLE,"Network error! : ${e.message}"))
        e.printStackTrace()
    }

    /**
     * Fetches [Response] from the remote end point.
     */
    @MainThread
    protected abstract suspend fun fetchFromRemote(): Response<REQUEST>
}

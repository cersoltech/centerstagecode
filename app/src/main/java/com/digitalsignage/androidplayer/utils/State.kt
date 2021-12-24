package com.digitalsignage.androidplayer.utils

/**
 * State Management for UI
 */
sealed class State<T> {
    class Loading<T> : State<T>()

    data class Success<T>(val code: Int, val data: T) : State<T>()

    data class Error<T>(val code: Int, val message: String) : State<T>()

    companion object {

        val STATE_CODE_SIMULATED_OK = 2500
        val STATE_CODE_SERVER_UNREACHABLE = 2501

        /**
         * Returns [State.Loading] instance.
         */
        fun <T> loading() = Loading<T>()

        /**
         * Returns [State.Success] instance.
         * @param data com.vialis.app.data.remote.response.com.vialis.app.data.remote.response.Data to emit with status.
         */
        fun <T> success(code:Int, data: T) =
            Success(code, data)

        /**
         * Returns [State.Error] instance.
         * @param message Description of failure.
         */
        fun <T> error(code:Int, message: String) =
            Error<T>(code, message)
    }
}

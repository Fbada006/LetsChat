package com.example.letschat.utils

import io.ably.lib.realtime.AblyRealtime
import io.ably.lib.realtime.Channel
import io.ably.lib.realtime.CompletionListener
import io.ably.lib.realtime.ConnectionState
import io.ably.lib.realtime.ConnectionStateListener
import io.ably.lib.types.AblyException
import io.ably.lib.types.ErrorInfo
import timber.log.Timber

class AblyConnection {

    companion object {
        private const val API_KEY = "API_KEY_GOES_HERE"
        private const val ABLY_CHANNEL_NAME = "mobile:chat"
        private const val TAG = "AblyConnectionUtils"
    }

    private lateinit var sessionChannel: Channel
    private lateinit var ablyRealtime: AblyRealtime
    private lateinit var messageListener: Channel.MessageListener

    @Throws(AblyException::class)
    fun initListener(listener: Channel.MessageListener) {
        sessionChannel.subscribe(listener)
        messageListener = listener
    }

    @Throws(AblyException::class)
    fun establishConnectionForID(callback: ConnectionCallback) {
        ablyRealtime = AblyRealtime(API_KEY)

        ablyRealtime.connection.on(ConnectionStateListener { connectionStateChange ->
            when (connectionStateChange.current) {
                ConnectionState.closed -> {
                    // Ignore this
                }
                ConnectionState.initialized -> {
                    // Ignore this
                }
                ConnectionState.connecting -> {
                    // Ignore this
                }
                ConnectionState.connected -> {
                    sessionChannel = ablyRealtime.channels[ABLY_CHANNEL_NAME]

                    try {
                        sessionChannel.attach()
                        callback.onConnectionCallback(null)
                    } catch (e: AblyException) {
                        callback.onConnectionCallback(e)
                        Timber.e(e, "Something went wrong attaching channel! ")
                        return@ConnectionStateListener
                    }
                }
                ConnectionState.disconnected -> {
                    callback.onConnectionCallback(
                        Exception("$TAG Ably connection was disconnected. We will retry connecting again in 30 seconds.")
                    )
                }
                ConnectionState.suspended -> {
                    callback.onConnectionCallback(
                        Exception("$TAG Ably connection was suspended. We will retry connecting again in 60 seconds.")
                    )
                }
                ConnectionState.closing -> {
                    sessionChannel.unsubscribe(messageListener)
                }
                ConnectionState.failed -> {
                    callback.onConnectionCallback(Exception("$TAG We're sorry, Ably connection failed. Please restart the app."))
                }
                else -> {
                    throw RuntimeException("Unknown state received ${connectionStateChange.current}")
                }
            }
        })
    }

    @Throws(AblyException::class)
    fun sendMessage(userName: String, message: String?, callback: ConnectionCallback) {
        sessionChannel.publish(userName, message, object : CompletionListener {
            override fun onSuccess() {
                callback.onConnectionCallback(null)
                Timber.d("Message sent!!!")
            }

            override fun onError(errorInfo: ErrorInfo) {
                callback.onConnectionCallback(Exception(errorInfo.message))
            }
        })
    }

}
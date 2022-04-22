package com.example.letschat.utils

import com.example.letschat.BuildConfig
import io.ably.lib.realtime.AblyRealtime
import io.ably.lib.realtime.Channel
import io.ably.lib.realtime.CompletionListener
import io.ably.lib.realtime.ConnectionState
import io.ably.lib.realtime.ConnectionStateListener
import io.ably.lib.types.AblyException
import io.ably.lib.types.ChannelOptions
import io.ably.lib.types.ErrorInfo
import io.ably.lib.util.Crypto.generateRandomKey
import timber.log.Timber


object AblyConnection {

    private const val API_KEY = BuildConfig.API_KEY
    private const val ABLY_CHANNEL_NAME = "mobile:chat"
    private const val TAG = "AblyConnectionUtils"
    private val CIPHER_KEY_BASE64 = generateRandomKey()

    private lateinit var sessionChannel: Channel
    private lateinit var ablyRealtime: AblyRealtime
    private lateinit var messageListener: Channel.MessageListener
    private var userName: String? = null

    fun initListener(listener: Channel.MessageListener) {
        sessionChannel.subscribe(listener)
        messageListener = listener
    }

    fun establishConnectionForID(userName: String, callback: AblyConnectionCallback) {
        this.userName = userName

        ablyRealtime = AblyRealtime(API_KEY)
        val options = ChannelOptions.withCipherKey(CIPHER_KEY_BASE64)

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
                    sessionChannel = ablyRealtime.channels[ABLY_CHANNEL_NAME, options]

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

    fun sendMessage(message: String?, callback: AblyConnectionCallback) {
        sessionChannel.publish(userName, message, object : CompletionListener {
            override fun onSuccess() {
                callback.onConnectionCallback(null)
                Timber.d("Message sent!!!")
            }

            override fun onError(errorInfo: ErrorInfo) {
                Timber.e(errorInfo.toString())
                callback.onConnectionCallback(Exception(errorInfo.message))
            }
        })
    }

}
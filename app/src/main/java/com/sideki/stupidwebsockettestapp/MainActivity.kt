package com.sideki.stupidwebsockettestapp

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import okio.ByteString

const val NORMAL_CLOSURE_STATUS = 1000

class MainActivity : AppCompatActivity() {

    private lateinit var btnStart: Button
    private lateinit var textView: TextView
    private lateinit var client: OkHttpClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnStart = findViewById(R.id.start)
        textView = findViewById(R.id.output)

        client = OkHttpClient()

        btnStart.setOnClickListener {
            start()
        }
    }

    inner class EchoWebSocketListener : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: Response?) {
            webSocket.send("Hello, it's SSaurel !")
            webSocket.send("What's up ?")
            webSocket.send(ByteString.decodeHex("deadbeef"))
            webSocket.close(NORMAL_CLOSURE_STATUS, "Goodbye !")
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            output("Receiving : $text")
        }

        override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
            output("Receiving bytes : " + bytes.hex())
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            webSocket.close(NORMAL_CLOSURE_STATUS, null)
            output("Closing : $code / $reason")
        }

        override fun onFailure(webSocket: WebSocket?, t: Throwable, response: Response?) {
            output("Error : " + t.message)
        }
    }

    private fun start() {
        val request = Request.Builder().url("ws://echo.websocket.org").build()
        val listener = EchoWebSocketListener()
        client.newWebSocket(request, listener)
        client.dispatcher().executorService().shutdown()
    }

    private fun output(txt: String) {
        runOnUiThread {textView.text = textView.text.toString() + "\n\n" + txt}
    }
}
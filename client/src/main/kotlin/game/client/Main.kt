package game.client

import game.common.ClientMessageUnion
import game.common.HelloMessage
import kotlinx.serialization.json.JSON
import org.w3c.dom.WebSocket


const val serverUrl = "ws://localhost:8887"

fun main(args: Array<String>) {
    val socket = WebSocket(serverUrl)

    socket.onmessage = { event ->
        val message = event.asDynamic().data as String
        println("Received message: $message")
    }

    socket.onopen = {
        socket.send(JSON.stringify(ClientMessageUnion(helloMessage = HelloMessage("abcd"))))
    }
}

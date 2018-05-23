package game.client

import game.common.ClientMessageUnion
import game.common.HelloMessage
import kotlinx.html.dom.create
import kotlinx.html.js.canvas
import kotlinx.serialization.json.JSON
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.MessageEvent
import org.w3c.dom.WebSocket
import kotlin.browser.document
import kotlin.math.PI


const val serverUrl = "ws://localhost:8887"

fun main(args: Array<String>) {
    val socket = WebSocket(serverUrl)

    socket.onmessage = { event ->
        val message = (event as MessageEvent).data
        println("Received message: $message")
    }

    socket.onopen = {
        socket.send(JSON.stringify(ClientMessageUnion(helloMessage = HelloMessage("abcd"))))
    }

    val canvas = document.create.canvas {
        width = "640px"
        height = "480px"
    }.also { document.body!!.append(it) }

    val ctx = canvas.getContext("2d") as CanvasRenderingContext2D

    ctx.moveTo(0.0, 0.0)
    ctx.lineTo(200.0, 100.0)
    ctx.stroke()

    ctx.beginPath();
    ctx.arc(95.0, 50.0, 40.0, 0.0, 2 * PI);
    ctx.stroke();
}

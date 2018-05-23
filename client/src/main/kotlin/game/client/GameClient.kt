package game.client

import game.common.*
import kotlinx.html.dom.create
import kotlinx.html.js.canvas
import kotlinx.serialization.json.JSON
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.MessageEvent
import org.w3c.dom.WebSocket
import org.w3c.dom.events.KeyboardEvent
import kotlin.browser.document
import kotlin.browser.window


private const val serverUrl = "ws://localhost:8887"

private const val left = 37
private const val up = 38
private const val right = 39
private const val down = 40

private const val segmentWidth = 16.0
private const val segmentHeight = segmentWidth

class GameClient {
    private var worldState: WorldState? = null

    private var direction = Vec2i(0, 0)

    private val socket = WebSocket(serverUrl)

    val canvas = document.create.canvas {
        width = "640px"
        height = "480px"
    }

    private val context = canvas.getContext("2d") as CanvasRenderingContext2D

    init {
        document.addEventListener("keydown", { event ->
            handleKeyDown(event as KeyboardEvent)
        }, false);

        socket.onmessage = { event ->
            handleMessage(event as MessageEvent)
        }

//        socket.onopen = {
//            window.setInterval({ sendCommand() }, 50)
//        }

        fun callback() {
            render()
            window.requestAnimationFrame { callback() }
        }

        window.requestAnimationFrame { callback() }
    }

    private fun handleKeyDown(event: KeyboardEvent) {
        when (event.keyCode) {
            left -> direction = Vec2i(-1, 0)
            right -> direction = Vec2i(1, 0)
            up -> direction = Vec2i(0, -1)
            down -> direction = Vec2i(0, 1)
        }

        event.preventDefault()

        sendCommand()
    }

    private fun sendCommand() {
        try {
            socket.send(JSON.stringify(ClientMessage(
                    clientCommand = ClientCommandMessage(direction)
            )))
        } catch (ex: Exception) {
        }
    }

    private fun handleMessage(event: MessageEvent) {
        val messageJson = event.data as String
        JSON.parse<ServerMessage>(messageJson).run {
            println("Received message: $this")
            worldUpdate?.let { handleWorldUpdate(it) }
            Unit
        }
    }

    private fun handleWorldUpdate(worldUpdateMessage: WorldUpdateMessage) {
        worldState = worldUpdateMessage.worldState
    }

    private fun render() {
        context.clearRect(0.0, 0.0, canvas.width.toDouble(), canvas.height.toDouble())
        worldState?.let { worldState ->
            worldState.snakes.forEach { snake ->
                snake.segments.forEach { segment ->
                    context.beginPath()
                    context.rect(
                            segment.x * segmentWidth,
                            segment.y * segmentHeight,
                            segmentWidth,
                            segmentHeight)
                    context.fill()
                    context.closePath()
                }
            }
        }
    }
}

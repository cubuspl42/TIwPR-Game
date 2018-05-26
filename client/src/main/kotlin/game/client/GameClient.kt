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


private const val fruitOffset = 4.0
private const val fruitWidth = 8.0
private const val fruitHeight = fruitWidth

class GameClient(
        private val worldId: Int,
        private val userUuid: String
) {
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

        socket.onopen = {
            sendMessage(ClientMessage(hello = HelloMessage(userUuid)))
            sendMessage(ClientMessage(enteredWorld = EnteredWorldMessage(worldId)))
        }

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
            sendMessage(ClientMessage(
                    clientCommand = ClientCommandMessage(direction)
            ))
        } catch (ex: Exception) {
        }
    }

    private fun sendMessage(message: ClientMessage) {
        socket.send(JSON.stringify(message))
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
                    context.fillStyle = "darkgray"
                    context.rect(
                            segment.x * segmentWidth,
                            segment.y * segmentHeight,
                            segmentWidth,
                            segmentHeight)
                    context.fill()
                    context.closePath()
                }
            }

            context.fillStyle = "blue"
            context.beginPath()
            context.rect(
                    worldState.fruit.x * segmentWidth + fruitOffset,
                    worldState.fruit.y * segmentHeight + fruitOffset,
                    fruitWidth,
                    fruitHeight)
            context.fill()
            context.closePath()
        }
    }
}

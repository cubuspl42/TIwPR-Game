package game.server

import game.common.*
import kotlinx.serialization.json.JSON
import org.java_websocket.WebSocket
import java.util.concurrent.ConcurrentLinkedQueue

private const val tickrate = 4

sealed class Event

data class ClientConnectedEvent(
        val clientUuid: String,
        val socket: WebSocket
) : Event()

data class ClientDisconnectedEvent(
        val socket: WebSocket
) : Event()

data class ClientMessageEvent(
        val clientMessage: ClientMessage
) : Event()

class Client(
        val socket: WebSocket
) {
    fun handleMessage(message: ServerMessage) {
        socket.send(JSON.stringify(message))
    }
}

class GameServer : Runnable {
    private val clients = mutableSetOf<Client>()

    private val world = World()

    private val eventQueue = ConcurrentLinkedQueue<Event>()

    fun handleEvent(event: Event) {
        eventQueue.add(event)
    }

    override fun run() {
        while (true) {
            processEvents()
            updateWorld()
            broadcastWorldState()

            Thread.sleep((1000 / tickrate).toLong())
        }
    }

    private fun processEvents() {
        generateSequence { eventQueue.poll() }.forEach {
            processEvent(it)
        }
    }

    private fun processEvent(event: Event) {
        when (event) {
            is ClientConnectedEvent -> {
                clients.add(Client(event.socket))
            }
            is ClientDisconnectedEvent -> {
                clients.remove(findClientBySocket(event.socket))
            }
            is ClientMessageEvent -> {
                processMessage(event.clientMessage)
            }
        }
    }

    private fun findClientBySocket(socket: WebSocket) =
            clients.first { it.socket == socket }

    private fun processMessage(message: ClientMessage) {
    }

    private fun updateWorld() {
        world.update()
    }

    private fun broadcastWorldState() {
        clients.forEach {
            it.handleMessage(ServerMessage(WorldUpdateMessage(world.dump())))
        }
    }
}
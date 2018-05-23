package game.server

import game.common.*
import kotlinx.serialization.json.JSON
import org.java_websocket.WebSocket
import java.util.concurrent.ConcurrentLinkedQueue

private const val tickrate = 4

sealed class Event

data class ClientConnectedEvent(
        val clientId: Int,
        val socket: WebSocket
) : Event()

data class ClientDisconnectedEvent(
        val clientId: Int
) : Event()

data class ClientMessageEvent(
        val clientId: Int,
        val clientMessage: ClientMessage
) : Event()

class Client(
        val id: Int,
        private val socket: WebSocket
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
                val client = Client(event.clientId, event.socket)
                clients.add(client)
                world.addSnake(client.id)
            }
            is ClientDisconnectedEvent -> {
                clients.remove(findClientById(event.clientId))
            }
            is ClientMessageEvent -> {
                processMessage(event.clientId, event.clientMessage)
            }
        }
    }

    private fun findClientById(id: Int) =
            clients.first { it.id == id }

    private fun processMessage(clientId: Int, message: ClientMessage) {
        message.clientCommand?.let {
            world.controlSnake(clientId, it.direction)
        }
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
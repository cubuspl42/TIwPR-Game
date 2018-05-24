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
        private val socket: WebSocket,
        var world: World? = null
) {
    fun handleMessage(message: ServerMessage) {
        socket.send(JSON.stringify(message))
    }
}

class GameServer : Runnable {
    private val clients = mutableSetOf<Client>()

    private val worlds = (0 until worldCount).map { it to World(it) }.toMap()

    private val eventQueue = ConcurrentLinkedQueue<Event>()

    fun handleEvent(event: Event) {
        eventQueue.add(event)
    }

    override fun run() {
        while (true) {
            processEvents()
            updateWorlds()
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
            }
            is ClientDisconnectedEvent -> {
                val client = findClientById(event.clientId)
                clients.remove(client)
                client.world?.removeSnake(client)
            }
            is ClientMessageEvent -> {
                processMessage(event.clientId, event.clientMessage)
            }
        }
    }

    private fun findClientById(id: Int) =
            clients.first { it.id == id }

    private fun processMessage(clientId: Int, message: ClientMessage) {
        val client = findClientById(clientId)
        message.clientCommand?.let {
            client.world?.controlSnake(clientId, it.direction)
        }
        message.enteredWorld?.let {
            val world = worlds[it.worldId]!!
            client.world = world
            world.addSnake(client)
        }
    }

    private fun updateWorlds() {
        worlds.forEach { _, world ->
            world.update()
        }
    }

    private fun broadcastWorldState() {
        worlds.forEach { _, world ->
            world.broadcastState()
        }
    }
}
package game.server

import game.common.ServerMessage
import game.common.Vec2i
import game.common.WorldState
import game.common.WorldUpdateMessage
import java.util.*


const val worldWidth = 10
const val worldHeight = 10

class World(val worldId: Int) {
    private val rand = Random()

    private val snakes = mutableListOf<Snake>()

    private var fruit = randomPosition()

    private fun randomPosition() = Vec2i(rand.nextInt(worldWidth), rand.nextInt(worldHeight))

    fun addSnake(client: Client) {
        snakes.add(Snake(Vec2i(0, 0), client))
    }

    fun update() {
        snakes.forEach { it.advance() }
        snakes.forEach {
            if (it.head == fruit) {
                it.points++
                createFruit()
            }
        }
    }

    private fun createFruit() {
        fruit = randomPosition()
    }

    fun dump() = WorldState(snakes.map { it.dump() }, fruit)

    fun controlSnake(clientId: Int, direction: Vec2i) {
        snakes.first { it.client.id == clientId }.velocity = direction
    }

    fun broadcastState() {
        snakes.forEach {
            it.client.handleMessage(ServerMessage(WorldUpdateMessage(dump())))
        }
    }

    fun removeSnake(client: Client) {
        snakes.removeAll { it.client == client }
    }
}

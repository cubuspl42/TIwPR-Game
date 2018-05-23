package game.server

import game.common.Vec2i
import game.common.WorldState


const val worldWidth = 10
const val worldHeight = 10

class World {
    private val snakes = mutableListOf<Snake>()

    fun addSnake(clientId: Int) {
        snakes.add(Snake(Vec2i(0, 0), clientId))
    }

    fun update() {
        snakes.forEach { it.advance() }
    }

    fun dump() = WorldState(snakes.map { it.dump() })

    fun controlSnake(clientId: Int, direction: Vec2i) {
        snakes.first { it.clientId == clientId }.velocity = direction
    }
}
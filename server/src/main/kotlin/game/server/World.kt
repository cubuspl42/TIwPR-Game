package game.server

import game.common.Vec2i
import game.common.WorldState


const val worldWidth = 10
const val worldHeight = 10

class World {
    private val snakes = mutableListOf(Snake(Vec2i(1, 1)))

    fun update() {
        snakes.forEach { it.advance() }
    }

    fun dump() = WorldState(snakes.map { it.dump() })
}
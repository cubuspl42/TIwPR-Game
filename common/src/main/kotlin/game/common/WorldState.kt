package game.common

import kotlinx.serialization.Serializable

const val worldWidth = 10
const val worldHeight = 10

@Serializable
data class Snake(
        val segments: List<Vec2i>
)

@Serializable
data class WorldState(
        val snakes: List<Snake>
)
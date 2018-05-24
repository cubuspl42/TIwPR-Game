package game.common

import kotlinx.serialization.Serializable


@Serializable
data class WsSnake(
        val segments: List<Vec2i>,
        val points: Int
)

@Serializable
data class WorldState(
        val snakes: List<WsSnake>,
        val fruit: Vec2i
)
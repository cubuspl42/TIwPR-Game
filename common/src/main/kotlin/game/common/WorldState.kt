package game.common

import kotlinx.serialization.Serializable


@Serializable
data class WsSnake(
        val segments: List<Vec2i>
)

@Serializable
data class WorldState(
        val snakes: List<WsSnake>
)
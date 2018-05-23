package game.common

import kotlinx.serialization.Optional
import kotlinx.serialization.Serializable

/**
 * Must be implemented by all modules and return the one and only answer Answer to the Ultimate Question of Life,
 * the Universe, and Everything. This is not the best example of a multi-platform usage, since we expect it to
 * return the same number on all platforms, but you get the idea.
 */
expect fun getAnswer(): Int

@Serializable
data class Vec2i(val x: Int, val y: Int)

@Serializable
data class HelloMessage(
        val userUuid: String
)

@Serializable
data class ClientCommandMessage(
        val direction: Vec2i
)

@Serializable
data class ClientMessage(
        val hello: HelloMessage? = null,
        val clientCommand: ClientCommandMessage? = null
)

@Serializable
data class WorldUpdateMessage(
        val worldState: WorldState
)

@Serializable
data class ServerMessage(
        val worldUpdate: WorldUpdateMessage? = null
)

package game.server

import game.common.Vec2i
import game.common.WsSnake

class Snake(initialPosition: Vec2i) {
    private val _segments = mutableListOf(initialPosition)

    val segments: List<Vec2i> = _segments

    var velocity = Vec2i(1, 0)
        set(value) {
            if (value != -field) {
                field = value
            }
        }

    fun advance() {
        val head = _segments.first()
        val headNext = head + velocity

        var x = headNext.x
        var y = headNext.y

        if (x < 0) x += worldWidth
        if (y < 0) y += worldHeight

        x %= worldWidth
        y %= worldWidth

        _segments.add(0, Vec2i(x, y))
        _segments.removeAt(_segments.size - 1);
    }

    fun dump(): WsSnake {
        return WsSnake(segments)
    }
}

package game.client

import game.common.worldCount
import kotlinx.html.button
import kotlinx.html.dom.append
import kotlinx.html.js.onClickFunction
import kotlinx.html.js.ul
import kotlinx.html.li
import org.w3c.dom.HTMLElement

class App(
       private val wrapper: HTMLElement
) {
    private val ul = wrapper.append.ul {
        repeat(worldCount) { worldId ->
            li {
                button {
                    onClickFunction = { enterWorld(worldId) }
                    +"World $worldId"
                }
            }
        }
    }

    private fun enterWorld(worldId: Int) {
        ul.remove()
        wrapper.append(GameClient(worldId).canvas)
    }
}
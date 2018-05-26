package game.client

import org.w3c.dom.get
import org.w3c.dom.set
import kotlin.browser.document
import kotlin.browser.sessionStorage
import kotlin.js.Math

fun main(args: Array<String>) {
//    val gameClient = GameClient()
//    document.body!!.append(gameClient.canvas)
    val userUuid = sessionStorage.get("userUuid") ?: run {
        val uuid = Math.random().toString()
        sessionStorage.set("userUuid", uuid)
        uuid
    }

    App(document.body!!, userUuid)
}

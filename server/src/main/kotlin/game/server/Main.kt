package game.server

import java.net.InetSocketAddress
import kotlin.concurrent.thread


private const val host = "localhost"
private const val port = 8887

fun main(args: Array<String>) {
    val gameServer = GameServer()
    val gameWebSocketServer = GameWebSocketServer(InetSocketAddress(host, port), gameServer)

    val t1 = thread { gameServer.run() }
    val t2 = thread { gameWebSocketServer.run() }

    t1.join()
    t2.join()
}

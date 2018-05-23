package game.server

import java.net.InetSocketAddress


private const val host = "localhost"
private const val port = 8887

fun main(args: Array<String>) {
    val server = GameServer(InetSocketAddress(host, port))
    server.run()
}

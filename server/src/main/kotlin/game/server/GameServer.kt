package game.server

import game.common.ClientMessageUnion
import game.common.HelloMessage
import kotlinx.serialization.json.JSON
import org.java_websocket.WebSocket
import org.java_websocket.handshake.ClientHandshake
import org.java_websocket.server.WebSocketServer
import java.net.InetSocketAddress
import java.nio.ByteBuffer


class GameServer(address: InetSocketAddress) : WebSocketServer(address) {
    override fun onOpen(conn: WebSocket, handshake: ClientHandshake) {
        println("new connection to " + conn.remoteSocketAddress)
    }

    override fun onClose(conn: WebSocket, code: Int, reason: String, remote: Boolean) {
        println("closed " + conn.remoteSocketAddress + " with exit code " + code + " additional info: " + reason)
    }

    override fun onMessage(conn: WebSocket, message: String) {
        println("received message from " + conn.remoteSocketAddress + ": " + message)
        JSON.parse<ClientMessageUnion>(message).run {
            helloMessage?.let { handleHello(it) }
            clientCommandMessage?.let {}
            Unit
        }
    }

    override fun onMessage(conn: WebSocket, message: ByteBuffer) {
        println("received ByteBuffer from " + conn.remoteSocketAddress)
    }

    override fun onError(conn: WebSocket?, ex: Exception) {
        System.err.println("an error occured on connection " + conn?.remoteSocketAddress + ":" + ex)
    }

    override fun onStart() {
        println("server started successfully")
    }

    private fun handleHello(message: HelloMessage) {

    }
}

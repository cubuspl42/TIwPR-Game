package game.server

import game.common.ClientMessage
import game.common.HelloMessage
import kotlinx.serialization.json.JSON
import org.java_websocket.WebSocket
import org.java_websocket.handshake.ClientHandshake
import org.java_websocket.server.WebSocketServer
import java.net.InetSocketAddress
import java.nio.ByteBuffer

data class ConnectionState(
        val clientId: Int
)

class GameWebSocketServer(
        address: InetSocketAddress,
        private val gameServer: GameServer
) : WebSocketServer(address) {
    private var nextClientId = 0

    override fun onOpen(conn: WebSocket, handshake: ClientHandshake) {
        println("new connection to " + conn.remoteSocketAddress)
        val clientId = nextClientId++
        conn.setAttachment(ConnectionState(clientId))
        gameServer.handleEvent(ClientConnectedEvent(clientId, conn))
    }

    override fun onClose(conn: WebSocket, code: Int, reason: String, remote: Boolean) {
        println("closed " + conn.remoteSocketAddress + " with exit code " + code + " additional info: " + reason)
        val connectionState = conn.getAttachment<ConnectionState>()
        gameServer.handleEvent(ClientDisconnectedEvent(connectionState.clientId))

    }

    override fun onMessage(conn: WebSocket, message: String) {
        println("received message from " + conn.remoteSocketAddress + ": " + message)
        val connectionState = conn.getAttachment<ConnectionState>()
        gameServer.handleEvent(ClientMessageEvent(connectionState.clientId, JSON.parse(message)))
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

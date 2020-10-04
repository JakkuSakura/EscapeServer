package network

import java.net.Proxy

import com.github.steveice10.mc.protocol.{MinecraftConstants, MinecraftProtocol}
import com.github.steveice10.mc.protocol.data.SubProtocol
import com.github.steveice10.mc.protocol.data.message.TextMessage
import com.github.steveice10.mc.protocol.data.status.{PlayerInfo, ServerStatusInfo, VersionInfo}
import com.github.steveice10.mc.protocol.data.status.handler.ServerInfoBuilder
import com.github.steveice10.packetlib.{Server, Session}
import com.github.steveice10.packetlib.event.server.{ServerAdapter, ServerClosedEvent, SessionAddedEvent, SessionRemovedEvent}
import com.github.steveice10.packetlib.tcp.TcpSessionFactory


object Boostrap {
    private val VERIFY_USERS: Boolean = true
    private val HOST: String = "0.0.0.0"
    private val PORT: Int = 25565
    private val AUTH_PROXY: Proxy = Proxy.NO_PROXY

    def main(args: Array[String]): Unit = {
        game.Server.start()
        MessageSender.start()

        val server = new Server(HOST, PORT, classOf[MinecraftProtocol], new TcpSessionFactory())
        server.setGlobalFlag(MinecraftConstants.AUTH_PROXY_KEY, AUTH_PROXY);
        server.setGlobalFlag(MinecraftConstants.VERIFY_USERS_KEY, VERIFY_USERS);
        server.setGlobalFlag(MinecraftConstants.SERVER_INFO_BUILDER_KEY, new ServerInfoBuilder {
            override def buildInfo(session: Session): ServerStatusInfo =
                new ServerStatusInfo(
                    new VersionInfo(MinecraftConstants.GAME_VERSION, MinecraftConstants.PROTOCOL_VERSION),
                    new PlayerInfo(999, 999, Array.empty),
                    new TextMessage("Hello world!"),
                    null
                )

        })

        server.setGlobalFlag(MinecraftConstants.SERVER_COMPRESSION_THRESHOLD, 100)

        server.addListener(new ServerAdapter() {
            override def serverClosed(event: ServerClosedEvent): Unit = {
                println("Server closed.")
            }

            override def sessionAdded(event: SessionAddedEvent): Unit = {
                event.getSession.addListener(new network.Client(event.getSession))
            }

            override def sessionRemoved(event: SessionRemovedEvent): Unit = {
                val protocol = event.getSession.getPacketProtocol.asInstanceOf[MinecraftProtocol]
                if (protocol.getSubProtocol == SubProtocol.GAME) {
                    // some process?
                }
            }
        })
        server.bind()
    }
}
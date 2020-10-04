package network

import com.github.steveice10.mc.auth.data.GameProfile
import com.github.steveice10.mc.auth.exception.request.RequestException
import com.github.steveice10.mc.protocol.MinecraftConstants
import com.github.steveice10.mc.protocol.MinecraftProtocol
import com.github.steveice10.mc.protocol.ServerLoginHandler
import com.github.steveice10.mc.protocol.data.SubProtocol
import com.github.steveice10.mc.protocol.data.game.entity.player.GameMode
import com.github.steveice10.mc.protocol.data.game.world.WorldType
import com.github.steveice10.mc.protocol.data.message.ChatColor
import com.github.steveice10.mc.protocol.data.message.ChatFormat
import com.github.steveice10.mc.protocol.data.message.Message
import com.github.steveice10.mc.protocol.data.message.MessageStyle
import com.github.steveice10.mc.protocol.data.message.TextMessage
import com.github.steveice10.mc.protocol.data.message.TranslationMessage
import com.github.steveice10.mc.protocol.data.status.PlayerInfo
import com.github.steveice10.mc.protocol.data.status.ServerStatusInfo
import com.github.steveice10.mc.protocol.data.status.VersionInfo
import com.github.steveice10.mc.protocol.data.status.handler.ServerInfoBuilder
import com.github.steveice10.mc.protocol.data.status.handler.ServerInfoHandler
import com.github.steveice10.mc.protocol.data.status.handler.ServerPingTimeHandler
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientChatPacket
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerChatPacket
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerJoinGamePacket
import com.github.steveice10.packetlib.Client
import com.github.steveice10.packetlib.ProxyInfo
import com.github.steveice10.packetlib.Server
import com.github.steveice10.packetlib.Session
import com.github.steveice10.packetlib.event.server.ServerAdapter
import com.github.steveice10.packetlib.event.server.ServerClosedEvent
import com.github.steveice10.packetlib.event.server.SessionAddedEvent
import com.github.steveice10.packetlib.event.server.SessionRemovedEvent
import com.github.steveice10.packetlib.event.session.DisconnectedEvent
import com.github.steveice10.packetlib.event.session.PacketReceivedEvent
import com.github.steveice10.packetlib.event.session.SessionAdapter
import com.github.steveice10.packetlib.tcp.{TcpServerSession, TcpSessionFactory}
import java.net.Proxy
import java.util

import com.github.steveice10.mc.protocol.packet.login.client.LoginStartPacket
import game.Server


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
                event.getSession.addListener(new network.Client)
            }

            override def sessionRemoved(event: SessionRemovedEvent): Unit = {
                val protocol = event.getSession.getPacketProtocol.asInstanceOf[MinecraftProtocol]
                if (protocol.getSubProtocol == SubProtocol.GAME) {
                    println("Closing server.")
                    event.getServer.close(false)
                }
            }
        })
        server.bind()
    }
}
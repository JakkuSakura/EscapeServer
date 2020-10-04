package network

import com.github.steveice10.mc.protocol.data.game.entity.player.GameMode
import com.github.steveice10.mc.protocol.data.game.world.WorldType
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerJoinGamePacket
import com.github.steveice10.mc.protocol.packet.login.client.LoginStartPacket
import com.github.steveice10.mc.protocol.packet.login.server.LoginSuccessPacket
import com.github.steveice10.packetlib.Session
import com.github.steveice10.packetlib.event.session.{DisconnectedEvent, PacketReceivedEvent, PacketSentEvent, SessionAdapter}
import com.github.steveice10.packetlib.packet.Packet
import game.Server
import messages.MNetworkIn
import player.Player

class Client(val session: Session) extends SessionAdapter {
    var username: String = _
    var player: Player = _

    override def packetReceived(event: PacketReceivedEvent): Unit = {
        val packet: Packet = event.getPacket
        println("Packet received " + packet)

        packet match {
            case loginStartPacket: LoginStartPacket =>
                println(loginStartPacket.getUsername + " logging in")

                username = loginStartPacket.getUsername
                player = new Player
                player.player_name = username
            case _ =>
        }
        Server.message_queue.broadcast(packet.getClass, MNetworkIn(player, packet))

    }

    override def packetSent(event: PacketSentEvent): Unit = {
        if (event.getPacket.isInstanceOf[LoginSuccessPacket]) {
            println("Login success")
            ClientManger.addClient(this)
            Server.addPlayer(player)
        }
    }

    override def disconnected(event: DisconnectedEvent): Unit = {
        println(session + " disconnect " + event.getReason)
        if (username != null) {
            Server.removePlayer(username)
            ClientManger.removeClient(this)
        }
    }
}

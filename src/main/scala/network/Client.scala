package network

import com.github.steveice10.mc.protocol.packet.login.server.LoginSuccessPacket
import com.github.steveice10.packetlib.Session
import com.github.steveice10.packetlib.event.session.{DisconnectedEvent, PacketReceivedEvent, PacketSentEvent, SessionAdapter}
import com.github.steveice10.packetlib.packet.Packet
import game.Server
import messages.MNetworkIn
import player.Player

class Client(val session: Session) extends SessionAdapter {
    var player: Player = _

    override def packetReceived(event: PacketReceivedEvent): Unit = {
        val packet: Packet = event.getPacket
        println("Packet received " + packet)

        if (player != null)
            Server.message_queue.broadcast(packet.getClass, MNetworkIn(player, packet))
    }

    override def packetSent(event: PacketSentEvent): Unit = {
        if (event.getPacket.isInstanceOf[LoginSuccessPacket]) {
            player = new Player
            player.entity_id = event.getPacket[LoginSuccessPacket].getProfile.getId
            player.player_name = event.getPacket[LoginSuccessPacket].getProfile.getName
            player.profile = event.getPacket[LoginSuccessPacket].getProfile
            println("Login success " + player.player_name)
            ClientManger.addClient(this)
            Server.addPlayer(player)
        }
    }

    override def disconnected(event: DisconnectedEvent): Unit = {
        println(session + " disconnect " + event.getReason)
        if (player != null) {
            Server.removePlayer(player.player_name)
            ClientManger.removeClient(this)
        }
    }
}

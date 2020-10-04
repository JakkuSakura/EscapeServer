package network

import com.github.steveice10.mc.protocol.data.game.entity.player.GameMode
import com.github.steveice10.mc.protocol.data.game.world.WorldType
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerJoinGamePacket
import com.github.steveice10.mc.protocol.packet.login.client.LoginStartPacket
import com.github.steveice10.mc.protocol.packet.login.server.LoginSuccessPacket
import com.github.steveice10.packetlib.event.session.{DisconnectedEvent, PacketReceivedEvent, PacketSentEvent, SessionAdapter}
import game.Server
import player.Player

class Client extends SessionAdapter {
    var username: String = _
    var player: Player = _

    override def packetReceived(event: PacketReceivedEvent): Unit = {
        println("Packet received " + event.getPacket)

        if (event.getPacket.isInstanceOf[LoginStartPacket]) {
            val loginStartPacket: LoginStartPacket = event.getPacket
            println(loginStartPacket.getUsername + " logging in")

            username = loginStartPacket.getUsername
            player = new Player
            player.player_name = username
        }

    }

    override def packetSent(event: PacketSentEvent): Unit = {
        if (event.getPacket.isInstanceOf[LoginSuccessPacket]) {
            println("Login success")
            event.getSession.send(new ServerJoinGamePacket(0, false, GameMode.SURVIVAL, 0, 0, 100, WorldType.DEFAULT, 16, false, true));
            Server.addPlayer(player)
            ClientManger.addClient(this)
        }
    }

    override def disconnected(event: DisconnectedEvent): Unit = {
        if (username != null) {
            Server.removePlayer(username)
            ClientManger.removeClient(this)
        }
    }
}

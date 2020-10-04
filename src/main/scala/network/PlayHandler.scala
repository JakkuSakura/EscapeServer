package network

import game.{Server, Player}
import messages.MNetworkIn

class PlayHandler(client: Client) extends PacketHandler {
    ClientManger.addClient(client)
    Server.addPlayer(client.player)
    override def apply(packet: McPacket): Unit = {
        Server.message_queue.broadcast(MNetworkIn(client.player, packet))
    }
}

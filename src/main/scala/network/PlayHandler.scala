package network

import game.{Server, Player}
import messages.MNetworkIn

class PlayHandler(client: Client) extends PacketHandler {
    Server.addPlayer(client.player)
    ClientManger.addClient(client)
    override def apply(packet: McPacket): Unit = {
        Server.message_queue.broadcast(MNetworkIn(client.player, packet))
    }
}

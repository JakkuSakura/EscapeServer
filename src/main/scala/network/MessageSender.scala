package network

import game.Server
import messages.MNetworkOut

object MessageSender extends Thread {
    override def run(): Unit = {
        val out = Server.message_queue.getQueue[MNetworkOut](classOf[MNetworkOut])

        while(true) {
            val MNetworkOut(player, msg) = out.take()
            val client = ClientManger.getClient(player)
            client.session.send(msg)

        }
    }
}

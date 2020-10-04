package network

import game.Server
import messages.MNetworkMessageOut

object MessageSender extends Thread {
    override def run(): Unit = {
        val out = Server.message_queue.getQueue(classOf[MNetworkMessageOut])

        while(true) {
            val MNetworkMessageOut(player, msg) = out.take()
            println("get one message")
            //ClientManger.getClient(player).channel().writeAndFlush(msg)

        }
    }
}

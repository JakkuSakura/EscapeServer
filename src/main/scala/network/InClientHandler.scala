package network

import game.Server
import io.netty.channel.{ChannelHandlerContext, SimpleChannelInboundHandler}

trait PacketHandler {
    def apply(packet: McPacket): Unit
}

class InClientHandler extends SimpleChannelInboundHandler[McPacket] {
    var client: Client = _
    var current_handler: PacketHandler = _

    override def channelActive(ctx: ChannelHandlerContext): Unit = {
        println("Connection established " + ctx)
        client = new Client(ctx, null)
        current_handler = new HandshakeHandler(this, client)
    }

    override def channelRead0(ctx: ChannelHandlerContext, packet: McPacket): Unit = {
        println("Received a packet:" + packet)
        current_handler(packet)

    }

    override def channelInactive(ctx: ChannelHandlerContext): Unit = {
        println("Inactive " + ctx)
        if (client.player != null)
            Server.removePlayer(client.player.player_name)
        client = null
    }
}

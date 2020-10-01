package network

import game.EscapeServer
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
        println("ChannelRead invoked:" + packet)
        current_handler(packet)

    }

    override def channelInactive(ctx: ChannelHandlerContext): Unit = {
        println("Inactive " + ctx)
        if (client.player != null)
            EscapeServer.removeClient(client.player.player_name)
        client = null
    }
}

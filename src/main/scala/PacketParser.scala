import java.util.UUID

import io.netty.buffer.{ByteBuf, ByteBufUtil}
import io.netty.channel.{ChannelHandlerContext, ChannelInboundHandlerAdapter}
import io.netty.util.CharsetUtil

class PacketParser extends ChannelInboundHandlerAdapter {

    override def channelActive(ctx: ChannelHandlerContext): Unit = {
        println("Connection established " + ctx)
        EscapeServer.add_client(new Client(ctx))
    }

    override def channelRead(ctx: ChannelHandlerContext, msg: scala.Any): Unit = {
        val packet = msg.asInstanceOf[RawPacket]
        println("ChannelRead invoked:" + packet)
        val client = EscapeServer.clients(ctx)
        //      TODO  client.player.connection match
        // connection state is LOGIN
        packet.packet_id match {
            case 0x00 =>
                if (client.player.handshake_state != 2) {
                    // 1. C→S: Handshake with Next State set to 2 (login)
                    val hs = new HandShaking(packet.data)
                    client.player.handshake_state = hs.next_state
                    println("Handshake " + hs.next_state)
                } else {
                    // 2. C→S: Login Start
                    val login_start = new LoginStart(packet.data)
                    client.player.player_name = login_start.player_name
                    // 3. S→C: Encryption Request
                    ctx.channel().writeAndFlush(new EncryptionRequest().toByteBuf)
                }
            case 0x01 =>
                // 4. Client auth
                // 5. C→S: Encryption Response
                val _en_res = new EncryptionResponse(packet.data)
                // 6. Server auth, both enable encryption
                // assume it's always true
                // 7. S→C: Set Compression (optional)
                // no compression if no sending
                // ctx.channel().writeAndFlush(new SetCompression(-1).toByteBuf)
                // 8. S→C: Login Success
                ctx.channel().writeAndFlush(new LoginSuccess(UUID.randomUUID(), client.player.player_name).toByteBuf)
            // TODO set connection state to PLAY
            case _ =>
                println("Unknown")
        }
    }

    override def channelInactive(ctx: ChannelHandlerContext): Unit = {
        println("Inactive " + ctx)
    }
}

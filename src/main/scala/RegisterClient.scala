import io.netty.buffer.ByteBuf
import io.netty.channel.{ChannelHandlerContext, ChannelInboundHandlerAdapter}
import io.netty.util.CharsetUtil

class RegisterClient extends ChannelInboundHandlerAdapter {

    override def channelActive(ctx: ChannelHandlerContext): Unit = {
        println("Connection established" + ctx)
        EscapeServer.add_client(new Client(ctx));
    }

    override def channelRead(ctx: ChannelHandlerContext, msg: scala.Any): Unit = {
        val in = msg.asInstanceOf[ByteBuf]
        println("channelRead invoked: " + in.toString(CharsetUtil.UTF_8))
//        Unpooled.copiedBuffer("Ack", CharsetUtil.UTF_8)
        ctx.writeAndFlush(in)
    }
}

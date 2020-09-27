import io.netty.channel.ChannelHandlerContext

class Client(val ctx: ChannelHandlerContext) {
    val player: Player = new Player()
}

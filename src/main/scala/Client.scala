import io.netty.channel.ChannelHandlerContext

class Client(val ctx: ChannelHandlerContext) {
    {
        println("New Client")
    }
    val player: Player = new Player()
}

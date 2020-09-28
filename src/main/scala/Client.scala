import io.netty.channel.ChannelHandlerContext

class Client(var ctx: ChannelHandlerContext, var player: Player) {
    var connection_state: Int = 0
}
object Client {
    val HANDSHAKE = 0
    val STATUS = 1
    val LOGIN = 2
    val PLAY = 3
}
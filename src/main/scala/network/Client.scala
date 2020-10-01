package network

import io.netty.channel.ChannelHandlerContext
import game.Player

class Client(var ctx: ChannelHandlerContext, var player: Player) {
    var protocol_version: Int = 0
    var connection_state: ConnectionState.Value = ConnectionState.HANDSHAKE
}

object ConnectionState extends Enumeration {
    val HANDSHAKE, STATUS, LOGIN, PLAY = Value
}
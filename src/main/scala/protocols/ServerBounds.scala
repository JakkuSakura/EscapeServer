package protocols

import io.netty.buffer.ByteBuf

// Bound to server 0x00
class HandShaking(byteBuf: ByteBuf) extends McPacket(0x00, byteBuf) {
    val protocol_version: Int = readVarInt()
    val server_address: String = readString()
    val server_port: Short = readShort()
    val next_state: Int = readVarInt()
}

// Bound to server 0x00
class LoginStart(byteBuf: ByteBuf) extends McPacket(0x00, byteBuf) {
    val player_name: String = readString()
}

// Bound to server 0x01
class EncryptionResponse(byteBuf: ByteBuf) extends McPacket(0x01, byteBuf) {
    val shared_secret: String = readString()
    val verify_token: String = readString()
}

package network

import java.util.UUID

import game.{Server, NBT, Player}
import io.netty.buffer.{ByteBuf, Unpooled}
import utils.Configuration.{DIFFICULTY, GAMEMODE, PLAYER_LOGIN_EID_BASE, WORLDTYPE}
import utils.{Crypto, Utils}

class McPacket(private var packet_id: Byte, buf_ : ByteBuf) extends PacketByteBuf(buf_) {

    def this() = this(0, Unpooled.buffer())

    def this(buf: ByteBuf) = this(0, buf)

    def this(packet_id: Byte) = this(packet_id, Unpooled.buffer())

    def this(packet_id: Int) = this(packet_id.toByte, Unpooled.buffer())

    // It seems like there is no packet id greater 0x7F yet
    final def getPacketId: Byte = packet_id

    final def getData: ByteBuf = buf

    final def setPacketId(id: Byte): Unit = packet_id = id

    final def setData(data: ByteBuf): Unit = buf = data


    override def toString: String = {
        String.format("Packet id: %02x\nData: %d\n", packet_id, readableBytes) + Utils.getHexAndChar(buf)
    }
}

//class PlayerList() extends network.McPacket {
//    override def getData: ByteBuf = {
//        TODO implement this
//        unsigned length = sizeof( pingjson1 ) + sizeof( pingjson2 ) + sizeof( pingjson3 ) + sizeof( pingjson4 ) + 24 + strlen( MOTD_NAME );
//        char buffo[length];
//        char maxplayers[12];
//        char curplayers[12];
//        buffo[0] = 0;
//        Uint32To10Str( maxplayers, MAX_PLAYERS );
//        Uint32To10Str( curplayers, dumbcraft_playercount );
//        mstrcatp( buffo, pingjson1, length );
//        mstrcat( buffo, MOTD_NAME, length );
//        mstrcatp( buffo, pingjson2, length );
//        mstrcat( buffo, maxplayers, length );
//        mstrcatp( buffo, pingjson3, length );
//        mstrcat( buffo, curplayers, length );
//        mstrcatp( buffo, pingjson4, length );
//        StartSend();
//        Sbyte( 0x00 );
//        Sstring( buffo, -1 );
//        DoneSend();
//        Unpooled.EMPTY_BUFFER
//    }
//}

class LookUpdate(p: Player) extends McPacket(0x2E) {
    writeDouble(p.x)
    writeDouble(p.y)
    writeDouble(p.z)
    writeFloat(p.yaw)
    writeFloat(p.pitch)
    writeByte(0x00) //xyz absolute
    writeVarInt(0)
}

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

// Bound to client 0x01
class EncryptionRequest() extends McPacket(0x01) {
    writeVarString("")
    // Public Key
    writeVarInt(Crypto.KEYPAIR.getPublic.getEncoded.length)
    writeBytes(Crypto.KEYPAIR.getPublic.getEncoded)
    // Verify Token
    writeByte(4)
    writeBytes(Crypto.VERIFICATION_TOKEN)

}

// Bound to client 0x03
/** enable compression only if sending a non-negative value */
class SetCompression(threshold: Int) extends McPacket(0x03) {
    writeVarInt(threshold)
}

// Bound to client 0x02
class LoginSuccess(UUID: UUID, username: String, protocol_version: Int) extends McPacket(0x02) {
    if (protocol_version > 578) {
        writeUUID(UUID)
    } else {
        writeVarString(UUID.toString)
    }
    writeVarString(username)
}
// Bound to client 0x01
class Pong(num: Long) extends McPacket(0x01) {
    writeLong(num)
}
// Bound to client 0x26
class JoinGame(p: Player) extends McPacket(0x26) {
    writeInt(p.entity_id)
    writeByte(p.game_mode.id.toByte)
    writeInt(p.dimension.id)
    writeLong(Server.hashed_seed)
    writeByte(0) // max players
    writeVarString("default") // level type like flat
    writeVarInt(p.view_distance)
    writeBoolean(false) // reduced debug
    writeBoolean(false) // enable respawn screen
}
// Bound to client 0x40
class HeldItemChange(slot: Byte) extends McPacket(0x40) {
    writeByte(slot)
}
import java.util.UUID

import Configuration.{DIFFICULTY, GAMEMODE, MAX_PLAYERS, PLAYER_LOGIN_EID_BASE, WORLDTYPE}
import io.netty.buffer.{ByteBuf, EmptyByteBuf, Unpooled}

class McPacket() {
    def toByteBuf: ByteBuf = {
        Unpooled.EMPTY_BUFFER
    }
}

class RawPacket(val packet_id: Int, val data: ByteBuf) extends McPacket {
    override def toByteBuf: ByteBuf = {
        val packet_id_buf = new PacketByteBuf(Unpooled.buffer())
        packet_id_buf.writeVarInt(packet_id)
        packet_id_buf.readableBytes()

        val buf = new PacketByteBuf(Unpooled.buffer(4 + packet_id_buf.readableBytes() + data.readableBytes()))
        buf.writeVarInt(packet_id_buf.readableBytes() + data.readableBytes())
        buf.writeBytes(packet_id_buf)
        buf.writeBytes(data)
        buf.buf
    }

    override def toString: String = {
        String.format("Packet id: %02x\nData: %d\n", packet_id, data.readableBytes()) + Utils.getHexAndChar(data)
    }
}

class Pong(name: String) extends McPacket {
    override def toByteBuf: ByteBuf = {
        val buf = new PacketByteBuf(Unpooled.buffer(1 + name.length))
        buf.writeByte(0x01)
        buf.writeBuffer(name)
        buf.buf
    }
}

class PlayerList() extends McPacket {
    override def toByteBuf: ByteBuf = {
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
        Unpooled.EMPTY_BUFFER
    }
}

class LookUpdate(p: Player) extends McPacket {
    override def toByteBuf: ByteBuf = {
        val buf = new PacketByteBuf(Unpooled.buffer(1 + 8 + 8 + 8 + 4 + 4 + 1 + 1))
        buf.writeByte(0x2E) //updated
        buf.writeDouble(p.x)
        buf.writeDouble(p.y)
        buf.writeDouble(p.z)
        buf.writeFloat(p.yaw)
        buf.writeFloat(p.pitch)
        buf.writeByte(0x00) //xyz absolute
        buf.writeVarInt(0)
        buf.buf
    }
}

class WelcomeWorld(p: Player) extends McPacket {
    override def toByteBuf: ByteBuf = {
        val buf = new PacketByteBuf(Unpooled.buffer(1 + 8 + 8 + 8 + 4 + 4 + 1 + 1))
        buf.writeByte(0x23) //Updated (Join Game)
        buf.writeInt(p.player_id + PLAYER_LOGIN_EID_BASE);
        buf.writeByte(GAMEMODE)
        buf.writeInt(WORLDTYPE)
        buf.writeByte(DIFFICULTY)
        buf.writeByte(MAX_PLAYERS)
        buf.writeString("default")
        buf.writeByte(0); //Reduce debug info?
        buf.buf
    }
}

// Bound to server 0x00
class HandShaking(byteBuf: ByteBuf) extends McPacket {
    private val buf = new PacketByteBuf(byteBuf)
    val protocol_version: Int = buf.readVarInt()
    val server_address: String = buf.readString()
    val server_port: Short = buf.readUnsignedShort().toShort
    val next_state: Int = buf.readVarInt()
}

// Bound to server 0x00
class LoginStart(byteBuf: ByteBuf) extends McPacket {
    private val buf = new PacketByteBuf(byteBuf)
    val player_name: String = buf.readString()
}

// Bound to server 0x01
class EncryptionResponse(byteBuf: ByteBuf) extends McPacket {
    private val buf = new PacketByteBuf(byteBuf)
    val shared_secret: String = buf.readString()
    val verify_token: String = buf.readString()
}

// Bound to client 0x01
class EncryptionRequest() extends McPacket {
    override def toByteBuf: ByteBuf = {
        val buf = new PacketByteBuf(Unpooled.buffer())
        buf.writeByte(0x01)
        buf.writeString("")
        // Public Key
        buf.writeString("123456")
        // Verify Token
        buf.writeString("1234")
        buf.buf
    }
}

// Bound to client 0x03
/** enable compression only if sending a non-negative value */
class SetCompression(packets_before_compressed: Int) extends McPacket {
    override def toByteBuf: ByteBuf = {
        val buf = new PacketByteBuf(Unpooled.buffer())
        buf.writeByte(0x03)
        buf.writeVarInt(packets_before_compressed)
        buf.buf
    }
}
// Bound to client 0x02
class LoginSuccess(UUID: UUID, username: String) extends McPacket {
    override def toByteBuf: ByteBuf = {
        val buf = new PacketByteBuf(Unpooled.buffer())
        buf.writeString(UUID.toString)
        buf.writeString(username)
        buf.buf
    }
}
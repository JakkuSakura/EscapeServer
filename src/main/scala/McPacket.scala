import io.netty.buffer.{ByteBuf, EmptyByteBuf, Unpooled}

abstract class McPacket() {
    def toByteBuf: ByteBuf
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
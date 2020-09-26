import io.netty.buffer.ByteBuf

class PacketByteBuf(val buf: ByteBuf) extends DelegatedByteBuf(buf) {
    def writeBuffer(v: String): Unit = {
        buf.writeBytes(v.getBytes())
    }

    def writeUnsignedByte(v: Byte): Unit = {
        buf.writeByte(v)
    }

    def writeVarInt(v: Int): Unit = {
        var value = v
        do {
            var temp = (value & 0x7F).toByte
            // Note: >>> means that the sign bit is shifted with the rest of the number rather than being left alone
            value >>>= 7
            if (value != 0) temp = (temp | 0x80).toByte
            writeByte(temp)
        } while ( {
            value != 0
        })
    }

    def writeVarLong(v: Long): Unit = {
        var value = v
        do {
            var temp = (value & 0x7F).toByte
            value >>>= 7
            if (value != 0) temp = (temp | 0x80).toByte
            writeByte(temp)
        } while ( {
            value != 0
        })
    }
}

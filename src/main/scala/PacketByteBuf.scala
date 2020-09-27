import io.netty.buffer.ByteBuf
import io.netty.util.CharsetUtil

class PacketByteBuf(val buf: ByteBuf) extends DelegatedByteBuf(buf) {

    def writeString(str: String): Unit = {
        writeVarInt(str.length)
        writeBuffer(str)
    }

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

    def readVarInt(): Int = {
        var numRead = 0
        var result = 0
        var read: Byte = 0
        do {
            read = buf.readByte()
            val value = read & 0x7F
            result |= (value << (7 * numRead));
            numRead += 1
            if (numRead > 5) {
                throw new RuntimeException("VarInt is too big");
            }
        } while ((read & 0x80) != 0);
        result
    }

    def readVarLong(): Long = {
        var numRead = 0
        var result = 0
        var read = 0
        do {
            read = buf.readByte()
            val value = read & 0x7F
            result |= (value << (7 * numRead));
            numRead += 1;
            if (numRead > 10) {
                throw new RuntimeException("VarInt is too big");
            }
        } while ((read & 0x80) != 0)
        result
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

    def readString(): String = {
        val length = readVarInt()
        readBytes(length).toString(CharsetUtil.UTF_8)
    }
}

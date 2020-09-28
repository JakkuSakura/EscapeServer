import java.util.UUID

import io.netty.buffer.ByteBuf
import io.netty.util.CharsetUtil

class PacketByteBuf(var buf: ByteBuf) {
    def readableBytes: Int = buf.readableBytes()

    def writableBytes: Int = buf.writableBytes()

    def writeDouble(v: Double): Unit = buf.writeDouble(v)

    def writeFloat(v: Float): Unit = buf.writeFloat(v)

    def writeLong(v: Long): Unit = buf.writeLong(v)

    def writeShort(v: Short): Unit = buf.writeShort(v)

    def writeInt(v: Int): Unit = buf.writeInt(v)

    def writeByte(v: Byte): Unit = buf.writeByte(v)

    def readDouble(): Double = buf.readDouble()

    def readFloat(): Float = buf.readFloat()

    def readLong(): Long = buf.readLong()

    def readShort(): Short = buf.readShort()

    def readInt(): Int = buf.readInt()

    def readByte(): Byte = buf.readByte()

    def writeString(str: String): Unit = {
        writeVarInt(str.length)
        writeBytes(str)
    }

    def writeBytes(v: String): Unit = buf.writeBytes(v.getBytes())

    def writeBytes(v: Array[Byte]): Unit = buf.writeBytes(v)

    def writeBytes(v: ByteBuf): Unit = buf.writeBytes(v)

    def writeUnsignedByte(v: Byte): Unit = buf.writeByte(v)

    def readBytes(i: Int): ByteBuf = buf.readBytes(i)

    def writeBoolean(b: Boolean): Unit = writeByte(if (b) 1 else 0)

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

    def writeUUID(UUID: UUID): Unit = {
        writeLong(UUID.getMostSignificantBits)
        writeLong(UUID.getLeastSignificantBits)
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
            read = readByte()
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

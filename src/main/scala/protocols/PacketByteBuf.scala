package protocols

import java.io.{DataOutput, IOException}
import java.util.UUID

import com.github.steveice10.packetlib.io.NetOutput
import io.netty.buffer.ByteBuf
import io.netty.util.CharsetUtil

class PacketByteBuf(var buf: ByteBuf) extends NetOutput with DataOutput {
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

    def writeVarString(str: String): Unit = {
        writeVarInt(str.length)
        writeBytes(str)
    }

    def writeVarBytes(bytes: Seq[Byte]): Unit = {
        writeVarInt(bytes.length)
        writeBytes(bytes.toArray)
    }

    def writeVarBytes(buf: ByteBuf): Unit = {
        writeVarInt(buf.readableBytes())
        writeBytes(buf)
    }

    def writeBytes(v: String): Unit = buf.writeBytes(v.getBytes())

    def writeBytes(v: Array[Byte]): Unit = buf.writeBytes(v)

    def writeBytes(v: ByteBuf): Unit = buf.writeBytes(v)

    def readBytes(i: Int): ByteBuf = buf.readBytes(i)

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
            result |= (value << (7 * numRead))
            numRead += 1
            if (numRead > 5) {
                throw new RuntimeException("VarInt is too big")
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
                throw new RuntimeException("VarInt is too big")
            }
        } while ((read & 0x80) != 0)
        result
    }

    def readString(): String = {
        val length = readVarInt()
        readBytes(length).toString(CharsetUtil.UTF_8)
    }

    @throws[IOException]
    override def writeBoolean(b: Boolean): Unit = {
        this.buf.writeBoolean(b)
    }

    @throws[IOException]
    override def writeByte(b: Int): Unit = {
        this.buf.writeByte(b)
    }

    @throws[IOException]
    override def writeShort(s: Int): Unit = {
        this.buf.writeShort(s)
    }

    @throws[IOException]
    override def writeChar(c: Int): Unit = {
        this.buf.writeChar(c)
    }

    @throws[IOException]
    override def writeVarInt(i_ : Int): Unit = {
        var i = i_
        while ( {
            (i & ~0x7F) != 0
        }) {
            this.writeByte((i & 0x7F) | 0x80)
            i >>>= 7
        }
        this.writeByte(i)
    }

    @throws[IOException]
    override def writeVarLong(l_ : Long): Unit = {
        var l = l_
        while ( {
            (l & ~0x7F) != 0
        }) {
            this.writeByte((l & 0x7F).toInt | 0x80)
            l >>>= 7
        }
        this.writeByte(l.toInt)
    }

    override def writeBytes(b: Array[Byte], length: Int): Unit = {
        this.buf.writeBytes(b, 0, length)
    }

    override def writeShorts(s: Array[Short]): Unit = {
        this.writeShorts(s, s.length)
    }

    override def writeShorts(s: Array[Short], length: Int): Unit = {
        for (index <- 0 until length) {
            this.writeShort(s(index))
        }
    }

    override def writeInts(i: Array[Int]): Unit = {
        this.writeInts(i, i.length)
    }

    override def writeInts(i: Array[Int], length: Int): Unit = {
        for (index <- 0 until length) {
            this.writeInt(i(index))
        }
    }

    override def writeLongs(l: Array[Long]): Unit = {
        this.writeLongs(l, l.length)
    }

    override def writeLongs(l: Array[Long], length: Int): Unit = {
        for (index <- 0 until length) {
            this.writeLong(l(index))
        }
    }

    override def writeString(s: String): Unit = {
        if (s == null) throw new IllegalArgumentException("String cannot be null!")
        val bytes = s.getBytes("UTF-8")
        if (bytes.length > 32767) throw new IOException("String too big (was " + s.length + " bytes encoded, max " + 32767 + ")")
        else {
            this.writeVarInt(bytes.length)
            this.writeBytes(bytes)
        }
    }

    override def flush(): Unit = {
    }

    override def write(b: Int): Unit = writeInt(b)

    override def write(b: Array[Byte]): Unit = writeBytes(b)

    override def write(b: Array[Byte], off: Int, len: Int): Unit = {
        var i = 0
        while (i < len) {
            writeByte(b(i + off))
            i += 1
        }
    }

    override def writeChars(s: String): Unit = {
        writeString(s)
    }

    override def writeUTF(s: String): Unit = {
        writeString(s)
    }
}

package utils

import io.netty.buffer.ByteBuf

object Utils {
    def isPrintableChar(c: Char): Boolean = {
        val block = Character.UnicodeBlock.of(c)
        (!Character.isISOControl(c)) && c != '\uffff' && block != null && (block ne Character.UnicodeBlock.SPECIALS)
    }

    def getHexAndChar(buf: ByteBuf): String = {
        val sb = new StringBuilder(buf.readableBytes() * 3 * 2 + 2)
        try {
            buf.forEachByte(x => {
                sb.append(String.format("%02x ", x))
                true
            })
            sb.append("\n")

            buf.forEachByte(x => {
                if (isPrintableChar(x.toChar)) {
                    if (x < 0)
                        sb.append(String.format("%c  ", x + 128))
                    else
                        sb.append(String.format("%c  ", x))
                } else {
                    sb.append("** ")
                }
                true
            })
            sb.toString()
        } catch {
            case ex: Exception =>
                ex.printStackTrace()
                throw ex
        }

    }
}

package protocols

import java.util.UUID

import game.Server
import player.Player
import utils.Crypto

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


class LookUpdate(p: Player) extends McPacket(0x2E) {
    writeDouble(p.x)
    writeDouble(p.y)
    writeDouble(p.z)
    writeFloat(p.yaw)
    writeFloat(p.pitch)
    writeByte(0x00) //xyz absolute
    writeVarInt(0)
}

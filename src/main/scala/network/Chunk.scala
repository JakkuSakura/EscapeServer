package network

import game.NBT
import io.netty.buffer.ByteBuf

// Bound to client 0x22
class ChunkData(id_x: Int, id_z: Int, full_chunk: Boolean, primary_bit_mask: Int, height_map: NBT, biomes: Option[Seq[Int]], data: ByteBuf, block_entities: Seq[NBT]) extends McPacket(0x22) {
    writeInt(id_x)
    writeInt(id_z)
    writeBoolean(full_chunk)
    writeVarInt(primary_bit_mask)
    writeBytes(height_map.toBytes)
    if (full_chunk) {
        //1024 biome IDs, ordered by x then z then y, in 4×4×4 blocks. Not present if full chunk is false.
        writeVarInt(biomes.get.length)
        biomes.get.map(writeInt)
    }
    writeVarBytes(data)
    writeVarInt(block_entities.length)
    block_entities.map(x => writeBytes(x.toBytes))

}
class ChunkSection(block_count: Short, bits_per_block: Byte, palette: Any, data_array: Seq[Long])


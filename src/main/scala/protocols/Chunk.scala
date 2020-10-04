package protocols

import chunk.{ChunkIn, ChunkPos}
import com.github.steveice10.opennbt.tag.builtin.{CompoundTag, LongArrayTag}
import io.netty.buffer.ByteBuf
import utils.BitArray
/*
buf.writeInt(this.chunkX)
  buf.writeInt(this.chunkZ)
  buf.writeBoolean(this.fullChunk)
  buf.writeVarInt(this.availableSections)
  buf.writeCompoundTag(this.heightmapTags)
  if (this.biomes != null)  {
   this.biomes.writeToBuf(buf)
  }

  buf.writeVarInt(this.buffer.length)
  buf.writeBytes(this.buffer)
  buf.writeVarInt(this.tileEntityTags.size)

  import scala.collection.JavaConversions._
   for (compoundnbt <- this.tileEntityTags)  {
    buf.writeCompoundTag(compoundnbt)
  }
 */
// Bound to client 0x22
class ChunkData extends McPacket(0x22) {
    def init(chunk_x: Int, chunk_z: Int, full_chunk: Boolean, available_Sections: Int, height_map: CompoundTag, biomes: Option[Seq[Int]], data: ByteBuf, block_entities: Seq[CompoundTag]) = {
        writeInt(chunk_x)
        writeInt(chunk_z)
        writeBoolean(full_chunk)
        writeVarInt(available_Sections)
        height_map.write(this)
        if (full_chunk) {
            //1024 biome IDs, ordered by x then z then y, in 4×4×4 blocks. Not present if full chunk is false.
            writeVarInt(biomes.get.length)
            biomes.get.foreach(writeInt)
        }
        writeVarBytes(data)
        writeVarInt(block_entities.length)
        block_entities.map(x => x.write(this))
    }

    def this(chunk_in: ChunkIn, changedSectionFilter: Int) = {
        this()
        val chunk_pos: ChunkPos = chunk_in.getPos
        val fullChunk = changedSectionFilter == 65535
        val heightmapTags = new CompoundTag("HeightMap")
        val longarray = new BitArray(9, 256)
        for(i <- 0 until 36)
            longarray.setAt(i, 255)

        heightmapTags.put(new LongArrayTag("MOTION_BLOCKING", longarray.longArray))
        val biomes: Option[Seq[Int]] = if (fullChunk)  {
            Some(new Array[Int](1024).toSeq)
        } else {
            None
        }
        val chunk_sections = chunk_in.getChunkSections
        //init(chunk_pos.x, chunk_pos.z, fullChunk, heightmapTags, biomes, data, Seq.empty[CompoundTag])
    }

}
class ChunkSection(block_count: Short, bits_per_block: Byte, palette: Any, data_array: Seq[Long])

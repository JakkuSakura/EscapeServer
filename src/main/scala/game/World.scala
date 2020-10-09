package game

import block.BlockCollection.Air
import block.{BlockPos, BlockPos2D, BlockState}
import chunk.ChunkPos
import com.github.steveice10.mc.protocol.data.game.chunk.{Chunk, Column, FlexibleStorage}
import com.github.steveice10.mc.protocol.packet.ingame.server.world.ServerChunkDataPacket
import com.github.steveice10.opennbt.tag.builtin.{CompoundTag, LongArrayTag}

abstract class World {
    def getBlock(x: Int, y: Int, z: Int): BlockState

    def setBlock(x: Int, y: Int, z: Int, block_state: BlockState): Unit

    def toChunkPos(x: Int, y: Int): ChunkPos = ChunkPos(x / 16, y / 16)

    def getChunk(pos: ChunkPos, first_load: Boolean): ServerChunkDataPacket = {
        val ChunkPos(x, z) = pos
        val chunks = new Array[Chunk](16)
        for (i <- 0 until 16) {
            chunks(i) = new Chunk()
        }
        val ba = new FlexibleStorage(9, 256)
        for (i <- 0 until 256)
            ba.set(i, 0)

        val hm = new CompoundTag("")
        hm.put(new LongArrayTag("MOTION_BLOCKING", ba.getData))
        val col = new Column(x, z, chunks, Array.empty[CompoundTag], hm, if (first_load) new Array[Int](1024) else null)
        val cd = new ServerChunkDataPacket(col)
        cd
    }

    def getWorldBoundary: (BlockPos, BlockPos)

    def getWorldBoundary2D: (BlockPos2D, BlockPos2D) = {
        val (BlockPos(x1, _, z1), BlockPos(x2, _, z2)) = getWorldBoundary
        (BlockPos2D(x1, z1), BlockPos2D(x2, z2))
    }

    def getWorldBoundaryChunk: (ChunkPos, ChunkPos) = {
        val (a, b) = getWorldBoundary2D
        (toChunkPos(a.x, a.z), toChunkPos(b.z, b.z))
    }

    def isInBoundary(blockPos: BlockPos): Boolean = {
        val (a, b) = getWorldBoundary
        a.x <= blockPos.x && blockPos.x <= b.x &&
          a.y <= blockPos.y && blockPos.y <= b.y &&
          a.z <= blockPos.z && blockPos.z <= b.z
    }

    def isInBoundary(chunkPos: ChunkPos): Boolean = {
        val (a, b) = getWorldBoundaryChunk
        a.x <= chunkPos.x && chunkPos.x <= b.x &&
          a.z <= chunkPos.z && chunkPos.z <= b.z
    }
}

class CubedWorld extends World {
    private val map = Array.ofDim[BlockState](256, 256, 256)

    override def getBlock(x: Int, y: Int, z: Int): BlockState = {
        val state = map(x - 128)(y)(z - 127);
        if (state == null)
            return Air;
        state
    }

    override def setBlock(x: Int, y: Int, z: Int, block_state: BlockState): Unit = {
        map(x)(y)(z) = block_state
    }

    override def getWorldBoundary: (BlockPos, BlockPos) = (BlockPos(-128, 0, -128), BlockPos(127, 255, 127))
}
package chunk

import protocols.ChunkSection

class ChunkIn {
    def getChunkSections: Seq[ChunkSection] = {
        Seq.empty[ChunkSection]
    }

    val chunk_pos: ChunkPos = ChunkPos(0, 0)
    def getPos: ChunkPos = {
        chunk_pos
    }

}

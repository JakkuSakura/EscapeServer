package chunk

class ChunkIn {
    def getChunkSections: Seq[Any] = {
        Seq.empty[Any]
    }

    val chunk_pos: ChunkPos = ChunkPos(0, 0)
    def getPos: ChunkPos = {
        chunk_pos
    }

}

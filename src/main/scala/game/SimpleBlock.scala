package game

import java.util.UUID

abstract class Block {
    def getBlockType: UUID
    def getNBT: NBT
}


class SimpleBlock(block_type: UUID) extends Block {
    override def getBlockType: UUID = block_type
    override def getNBT: NBT = null
}

package game

import java.util.UUID

import scala.collection.mutable
abstract class World {
    def getBlock(x: Int, y: Int, z: Int): Block
    def getMetaBlock(UUID: UUID): MetaBlock
}

class CubedWorld extends World {
    private val map = Array.ofDim[Block](256, 256, 256)
    private val uuid2block = new mutable.HashMap[UUID, MetaBlock]()
    override def getBlock(x: Int, y: Int, z: Int): Block = {
        val block = map(x)(y)(z)
        if (block == null) {
            BlockCollection.Air.asInstanceOf[Block]
        } else {
            block
        }
    }

    override def getMetaBlock(UUID: UUID): MetaBlock = {
        uuid2block(UUID)
    }
}
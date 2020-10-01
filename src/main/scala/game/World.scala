package game

import scala.collection.mutable
abstract class World {
    def getBlock(x: Int, y: Int, z: Int): Block
}

class CubedWorld extends World {
    private val map = Array.ofDim[Int](256, 256, 256)
    private val palette = new mutable.HashMap[Int, String]()
    private val extra_data = new mutable.HashMap[Int, NBT]()
    override def getBlock(x: Int, y: Int, z: Int): Block = {
        val block_in_chunk = map(x)(y)(z)
        val block_type = palette.get(block_in_chunk).orNull
        BlockCollection.getBlock(block_type).getOrElse(BlockCollection.UnknownBlock)
    }

}
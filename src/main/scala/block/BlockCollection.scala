package block

import scala.collection.mutable

object BlockCollection {
    val map = new mutable.HashMap[String, BlockState]()
    val Air = new Air()
    map.put("", Air)
    map.put("Air", Air)

    val Stone = new Stone()
    map.put("Stone", Stone)

    val UnknownBlock = new UnknownBlockState()
    map.put("UnknownBlock", UnknownBlock)

    def getBlock(block_type: String): Option[BlockState] = {
        map.get(block_type)
    }
}

package game

import scala.collection.mutable

object BlockCollection {
    val map = new mutable.HashMap[String, Block]()
    val Air = new Air()
    map.put("", Air)
    map.put("Air", Air)

    val Stone = new Stone()
    map.put("Stone", Stone)

    val UnknownBlock = new UnknownBlock()
    map.put("UnknownBlock", UnknownBlock)

    def getBlock(block_type: String): Option[Block] = {
        map.get(block_type)
    }
}

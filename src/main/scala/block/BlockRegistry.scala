package block

import scala.collection.mutable

object BlockRegistry {
    val str2state = new mutable.HashMap[String, BlockState]()
    val state2id = new mutable.HashMap[BlockState, Int]()

    // see https://github.com/ProtocolSupport/ProtocolSupport/blob/master/resources/resources/data/item.json
    // but notice the actual mapping

    val Air = new Air()
    str2state.put("", Air)
    str2state.put("Air", Air)
    state2id.put(Air, 0)

    val Stone = new Stone()
    str2state.put("Stone", Stone)
    state2id.put(Stone, 1)

    val Granite = new Granite()
    str2state.put("Granite", Granite)
    state2id.put(Granite, 2)

    val Grass = new Grass()
    str2state.put("Grass", Grass)
    state2id.put(Grass, 9)


    val UnknownBlock = new UnknownBlockState()
    str2state.put("UnknownBlock", UnknownBlock)
    state2id.put(UnknownBlock, 999)


    def getBlock(block_type: String): Option[BlockState] = {
        str2state.get(block_type)
    }

    def getBlockID(blockState: BlockState) : Int = {
        state2id(blockState)
    }
}

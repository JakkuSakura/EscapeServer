package game

class Block {
    def isCollidable: Boolean = true
    def getName: String = this.getClass.getCanonicalName
    def getNBT: NBT = null
}

class UnknownBlock extends Block {

}

class Air extends Block {
    override def isCollidable: Boolean = false
}

class Stone extends Block {

}
package game

import java.util.UUID

abstract class MetaBlock {
    def isCollidable: Boolean = true
    def getName: String = this.getClass.getCanonicalName
    def getUUID: UUID = UUID.fromString(this.getName)
}

class Air extends MetaBlock {
    override def isCollidable: Boolean = false
}

class Stone extends MetaBlock {

}
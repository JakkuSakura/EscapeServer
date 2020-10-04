package block

import com.github.steveice10.opennbt.tag.builtin.CompoundTag

class BlockState {
    def isCollidable: Boolean = true
    def getName: String = this.getClass.getCanonicalName
    def getNBT: CompoundTag = null
}

class UnknownBlockState extends BlockState {

}

class Air extends BlockState {
    override def isCollidable: Boolean = false
}

class Stone extends BlockState {

}
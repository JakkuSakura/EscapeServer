package utils

import java.util.function.IntConsumer

import utils.MathHelper.inclusiveBetween

class BitArray(val bitsPerEntry: Int, val arraySize: Int, val longArray: Array[Long]) {
    final private var maxEntryValue: Long = 0L

    {
        inclusiveBetween(1L, 32L, bitsPerEntry)
        maxEntryValue = (1L << bitsPerEntry) - 1L
        val i: Int = MathHelper.roundUp(arraySize * bitsPerEntry, 64) / 64
        if (longArray.length != i) throw new RuntimeException("Invalid length given for storage, got: " + longArray.length + " but expected: " + i)
    }


    def this(bitsPerEntryIn: Int, arraySizeIn: Int) {
        this(bitsPerEntryIn, arraySizeIn, new Array[Long](MathHelper.roundUp(arraySizeIn * bitsPerEntryIn, 64) / 64))
    }

    def swapAt(index: Int, value: Int): Int = {
        inclusiveBetween(0L, (arraySize - 1).toLong, index.toLong)
        inclusiveBetween(0L, maxEntryValue, value.toLong)
        val i = index * bitsPerEntry
        val j = i >> 6
        val k = (index + 1) * bitsPerEntry - 1 >> 6
        val l = i ^ j << 6
        var i1 = 0
        i1 = i1 | (longArray(j) >>> l & maxEntryValue).toInt
        longArray(j) = longArray(j) & ~(maxEntryValue << l) | (value.toLong & maxEntryValue) << l
        if (j != k) {
            val j1 = 64 - l
            val k1 = bitsPerEntry - j1
            i1 |= (longArray(k) << j1 & maxEntryValue).toInt
            longArray(k) = longArray(k) >>> k1 << k1 | (value.toLong & maxEntryValue) >> j1
        }
        i1
    }

    def setAt(index: Int, value: Int): Unit = {
        inclusiveBetween(0L, (arraySize - 1).toLong, index.toLong)
        inclusiveBetween(0L, maxEntryValue, value.toLong)
        val i = index * bitsPerEntry
        val j = i >> 6
        val k = (index + 1) * bitsPerEntry - 1 >> 6
        val l = i ^ j << 6
        longArray(j) = longArray(j) & ~(maxEntryValue << l) | (value.toLong & maxEntryValue) << l
        if (j != k) {
            val i1 = 64 - l
            val j1 = bitsPerEntry - i1
            longArray(k) = longArray(k) >>> j1 << j1 | (value.toLong & maxEntryValue) >> i1
        }
    }

    def getAt(index: Int): Int = {
        inclusiveBetween(0L, (arraySize - 1).toLong, index.toLong)
        val i = index * bitsPerEntry
        val j = i >> 6
        val k = (index + 1) * bitsPerEntry - 1 >> 6
        val l = i ^ j << 6
        if (j == k) (longArray(j) >>> l & maxEntryValue).toInt
        else {
            val i1 = 64 - l
            ((longArray(j) >>> l | longArray(k) << i1) & maxEntryValue).toInt
        }
    }

    def getBackingLongArray: Array[Long] = longArray

    def getAll(consumer: IntConsumer): Unit = {
        val i = longArray.length
        if (i != 0) {
            var j = 0
            var k = longArray(0)
            var l = if (i > 1) longArray(1)
            else 0L
            for (i1 <- 0 until arraySize) {
                val j1 = i1 * bitsPerEntry
                val k1 = j1 >> 6
                val l1 = (i1 + 1) * bitsPerEntry - 1 >> 6
                val i2 = j1 ^ k1 << 6
                if (k1 != j) {
                    k = l
                    l = if (k1 + 1 < i) longArray(k1 + 1)
                    else 0L
                    j = k1
                }
                if (k1 == l1) consumer.accept((k >>> i2 & maxEntryValue).toInt)
                else {
                    val j2 = 64 - i2
                    consumer.accept(((k >>> i2 | l << j2) & maxEntryValue).toInt)
                }
            }
        }
    }
}
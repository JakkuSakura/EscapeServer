package utils

object MathHelper {
    def roundUp(number: Int, interval_ : Int): Int = {
        var interval = interval_
        if (interval == 0) 0
        else if (number == 0) interval
        else {
            if (number < 0) interval *= -1
            val i = number % interval
            if (i == 0) number
            else number + interval - i
        }

    }

    /**
     * Validate that the specified primitive value falls between the two
     * inclusive values specified; otherwise, throws an exception.
     *
     * <pre>Validate.inclusiveBetween(0, 2, 1);</pre>
     *
     * @param start the inclusive start value
     * @param end   the inclusive end value
     * @param value the value to validate
     * @throws IllegalArgumentException if the value falls outside the boundaries (inclusive)
     * @since 3.3
     */
    @SuppressWarnings(Array("boxing"))
    def inclusiveBetween(start: Long, `end`: Long, value: Long): Unit = {
        // TODO when breaking BC, consider returning value
        if (value < start || value > `end`) throw new IllegalArgumentException(String.format("The value %s is not in the specified inclusive range of %s to %s", value, start, `end`))
    }


}

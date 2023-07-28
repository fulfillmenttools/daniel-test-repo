package com.fulfillmenttools.danieltestapplication

import org.junit.Assert.assertEquals
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun addition_isCorrect_01() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun addition_isCorrect_02() {
        assertEquals(6, 2 + 2 + 2)
    }

    @Test
    fun addition_isCorrect_03() {
        assertEquals(8, 2 + 2 + 2 + 2)
    }

    @Test
    fun addition_isCorrect_04() {
        assertEquals(28, 2 + 2 + 7)
    }
}

@file:Suppress("unused")

package game.client

import game.common.getAnswer
import kotlin.test.Test
import kotlin.test.assertEquals

class ClientTest {
    // JS test names cannot contain illegal characters.

    @Test
    fun the_answer_should_be_correct() {
        assertEquals(42, getAnswer())
    }
}

package com.pranvir.contactcasestyler

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CaseStylesTest {

    @Test fun camel_basic() {
        assertEquals("alexMorgan", CaseStyles.apply("Alex Morgan", CaseStyles.Style.CAMEL))
    }

    @Test fun camel_singleWord() {
        assertEquals("alex", CaseStyles.apply("Alex", CaseStyles.Style.CAMEL))
    }

    @Test fun camel_messySpacingAndCase() {
        assertEquals("alexMorgan", CaseStyles.apply("  ALEX   morgan ", CaseStyles.Style.CAMEL))
    }

    @Test fun pascal_threeWords() {
        assertEquals("AlexKumarMorgan", CaseStyles.apply("alex kumar morgan", CaseStyles.Style.PASCAL))
    }

    @Test fun snake_stripsPunctuation() {
        assertEquals("dr_alex_morgan", CaseStyles.apply("Dr. Alex Morgan!!", CaseStyles.Style.SNAKE))
    }

    @Test fun kebab_basic() {
        assertEquals("alex-morgan", CaseStyles.apply("Alex Morgan", CaseStyles.Style.KEBAB))
    }

    @Test fun upperSnake_basic() {
        assertEquals("ALEX_MORGAN", CaseStyles.apply("Alex Morgan", CaseStyles.Style.UPPER_SNAKE))
    }

    @Test fun title_lowercasesRest() {
        assertEquals("Alex Morgan", CaseStyles.apply("ALEX MORGAN", CaseStyles.Style.TITLE))
    }

    @Test fun lower_basic() {
        assertEquals("alex morgan", CaseStyles.apply("Alex Morgan", CaseStyles.Style.LOWER))
    }

    @Test fun upper_basic() {
        assertEquals("ALEX MORGAN", CaseStyles.apply("Alex Morgan", CaseStyles.Style.UPPER))
    }

    @Test fun bold_mapsAlphabet() {
        val out = CaseStyles.apply("Alex", CaseStyles.Style.BOLD)
        assertEquals(
            Character.toString(0x1D400) + Character.toString(0x1D425) +
                Character.toString(0x1D41E) + Character.toString(0x1D431),
            out,
        )
    }

    @Test fun swap_twoWords() {
        assertEquals("Morgan Alex", CaseStyles.apply("Alex Morgan", CaseStyles.Style.SWAP))
    }

    @Test fun swap_singleWordUnchanged() {
        assertEquals("Alex", CaseStyles.apply("Alex", CaseStyles.Style.SWAP))
    }

    @Test fun lastFirst_basic() {
        assertEquals("Morgan, Alex", CaseStyles.apply("Alex Morgan", CaseStyles.Style.LAST_FIRST))
    }

    @Test fun initials_twoWords() {
        assertEquals("A. Morgan", CaseStyles.apply("Alex Morgan", CaseStyles.Style.INITIALS))
    }

    @Test fun initials_threeWords() {
        assertEquals("A. K. Morgan", CaseStyles.apply("Alex Kumar Morgan", CaseStyles.Style.INITIALS))
    }

    @Test fun spaced_basic() {
        assertEquals("A L E X   M O R G A N", CaseStyles.apply("Alex Morgan", CaseStyles.Style.SPACED))
    }

    @Test fun dotted_basic() {
        assertEquals("A.l.e.x M.o.r.g.a.n", CaseStyles.apply("Alex Morgan", CaseStyles.Style.DOTTED))
    }

    @Test fun leet_basic() {
        assertEquals("4l3x M0rg4n", CaseStyles.apply("Alex Morgan", CaseStyles.Style.LEET))
    }

    @Test fun alternating_basic() {
        assertEquals("aLeX mOrGaN", CaseStyles.apply("Alex Morgan", CaseStyles.Style.ALTERNATING))
    }

    @Test fun fullwidth_mapsAscii() {
        val out = CaseStyles.apply("Alex", CaseStyles.Style.FULLWIDTH)
        assertEquals(
            Character.toString(0xFF21) + Character.toString(0xFF4C) +
                Character.toString(0xFF45) + Character.toString(0xFF58),
            out,
        )
    }

    @Test fun circled_lowercase() {
        val out = CaseStyles.apply("alex", CaseStyles.Style.CIRCLED)
        assertEquals(
            Character.toString(0x24D0) + Character.toString(0x24DB) +
                Character.toString(0x24D4) + Character.toString(0x24E7),
            out,
        )
    }

    @Test fun fraktur_usesBoldBlock() {
        val out = CaseStyles.apply("Al", CaseStyles.Style.FRAKTUR)
        assertEquals(
            "${Character.toString(0x1D504)}${Character.toString(0x1D529)}",
            out,
        )
    }

    @Test fun script_usesBoldBlock() {
        val out = CaseStyles.apply("Al", CaseStyles.Style.SCRIPT)
        assertEquals(
            "${Character.toString(0x1D4D0)}${Character.toString(0x1D4F5)}",
            out,
        )
    }

    @Test fun strike_addsCombiningMark() {
        val out = CaseStyles.apply("Alex", CaseStyles.Style.STRIKE)
        assertEquals(8, out.length)
        assertTrue(out.startsWith("A"))
        assertEquals(4, out.count { it.code == 0x0336 })
    }

    @Test fun brackets_wrapsTitle() {
        val out = CaseStyles.apply("Alex Morgan", CaseStyles.Style.BRACKETS)
        assertTrue(out.startsWith(Character.toString(0x3010)))
        assertTrue(out.endsWith(Character.toString(0x3011)))
        assertTrue(out.contains("Alex Morgan"))
    }

    @Test fun stars_wrapsTitle() {
        val out = CaseStyles.apply("Alex Morgan", CaseStyles.Style.STARS)
        assertTrue(out.startsWith(Character.toString(0x2605)))
        assertTrue(out.endsWith(Character.toString(0x2605)))
        assertTrue(out.contains("Alex Morgan"))
    }

    @Test fun safeFlags_plainStylesAreSafe() {
        val safe = listOf(
            CaseStyles.Style.CAMEL, CaseStyles.Style.PASCAL, CaseStyles.Style.SNAKE,
            CaseStyles.Style.KEBAB, CaseStyles.Style.UPPER_SNAKE, CaseStyles.Style.TITLE,
            CaseStyles.Style.LOWER, CaseStyles.Style.UPPER, CaseStyles.Style.SWAP,
            CaseStyles.Style.LAST_FIRST, CaseStyles.Style.INITIALS, CaseStyles.Style.SPACED,
            CaseStyles.Style.DOTTED, CaseStyles.Style.ALTERNATING, CaseStyles.Style.BRACKETS,
            CaseStyles.Style.STARS,
        )
        assertTrue(safe.all { it.safe })
    }

    @Test fun safeFlags_texturesAreFun() {
        val funStyles = listOf(
            CaseStyles.Style.BOLD, CaseStyles.Style.LEET, CaseStyles.Style.FULLWIDTH,
            CaseStyles.Style.CIRCLED, CaseStyles.Style.FRAKTUR, CaseStyles.Style.SCRIPT,
            CaseStyles.Style.STRIKE,
        )
        assertFalse(funStyles.any { it.safe })
    }

    @Test fun empty_returnsInput() {
        assertEquals("", CaseStyles.apply("", CaseStyles.Style.CAMEL))
        assertEquals("   ", CaseStyles.apply("   ", CaseStyles.Style.CAMEL))
    }
}

package com.pranvir.contactcasestyler

import org.junit.Assert.assertEquals
import org.junit.Test

class CaseStylesTest {

    @Test fun camel_basic() {
        assertEquals("pranvirSingh", CaseStyles.apply("Pranvir Singh", CaseStyles.Style.CAMEL))
    }

    @Test fun camel_singleWord() {
        assertEquals("pranvir", CaseStyles.apply("Pranvir", CaseStyles.Style.CAMEL))
    }

    @Test fun camel_messySpacingAndCase() {
        assertEquals("pranvirSingh", CaseStyles.apply("  PRANVIR   singh ", CaseStyles.Style.CAMEL))
    }

    @Test fun pascal_threeWords() {
        assertEquals("PranvirKumarSingh", CaseStyles.apply("pranvir kumar singh", CaseStyles.Style.PASCAL))
    }

    @Test fun snake_stripsPunctuation() {
        assertEquals("dr_pranvir_singh", CaseStyles.apply("Dr. Pranvir Singh!!", CaseStyles.Style.SNAKE))
    }

    @Test fun kebab_basic() {
        assertEquals("pranvir-singh", CaseStyles.apply("Pranvir Singh", CaseStyles.Style.KEBAB))
    }

    @Test fun upperSnake_basic() {
        assertEquals("PRANVIR_SINGH", CaseStyles.apply("Pranvir Singh", CaseStyles.Style.UPPER_SNAKE))
    }

    @Test fun title_lowercasesRest() {
        assertEquals("Pranvir Singh", CaseStyles.apply("PRANVIR SINGH", CaseStyles.Style.TITLE))
    }

    @Test fun lower_basic() {
        assertEquals("pranvir singh", CaseStyles.apply("Pranvir Singh", CaseStyles.Style.LOWER))
    }

    @Test fun upper_basic() {
        assertEquals("PRANVIR SINGH", CaseStyles.apply("Pranvir Singh", CaseStyles.Style.UPPER))
    }

    @Test fun bold_mapsAlphabet() {
        assertEquals("𝐏𝐫𝐚𝐧𝐯𝐢𝐫", CaseStyles.apply("Pranvir", CaseStyles.Style.BOLD))
    }

    @Test fun empty_returnsInput() {
        assertEquals("", CaseStyles.apply("", CaseStyles.Style.CAMEL))
        assertEquals("   ", CaseStyles.apply("   ", CaseStyles.Style.CAMEL))
    }
}

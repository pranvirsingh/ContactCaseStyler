package com.pranvir.contactcasestyler

/** One style for every contact name. Pure logic, no Android calls. */
object CaseStyles {

    enum class Style(val label: String, val example: String) {
        CAMEL("camelCase", "pranvirSingh"),
        PASCAL("PascalCase", "PranvirSingh"),
        SNAKE("snake_case", "pranvir_singh"),
        KEBAB("kebab-case", "pranvir-singh"),
        UPPER_SNAKE("UPPER_SNAKE", "PRANVIR_SINGH"),
        TITLE("Title Case", "Pranvir Singh"),
        LOWER("lowercase", "pranvir singh"),
        UPPER("UPPERCASE", "PRANVIR SINGH"),
        BOLD("Bold", "𝐏𝐫𝐚𝐧𝐯𝐢𝐫 𝐒𝐢𝐧𝐠𝐡"),
    }

    /** "  PRANVIR   Singh!! " -> ["pranvir", "singh"]-ish word parts. */
    fun words(displayName: String): List<String> =
        displayName.trim()
            .split(Regex("\\s+"))
            .map { it.trim { c -> !c.isLetterOrDigit() } }
            .filter { it.isNotEmpty() }

    fun apply(displayName: String, style: Style): String {
        val words = words(displayName)
        if (words.isEmpty()) return displayName
        val lower = words.map { it.lowercase() }
        return when (style) {
            Style.CAMEL -> lower.first() + lower.drop(1).joinToString("") { it.title() }
            Style.PASCAL -> lower.joinToString("") { it.title() }
            Style.SNAKE -> lower.joinToString("_")
            Style.KEBAB -> lower.joinToString("-")
            Style.UPPER_SNAKE -> lower.joinToString("_") { it.uppercase() }
            Style.TITLE -> lower.joinToString(" ") { it.title() }
            Style.LOWER -> lower.joinToString(" ")
            Style.UPPER -> lower.joinToString(" ") { it.uppercase() }
            Style.BOLD -> toBold(lower.joinToString(" ") { it.title() })
        }
    }

    private fun String.title(): String =
        replaceFirstChar { it.titlecase() }

    /** Plain A–Z/a–z/0–9 to Mathematical Bold. Anything else passes through. */
    fun toBold(text: String): String = buildString {
        for (c in text) {
            when (c) {
                in 'A'..'Z' -> appendCodePoint(0x1D400 + (c - 'A'))
                in 'a'..'z' -> appendCodePoint(0x1D41A + (c - 'a'))
                in '0'..'9' -> appendCodePoint(0x1D7CE + (c - '0'))
                else -> append(c)
            }
        }
    }

    private fun StringBuilder.appendCodePoint(codePoint: Int) {
        append(Character.highSurrogate(codePoint))
        append(Character.lowSurrogate(codePoint))
    }
}

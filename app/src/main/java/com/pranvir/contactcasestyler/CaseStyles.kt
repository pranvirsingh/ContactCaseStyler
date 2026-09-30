package com.pranvir.contactcasestyler

/** One style for every contact name. Pure logic, no Android calls. */
object CaseStyles {

    enum class Style(val label: String, val example: String, val safe: Boolean = true) {
        CAMEL("camelCase", "alexMorgan"),
        PASCAL("PascalCase", "AlexMorgan"),
        SNAKE("snake_case", "alex_morgan"),
        KEBAB("kebab-case", "alex-morgan"),
        UPPER_SNAKE("UPPER_SNAKE", "ALEX_MORGAN"),
        TITLE("Title Case", "Alex Morgan"),
        LOWER("lowercase", "alex morgan"),
        UPPER("UPPERCASE", "ALEX MORGAN"),
        BOLD("Bold", toBold("Alex Morgan"), false),
        SWAP("Swap", "Morgan Alex"),
        LAST_FIRST("Last, First", "Morgan, Alex"),
        INITIALS("Initials", "A. Morgan"),
        SPACED("Spaced", "A L E X"),
        DOTTED("Dotted", "A.l.e.x"),
        LEET("Leet", "4l3x M0rg4n", false),
        ALTERNATING("Alternating", "aLeX mOrGaN"),
        FULLWIDTH("Fullwidth", fullwidth("Alex"), false),
        CIRCLED("Circled", circled("alex"), false),
        FRAKTUR("Fraktur", fraktur("Alex"), false),
        SCRIPT("Script", script("Alex"), false),
        STRIKE("Strike", strike("Alex"), false),
        BRACKETS("Brackets", 0x3010.toChar() + "Alex" + 0x3011.toChar()),
        STARS("Stars", 0x2605.toChar() + " Alex " + 0x2605.toChar()),
    }

    /** "  ALEX   Morgan!! " -> ["alex", "morgan"]-ish word parts. */
    fun words(displayName: String): List<String> =
        displayName.trim()
            .split(Regex("\\s+"))
            .map { it.trim { c -> !c.isLetterOrDigit() } }
            .filter { it.isNotEmpty() }

    fun apply(displayName: String, style: Style): String {
        val words = words(displayName)
        if (words.isEmpty()) return displayName
        val lower = words.map { it.lowercase() }
        val titled = lower.joinToString(" ") { it.title() }
        return when (style) {
            Style.CAMEL -> lower.first() + lower.drop(1).joinToString("") { it.title() }
            Style.PASCAL -> lower.joinToString("") { it.title() }
            Style.SNAKE -> lower.joinToString("_")
            Style.KEBAB -> lower.joinToString("-")
            Style.UPPER_SNAKE -> lower.joinToString("_") { it.uppercase() }
            Style.TITLE -> titled
            Style.LOWER -> lower.joinToString(" ")
            Style.UPPER -> lower.joinToString(" ") { it.uppercase() }
            Style.BOLD -> toBold(titled)
            Style.SWAP -> swap(words)
            Style.LAST_FIRST -> lastFirst(words)
            Style.INITIALS -> initials(words)
            Style.SPACED -> spaced(lower.joinToString(" ").uppercase())
            Style.DOTTED -> lower.joinToString(" ") { it.title().toList().joinToString(".") }
            Style.LEET -> leet(titled)
            Style.ALTERNATING -> alternating(titled)
            Style.FULLWIDTH -> fullwidth(titled)
            Style.CIRCLED -> circled(lower.joinToString(" "))
            Style.FRAKTUR -> fraktur(titled)
            Style.SCRIPT -> script(titled)
            Style.STRIKE -> strike(titled)
            Style.BRACKETS -> 0x3010.toChar() + titled + 0x3011.toChar()
            Style.STARS -> 0x2605.toChar() + " " + titled + " " + 0x2605.toChar()
        }
    }

    private fun String.title(): String =
        replaceFirstChar { it.titlecase() }

    /** "Alex Morgan" -> "Morgan Alex". Single word stays titled. */
    fun swap(words: List<String>): String {
        val titled = words.map { it.lowercase().title() }
        if (titled.size < 2) return titled.first()
        return titled.last() + " " + titled.dropLast(1).joinToString(" ")
    }

    /** "Alex Morgan" -> "Morgan, Alex". */
    fun lastFirst(words: List<String>): String {
        val titled = words.map { it.lowercase().title() }
        if (titled.size < 2) return titled.first()
        return titled.last() + ", " + titled.dropLast(1).joinToString(" ")
    }

    /** "Alex Kumar Morgan" -> "A. K. Morgan". */
    fun initials(words: List<String>): String {
        val titled = words.map { it.lowercase().title() }
        if (titled.size < 2) return titled.first()
        return titled.dropLast(1).joinToString(" ") { "${it.first()}." } + " " + titled.last()
    }

    /** "ALEX MORGAN" -> "A L E X   M O R G A N". */
    fun spaced(upper: String): String =
        upper.toList().joinToString(" ")

    private val leetMap = mapOf(
        'a' to '4', 'e' to '3', 'i' to '1', 'o' to '0', 's' to '5', 't' to '7',
    )

    /** "Alex Morgan" -> "4l3x M0rg4n". */
    fun leet(text: String): String =
        text.map { leetMap[it.lowercaseChar()] ?: it }.joinToString("")

    /** "Alex Morgan" -> "aLeX mOrGaN". Case flips per letter, spaces ignored. */
    fun alternating(text: String): String = buildString {
        var upper = false
        for (c in text) {
            if (!c.isLetter()) {
                append(c)
                continue
            }
            append(if (upper) c.uppercaseChar() else c.lowercaseChar())
            upper = !upper
        }
    }

    /** Plain ASCII to fullwidth. Space becomes the ideographic space (U+3000). */
    fun fullwidth(text: String): String = buildString {
        for (c in text) {
            when {
                c == ' ' -> append(0x3000.toChar())
                c in '!'..'~' -> append((c.code + 0xFEE0).toChar())
                else -> append(c)
            }
        }
    }

    /** a-z, A-Z, 0-9 to circled forms. Anything else passes through. */
    fun circled(text: String): String = buildString {
        for (c in text) {
            when (c) {
                in 'A'..'Z' -> appendCodePoint(0x24B6 + (c - 'A'))
                in 'a'..'z' -> appendCodePoint(0x24D0 + (c - 'a'))
                in '1'..'9' -> append((0x2460 + (c - '1')).toChar())
                '0' -> append(0x24EA.toChar())
                else -> append(c)
            }
        }
    }

    /** A–Z/a–z to Mathematical Bold Fraktur. */
    fun fraktur(text: String): String = buildString {
        for (c in text) {
            when (c) {
                in 'A'..'Z' -> appendCodePoint(0x1D504 + (c - 'A'))
                in 'a'..'z' -> appendCodePoint(0x1D51E + (c - 'a'))
                else -> append(c)
            }
        }
    }

    /** A–Z/a–z to Mathematical Bold Script. */
    fun script(text: String): String = buildString {
        for (c in text) {
            when (c) {
                in 'A'..'Z' -> appendCodePoint(0x1D4D0 + (c - 'A'))
                in 'a'..'z' -> appendCodePoint(0x1D4EA + (c - 'a'))
                else -> append(c)
            }
        }
    }

    /** Each letter gets a combining strikethrough (U+0336). */
    fun strike(text: String): String = buildString {
        for (c in text) {
            append(c)
            if (c != ' ') append(0x0336.toChar())
        }
    }

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

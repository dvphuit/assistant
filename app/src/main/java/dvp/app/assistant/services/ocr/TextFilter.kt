package dvp.app.assistant.services.ocr

internal object TextFilter {
    private const val hiragana = "[\\u3040-\\u309F]"
    private const val katakana = "[\\u30A0-\\u30FF]"
    private const val kanji = "[\\u4E00-\\u9FAF]"

    private val re = Regex("$hiragana|$katakana|$kanji")

    fun containsJp(text: String) = re.containsMatchIn(text)

    fun removeBr(text: String) = text.replace("\n", "")
}
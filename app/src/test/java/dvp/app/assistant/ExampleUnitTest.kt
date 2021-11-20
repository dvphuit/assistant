package dvp.app.assistant


import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

    val pHiragana = "[\\u3041-\\u3096\\u309D-\\u309F]|\\uD82C\\uDC01|\\uD83C\\uDE00";
    val pKatakana = "[\\u30A1-\\u30FA\\u30FD-\\u30FF\\u31F0-\\u31FF\\u32D0-\\u32FE\\u3300-\\u3357\\uFF66-\\uFF6F\\uFF71-\\uFF9D]|\\uD82C\\uDC00";
    val pHan = "[\\u2E80-\\u2E99\\u2E9B-\\u2EF3\\u2F00-\\u2FD5\\u3005\\u3007\\u3021-\\u3029\\u3038-\\u303B\\u3400-\\u4DB5\\u4E00-\\u9FD5\\uF900-\\uFA6D\\uFA70-\\uFAD9]|[\\uD840-\\uD868\\uD86A-\\uD86C\\uD86F-\\uD872][\\uDC00-\\uDFFF]|\\uD869[\\uDC00-\\uDED6\\uDF00-\\uDFFF]|\\uD86D[\\uDC00-\\uDF34\\uDF40-\\uDFFF]|\\uD86E[\\uDC00-\\uDC1D\\uDC20-\\uDFFF]|\\uD873[\\uDC00-\\uDEA1]|\\uD87E[\\uDC00-\\uDE1D]";
//    val rx = Regex("^[a-zA-Z0-9()*_\\-!#%^&,.:'\"\$\\]\\[]+\$")
//    val rx = Regex("^[$pHiragana$pKatakana$pHan]")

    private val hiragana = "[\\u3040-\\u309F]"
    private val katakana = "[\\u30A0-\\u30FF]"
    private val kanji = "[\\u4E00-\\u9FAF]"
    private val rx = Regex("$hiragana|$katakana|$kanji")

    val test1 = "このAb12:1"
    val test2 = "100%"
    val test3 = "10:00"

    @Test
    fun callApiTest() {
        println("Matches 1 -> ${rx.containsMatchIn(test1)}")
        println("Matches 2 -> ${rx.containsMatchIn(test2)}")
        println("Matches 3 -> ${rx.containsMatchIn(test3)}")
    }
}
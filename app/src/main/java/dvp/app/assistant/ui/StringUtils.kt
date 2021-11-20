package dvp.app.assistant.ui

import android.text.TextPaint
import android.text.TextUtils
import java.util.*

object StringUtils {
    fun isSingleLine(text: String?): Boolean {
        val st = StringTokenizer(text)
        return st.countTokens() < 2
    }

    fun getLongestWord(text: String?): String {
        val st = StringTokenizer(text)
        var result = ""
        while (st.hasMoreTokens()) {
            val s = st.nextToken()
            if (s.length > result.length) {
                result = s
            }
        }
        return result
    }

    fun ellipsizeText(text: String, width: Float, tp: TextPaint?): String {
        var str = text
        val st = StringTokenizer(text)
        while (st.hasMoreTokens()) {
            val s = st.nextToken()
            val t = TextUtils.ellipsize(s, tp, width, TextUtils.TruncateAt.END).toString()
            if (!TextUtils.equals(t, s)) {
                str = str.replace(s, t)
            }
        }
        return str
    }
}
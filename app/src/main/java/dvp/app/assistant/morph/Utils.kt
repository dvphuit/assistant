package dvp.app.assistant.morph

import dvp.app.assistant.morph.Const.paramsCount
import java.lang.Character.isDigit

object Utils {

    fun parseData(path: String): List<List<String>> {
        val reSegment = Regex("([a-z]+[-.,\\d ]*)", RegexOption.IGNORE_CASE)
        val reData = Regex("([a-z]+|-?[.\\d]*\\d)", RegexOption.IGNORE_CASE)
        return reSegment
            .findAll(path)
            .map { seg ->
                reData.findAll(seg.value)
                    .map { it.value }
                    .toList()
            }.toList()
    }


    fun parsePathString(pathInput: String): MutableList<MutableList<String>> {

        var path = SvgPath(pathInput)

        skipSpaces(path);

        while (path.index < path.max) {
            scanSegment(path);
        }

        if (path.err.isNotEmpty()) {
            // @ts-ignore
            path.segments = mutableListOf()
        } else {
            if (!"m".contains(path.segments[0][0], ignoreCase = true)) {
                path.err = "invalidPathValue" + ": missing M/m";
                path.segments = mutableListOf()
            } else {
                path.segments[0][0] = "M"
            }
        }

        return path.segments;
    }

    fun scanSegment(path: SvgPath) {
        var max = path.max
        var pathValue = path.pathValue
        var index = path.index
        var cmdCode = pathValue[index].code
        var reqParams = paramsCount[pathValue[index].lowercase()]!!
        path.segmentStart = index

        if (!isPathCommand(pathValue[index])) {
            println("TEST: is not a path command")
            return
        }
        path.index += 1
        skipSpaces(path)

        path.data = mutableListOf()

        if (reqParams == 0) {
            finalizeSegment(path)
            return
        }

        while (true) {
            for (i in reqParams downTo 1) {
                if (isArcCommand(cmdCode) && (i == 3 || i == 4)) {
                    scanFlag(path)
                } else {
                    scanParam(path)
                }
                path.data.add(path.param)
                skipSpaces(path)

                if (path.index < max && pathValue[path.index].code == 0x2C/* , */) {
                    path.index += 1
                    skipSpaces(path)
                }
            }

            if (path.index > path.max) {
                break
            }

            if (!isDigitStart(pathValue[path.index])) {
                break
            }
        }

        finalizeSegment(path)
    }

    private fun scanParam(path: SvgPath) {
        var max = path.max;
        var pathValue = path.pathValue;
        var start = path.index;
        var index = start;
        var zeroFirst = false;
        var hasCeiling = false;
        var hasDecimal = false;
        var hasDot = false;

        if (index >= max) {
            path.err =
                "invalidPathValue" + " at " + index + ": missing param " + (pathValue[index]);
            return;
        }
        var ch = pathValue[index].code
        if (ch == 0x2B/* + */ || ch == 0x2D/* - */) {
            index += 1;
            ch = if (index < max) pathValue[index].code else 0
        }

        if (!isDigit(ch) && ch != 0x2E/* . */) {
            // path.err = 'SvgPath: param should start with 0..9 or `.` (at pos ' + index + ')';
            path.err =
                "invalidPathValue" + " at index " + index + ": " + (pathValue[index]) + " is not a number";
            return;
        }
        if (ch != 0x2E/* . */) {
            zeroFirst = (ch == 0x30/* 0 */);
            index += 1;

            ch = if (index < max) pathValue[index].code else 0

            if (zeroFirst && index < max) {
                if (ch != 0 && isDigit(ch)) {
                    path.err =
                        "invalidPathValue" + " at index " + start + ": " + (pathValue[start]) + " illegal number";
                    return;
                }
            }

            while (index < max && isDigit(pathValue[index])) {
                index += 1;
                hasCeiling = true;
            }
            ch = if (index < max) pathValue[index].code else 0
        }

        if (ch == 0x2E/* . */) {
            hasDot = true;
            index += 1;
            while (isDigit(pathValue[index])) {
                index += 1;
                hasDecimal = true;
            }
            ch = if (index < max) pathValue[index].code else 0;
        }

        if (ch == 0x65/* e */ || ch == 0x45/* E */) {
            if (hasDot && !hasCeiling && !hasDecimal) {
                path.err =
                    "invalidPathValue" + " at index " + index + ": " + (pathValue[index]) + " invalid float exponent";
                return;
            }

            index += 1;

            ch = if (index < max) pathValue[index].code else 0;
            if (ch == 0x2B/* + */ || ch == 0x2D/* - */) {
                index += 1;
            }
            if (index < max && isDigit(pathValue[index])) {
                while (index < max && isDigit(pathValue[index])) {
                    index += 1;
                }
            } else {
                path.err =
                    "invalidPathValue" + " at index " + index + ": " + (pathValue[index]) + " invalid float exponent";
                return;
            }
        }

        path.index = index;
        path.param = path.pathValue.slice(start..index).trim()
    }

    private fun skipSpaces(path: SvgPath) {
        val pathValue = path.pathValue
        val max = path.max
        while (path.index < max && pathValue[path.index].isWhitespace()) {
            path.index += 1
        }
    }

    private fun isSpace(ch: Int): Boolean {
        val specialSpaces = arrayOf(
            0x1680, 0x180E, 0x2000, 0x2001, 0x2002, 0x2003, 0x2004, 0x2005, 0x2006,
            0x2007, 0x2008, 0x2009, 0x200A, 0x202F, 0x205F, 0x3000, 0xFEFF
        )
        return (ch == 0x0A) || (ch == 0x0D) || (ch == 0x2028) || (ch == 0x2029) // Line terminators
                // White spaces
                || (ch == 0x20) || (ch == 0x09) || (ch == 0x0B) || (ch == 0x0C) || (ch == 0xA0)
                || (ch >= 0x1680 && specialSpaces.indexOf(ch) >= 0)
    }


    private fun finalizeSegment(path: SvgPath) {
        var pathCommand = path.pathValue[path.segmentStart].toString()
        var lk = pathCommand.lowercase()
        var data = path.data

        if (lk == "m" && data.size > 2) {
            path.segments.add(mutableListOf(pathCommand, data[0], data[1]))
            data = data.take(2).toMutableList()
            lk = "l"
            pathCommand = if (pathCommand == "m") "l" else "L"
        }

        var dataSize = data.size
        while (dataSize >= paramsCount[lk]!!) {
            val segment = (listOf(pathCommand) + data.take(paramsCount[lk]!!)).toMutableList()
            path.segments.add(segment)
            dataSize = 0
            if (lk == "z") {
                break
            }
        }
    }

    private fun scanFlag(path: SvgPath) {
        var index = path.index;
        var ch = path.pathValue[index].code

        if (ch == 0x30/* 0 */) {
            path.param = "0"
            path.index += 1
            return;
        }

        if (ch == 0x31/* 1 */) {
            path.param = "1"
            path.index += 1
            return;
        }

        path.err = "invalidPathValue: invalid Arc flag \"$ch\", expecting 0 or 1 at index $index"
    }

    private fun isPathCommand(ch: Char): Boolean {
//        return when (code or 0x20) {
//            0x60, 0x7A, 0x6C, 0x68, 0x76, 0x63, 0x73, 0x71, 0x74, 0x61 -> true
//            else -> false
//        }
        return arrayOf(
            'm',
            'z',
            'l',
            'h',
            'v',
            'c',
            's',
            'q',
            't',
            'a'
        ).contains(ch.lowercaseChar())
    }

    private fun isDigitStart(ch: Char): Boolean {
        val code = ch.code
        return (code in 48..57) /* 0..9 */
                || code == 0x2B /* + */
                || code == 0x2D /* - */
                || code == 0x2E /* . */
    }

    private fun isArcCommand(code: Int): Boolean {
        return (code or 0x20) == 0x61
    }
}
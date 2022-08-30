package dvp.app.assistant.morph

object Const {

    var epsilon = 1e-9;

    val paramsCount = mapOf(
        "a" to 7,
        "c" to 6,
        "h" to 1,
        "l" to 2,
        "m" to 2,
        "r" to 4,
        "q" to 4,
        "s" to 4,
        "t" to 2,
        "v" to 1,
        "z" to 0,
    )

    val paramsParser = mapOf(
        "x1" to 0, "y1" to 0, "x2" to 0, "y2" to 0, "x" to 0, "y" to 0, "qx" to null, "qy" to null,
    )


    val defaultOptions = mapOf(
        "origin" to arrayOf(0, 0, 0),
        "round" to 4,
    )

    val defaultOptions2 = mapOf(
        "duration" to 700,
        "delay" to 0,
        "easing" to "linear",
        "repeat" to 0,
        "repeatDelay" to 0,
        "yoyo" to false,
        "resetStart" to false,
        "offset" to 0,
    )

}
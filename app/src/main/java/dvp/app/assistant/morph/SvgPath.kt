package dvp.app.assistant.morph

class SvgPath(pathString: String) {
    var segments = mutableListOf<MutableList<String>>()
    var pathValue = pathString
    var max = pathString.length
    var index = 0
    var param = ""
    var segmentStart = 0
    var data = mutableListOf<String>()
    var err = ""
    var test = arrayListOf<String>()
}
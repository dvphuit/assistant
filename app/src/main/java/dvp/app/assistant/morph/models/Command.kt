package dvp.app.assistant.morph.models

class Command(val name: String, vararg val points: Float) {
    fun isEmpty(): Boolean {
        return name.isEmpty() && points.isEmpty()
    }

    fun length(): Int{
        return 1 + points.size
    }

    override fun toString(): String {
        return "$name ${points.joinToString(" ")}"
    }
}

package dvp.app.assistant

import dvp.app.assistant.morph.PathUtils
import dvp.app.assistant.morph.SvgPath
import dvp.app.assistant.morph.Utils
import org.junit.Test

class SvgUtilTest {
    @Test
    fun finalizeSegmentTest(){
        val svgPath = SvgPath("M 77 77 L 420 77 L 420 245 L 77 245 Z").apply {
            index = 0
            max = pathValue.length
            segmentStart = 0
        }

//        val segments = Utils.parsePathString("M 77 77 L 420 77 L 420 245 L 77 245 Z")
        val segments = PathUtils.normalizePath("M169.6,80H144h-9l-9-9l-9,9h-9H80c-4.4,0-8,3.6-8,8v44.7c0,4.4,3.6,8,8,8h89.6 c4.4,0,8-3.6,8-8V88C177.6,83.6,174,80,169.6,80z")
        println("TEST: $segments")
    }


    @Test
    fun list(){
        val list = arrayOf(1,2,3,4)
        list.slice(1..2)
        println(list.toList())

        var x = 3
        x.plus(4)
        println("TEST: $x")

        val test = mutableListOf<Int>(0,1,2,3,4,5)

        test.slice(2)
        println(test)

        val arr = arrayListOf(1,2,3,4,5)
        arr.slice(2)
        println(arr)
    }

    private fun <T> MutableList<T>.slice(x: Int) {
       for (i in 0..x){
           this.removeAt(i)
       }
    }

    private fun <T> ArrayList<T>.slice(x: Int) {
        for (i in 0..x){
            this.removeAt(i)
        }
    }
//    fun <T> slize(input: Array<out T>, n: Int): List<T> {
//        var input = input
//        input = input.sliceArray(0..n)
//    }

}
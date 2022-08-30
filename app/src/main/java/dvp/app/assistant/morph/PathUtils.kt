//package dvp.app.assistant.morph
//
//import dvp.app.assistant.morph.Const.paramsParser
//import dvp.app.assistant.morph.models.Command
//import dvp.app.assistant.morph.models.ParamsParser
//
//object PathUtils {
//
//    fun normalizePath(pathData: String): Array<Command> {
//        var assign: Command;
//
////        if (isNormalizedArray(pathInput)) {
////            return clonePath(pathInput);
////        }
//
//        var path = toAbsolute(pathData).toTypedArray();
//        var params = ParamsParser();
//        var ii = path.size;
//        var allPathCommands = arrayListOf<String>()
//
//        var pathCommand = "";
//        var prevCommand = "";
//
//        for (i in 0 until ii) {
//            assign = path[i]
//            pathCommand = assign.name;
//
//            // Save current path command
//            allPathCommands.add(i, pathCommand)
//            // Get previous path command
//            if (i != 0) {
//                prevCommand = allPathCommands[i - 1];
//            }
//            // Previous path command is used to normalizeSegment
//            // @ts-ignore -- expected on normalization
//            path[i] = normalizeSegment(path[i], params, prevCommand);
//
//            var segment = path[i];
//            val seglen = segment.points.size;
//
//            if (pathCommand == "Z") break
//
//            params.x1 = +segment.points[seglen - 2];
//            params.y1 = +segment.points[seglen - 1];
//            params.x2 = segment.points.getOrNull(seglen - 4)?.let { +it } ?: params.x1
//            params.y2 = segment.points.getOrNull(seglen - 3)?.let { +it } ?: params.y1
//        }
//
//        // @ts-ignore -- a `normalArray` is absolutely an `absoluteArray`
//        return path;
//    }
//
//    private fun approximatePolygon(parsed:Array<Command>, maxLength: Int) {
//        var ringPath = splitPath(pathToString(parsed))[0];
//        var curvePath = pathToCurve(ringPath);
//        var pathLength = getPathLength(curvePath);
//        var polygon = [];
//        var numPoints = 3;
//        var point;
//
//        if (maxLength && !Number.isNaN(maxLength) && +maxLength > 0) {
//            numPoints = Math.max(numPoints, Math.ceil(pathLength / maxLength));
//        }
//
//        for (var i = 0; i < numPoints; i += 1) {
//            point = getPointAtLength(curvePath, (pathLength * i) / numPoints);
//            polygon.push([point.x, point.y]);
//        }
//
//        // Make all rings clockwise
//        if (!getDrawDirection(curvePath)) {
//            polygon.reverse();
//        }
//
//        return {
//                polygon: polygon,
//                skipBisect: true,
//        };
//    }
//
//    private fun splitPath(pathInput: ) {
//        console.log("splitPath")
//        return pathToString(pathToAbsolute(pathInput), 0)
//            .replace(/(m|M)/g, '|$1')
//        .split('|')
//            .map(function (s) { return s.trim(); })
//            .filter(function (s) { return s; });
//    }
//
//    private fun exactPolygon(pathArray: Array<Command>): ArrayList<Array<Float>>? {
//        var polygon = arrayListOf<Array<Float>>();
//        var pathlen = pathArray.size;
//        var segment: Command;
//        var pathCommand = "";
//
//        if (pathArray.isEmpty() || pathArray[0].name != "M") {
//            return null;
//        }
//
//        for (i in 0 until pathlen) {
//            segment = pathArray[i]
//            pathCommand = segment.name
//            if ((pathCommand == "M" && i != 0) || pathCommand == "Z") {
//                break
//            } else if ("ML".contains(pathCommand)) {
//                polygon.add(segment.points.toTypedArray())
//            } else {
//                return null
//            }
//        }
//
//        return if (pathlen > 0) polygon else null;
//    }
//
//    private fun normalizeSegment(
//        segment: Command,
//        params: ParamsParser,
//        prevCommand: String
//    ): Command {
//        var pathCommand = segment.name;
//        var px1 = params.x1;
//        var py1 = params.y1;
//        var px2 = params.x2;
//        var py2 = params.y2;
//        var values = segment.points;
//        var result = segment;
//
//        if (!"TQ".contains(pathCommand)) {
//            // optional but good to be cautious
//            params.qx = null;
//            params.qy = null;
//        }
//
//        when (pathCommand) {
//            "H" -> {
//                result = Command("L", segment.points[0], py1)
//            }
//            "V" -> {
//                result = Command("L", px1, segment.points[0])
//            }
//            "S" -> {
//                val (x1, y1) = shorthandToCubic(px1, py1, px2, py2, prevCommand);
//                params.x1 = x1;
//                params.y1 = y1;
//                result = Command("C", x1, y1, *values)
//            }
//            "T" -> {
//                val (qx, qy) = shorthandToQuad(px1, py1, params.qx!!, params.qy!!, prevCommand);
//                params.qx = qx;
//                params.qy = qy;
//                result = Command("Q", qx, qy, *values)
//            }
//            "Q" -> {
//                val nqx = values[0];
//                val nqy = values[1];
//                params.qx = nqx;
//                params.qy = nqy;
//            }
//        }
//
//        return result;
//    }
//
//    private fun shorthandToCubic(
//        x1: Float,
//        y1: Float,
//        x2: Float,
//        y2: Float,
//        prevCommand: String
//    ): Array<Float> {
//        return if ("CS".contains(prevCommand))
//            arrayOf(x1 * 2 - x2, y1 * 2 - y2)
//        else
//            arrayOf(x1, y1)
//    }
//
//    private fun shorthandToQuad(
//        x1: Float,
//        y1: Float,
//        qx: Float,
//        qy: Float,
//        prevCommand: String
//    ): Array<Float> {
//        return if ("QT".contains(prevCommand))
//            arrayOf(x1 * 2 - qx, y1 * 2 - qy)
//        else
//            arrayOf(x1, y1)
//    }
//
//    fun toAbsolute(pathData: String): List<Command> {
//        val path = Utils.parseData(pathData)
//        var x = 0f
//        var y = 0f
//        var mx = 0f
//        var my = 0f
//
//        return path.map { segment ->
//
//            val points = segment.takeLast(segment.lastIndex).map { it.toFloat() }.toList()
//            val pathCommand = segment.first()
//            val absCommand = pathCommand.uppercase()
//
//            if (pathCommand == "M") {
//                x = points[0]
//                y = points[1]
//                mx = x
//                my = y
//                return@map Command(pathCommand, x, y)
//            }
//
//            val absoluteSegment: Command
//
//            if (pathCommand !== absCommand) {
//                when (absCommand) {
//                    "A" -> {
//                        absoluteSegment = Command(
//                            absCommand, points[0], points[1], points[2],
//                            points[3], points[4], points[5] + x, points[6] + y
//                        )
//                    }
//                    "V" -> {
//                        absoluteSegment = Command(absCommand, points[0] + y)
//                    }
//                    "H" -> {
//                        absoluteSegment = Command(absCommand, points[0] + x)
//                    }
//                    else -> {
//                        absoluteSegment = Command(
//                            name = absCommand,
//                            *points.mapIndexed { j, n -> n + if (j % 2 != 0) y else x }
//                                .toFloatArray()
//                        )
//                    }
//                }
//            } else {
//                absoluteSegment = Command(absCommand, *points.toFloatArray())
//            }
//
//            when (absCommand) {
//                "Z" -> {
//                    x = mx
//                    y = my
//                }
//                "H" -> {
//                    x = absoluteSegment.points[0]
//                }
//                "V" -> {
//                    y = absoluteSegment.points[0]
//
//                }
//                else -> {
//                    val pointsSize = absoluteSegment.points.size
//                    x = absoluteSegment.points[pointsSize - 2]
//                    y = absoluteSegment.points[pointsSize - 1]
//                    if (absCommand == "M") {
//                        mx = x
//                        my = y
//                    }
//                }
//            }
//            return@map absoluteSegment
//        }
//    }
//}
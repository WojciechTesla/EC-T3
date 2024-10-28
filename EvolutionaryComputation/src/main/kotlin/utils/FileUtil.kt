package utils

import ExperimentResult
import TSPNode

object FileUtil {

    fun readCSV(csvFile: String): List<TSPNode> {
        val inputStream = this.javaClass.classLoader.getResourceAsStream(csvFile)
        return inputStream
            .bufferedReader()
            .lineSequence()
            .filter { it.isNotBlank() }
            .mapIndexed {
                index, line -> val (x, y, cost) = line.split(";").map { it.toDouble() }
                TSPNode(index, x, y, cost)
        }.toList()
    }

    fun exportResultToCSV(fileName: String, folder: String, result: ExperimentResult) {
        val metricFile = java.io.File("$folder/$fileName.csv")
        metricFile.writeText("Best Objective,Average Objective,Worst Objective,Time Taken\n")
        metricFile.appendText("${result.bestObjective}, ${result.averageObjective}, ${result.worstObjective}, ${result.timeTaken}\n")
        metricFile.appendText("${result.averageObjective} (${result.bestObjective} – ${result.worstObjective}),,,")

        val bestFile = java.io.File("$folder/$fileName" + "_best_nodes.csv")
        bestFile.writeText("Index\n")
        for (index in result.bestNodes) {
            bestFile.appendText("$index\n")
        }

        val nodeFile = java.io.File("$folder/$fileName" + "_nodes.csv")
        nodeFile.writeText("X,Y,Cost\n")
        for (nodes in result.nodeMemory) {
            for (node in nodes) {
                nodeFile.appendText("${node.x}, ${node.y}, ${node.cost}\n")
            }
            nodeFile.appendText("\n")
        }

//        var headerString = ""
//        var index = 0;
//        for (nodes in result.nodeMemory) {
//            headerString += "x$index, y$index, cost$index\n"
//            index++
//        }
    }

    fun exportResultToJson(fileName: String, result: ExperimentResult) {
        val file = java.io.File(fileName)
        file.writeText("{\n")
        file.appendText("  \"bestObjective\": ${result.bestObjective},\n")
        file.appendText("  \"averageObjective\": ${result.averageObjective},\n")
        file.appendText("  \"worstObjective\": ${result.worstObjective},\n")
        file.appendText("  \"timeTaken\": ${result.timeTaken}\n")
        file.appendText("}\n")
    }
}
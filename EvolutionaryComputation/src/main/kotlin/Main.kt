import enums.DistanceType
import enums.HeuristicType
import enums.LocalSearchType
import enums.MoveType
import solvers.GreedySolver
import utils.FileUtil
import utils.LocalExperimentUtil

fun main(args: Array<String>) {
    val nodes = FileUtil.readCSV("TSPB.csv")
    val solver = GreedySolver(nodes, 0.5, DistanceType.COSTEUCLIDEAN)
    println(nodes.size)
//    for (i in 0..nol)
    for (i in nodes.indices) {
        var params = SolverParameters(
            heuristicType = enums.HeuristicType.GREEDY_END,
            k = 2,
            regretWeight = 0.5,
            objectiveFunctionWeight = 0.5,
            isOptimized = true,
            startingIndex = i
        )
//        println(solver.call(params))
    }
    var type = DistanceType.EUCLIDEAN
    val distanceMatrix = Array(nodes.size) { DoubleArray(nodes.size) }
    for (node1 in nodes.indices) {
        for (node2 in nodes.indices) {
            if (node1 == node2) {
                distanceMatrix[node1][node2] = Double.MAX_VALUE
                continue
            }
            distanceMatrix[node1][node2] = nodes[node1].calculateDistanceTo(nodes[node2], type)
        }
    }





    var params = SolverParameters(
        heuristicType = enums.HeuristicType.GREEDY_ANY,
        k = 2,
        regretWeight = 0.5,
        objectiveFunctionWeight = 0.5,
        isOptimized = true,
        startingIndex = 0
    )
    val s = solver.call(params)
    println(s)
    val l = emptyList<TSPNode>().toMutableList()
    for (node in nodes) {
        if (node in s.nodes){
            continue
        }
        else
        {
            l.add(node)
        }

    }
//    println(l.size)
    for (i in 0 until 100){
        for (j in 0 until 100){
//            if (i==1 && j==99){continue}

            val c = Change(Pair(i, j), MoveType.INTER_NODES, distanceMatrix)
            var s2 = c.calculateDelta(s, l)
            if (s2 < 0){
                println(s2)
            }
            var temp2 = s.nodes[i]

            var s22 = c.getNewSolution(s, l)
            l[j] = temp2
        }
    }

    println()
    println()
    LocalExperimentUtil.performExperiment(LocalSearchType.STEEPEST, heuristicType = HeuristicType.GREEDY_END,MoveType.INTER_NODES,"TSPA.csv")
//    val experimentResult = ExperimentUtil.performExperiment(200, solver, enums.HeuristicType.GREEDY_CYCLE, true)
//    println(experimentResult)
//    FileUtil.exportResultToCSV("result_cycle", "ResultsTSPA", experimentResult)

//    FileUtil.exportResultToCSV("result_kregret", "ResultsTSPB", experimentResult)
//    val pairs = listOf(Pair(3.0, "apple"), Pair(-1.1, "orange"), Pair(2.0, "banana"))
//    val sortedByFirst = pairs.sortedBy { it.first }
//    println(sortedByFirst)

}

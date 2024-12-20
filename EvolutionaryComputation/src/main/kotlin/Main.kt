import enums.DistanceType
import enums.HeuristicType
import solvers.GreedySolver
import utils.*

fun main(args: Array<String>) {


//    val h = listOf(HeuristicType.RANDOM,HeuristicType.GREEDY_ANY)
//    var st = listOf("TSPA","TSPB")
//    for (val2 in LocalSearchType.values() )
//    {
//        for (val3 in MoveTypeExp.values())
//        {
//            for (val1 in h)
//            {
//                for (s in st){
//                    print(val1.name)
//                    print(val2.name)
//                    print(val3.name)
//                    print(s)
//                    print("\n")
//                    var results = LocalExperimentUtil.performExperiment(val2,val1,val3,s+".csv")
//                    var totalTime = results.second
//                    var objectives = results.first.map {result -> result.objectiveFunctionValue!!}
//                    var pp = results.first.map {result -> Pair(result.objectiveFunctionValue,result)}
//                    var sorted = pp.sortedBy {it.first}
//                    var nodeMemory: MutableList<Array<TSPNode>> = mutableListOf()
//                    for (res in results.first)
//                    {
//                        nodeMemory.add(res.nodes.toTypedArray())
//                    }
//                    var e = ExperimentResult(sorted[0].second.indices, sorted[0].first!!, objectives.average(),sorted[sorted.size-1].first!! , totalTime, nodeMemory)
//                    FileUtil.exportResultToCSV(val2.name + "_" + val3.name + "_" + val1.name,"Wojciech_Results"+ s,e)
//                }
//            }
//        }
//    }
//    val N = 20
//    val results = CandidateMovesUtil.candidateMoves("TSPB.csv", N)
//    var totalTime = results.second
//    val objectives = results.first.map { result -> result.objectiveFunctionValue!! }
//    var pp = results.first.map { result -> Pair(result.objectiveFunctionValue, result) }
//    var sorted = pp.sortedBy { it.first }
//    var nodeMemory: MutableList<Array<TSPNode>> = mutableListOf()
//    for (res in results.first) {
//        nodeMemory.add(res.nodes.toTypedArray())
//    }
//    var e = ExperimentResult(
//        sorted[0].second.indices,
//        sorted[0].first!!,
//        objectives.average(),
//        sorted[sorted.size - 1].first!!,
//        totalTime,
//        nodeMemory
//    )
//    FileUtil.exportResultToCSV("Candidate_Moves_" + N, "Candidate_Moves_TSPB", e)
//    println(results.second)



//    val results = MultipleLocalSearch.steepestSearch("TSPB.csv")
//    val results = IteratedLocalSearch.steepestSearch("TSPA.csv")
//    var totalTime = results.second
//    val objectives = results.first.map { result -> result.objectiveFunctionValue!! }
//    var pp = results.first.map { result -> Pair(result.objectiveFunctionValue, result) }
//    var sorted = pp.sortedBy { it.first }
//    var nodeMemory: MutableList<Array<TSPNode>> = mutableListOf()
//    for (res in results.first) {
//        nodeMemory.add(res.nodes.toTypedArray())
//    }
//    var e = ExperimentResult(
//        sorted[0].second.indices,
//        sorted[0].first!!,
//        objectives.average(),
//        sorted[sorted.size - 1].first!!,
//        totalTime,
//        nodeMemory
//    )
//    FileUtil.exportResultToCSV("Iterated_5" , "Iterated_TSPA", e)
//    println(results.second)


        val fileString = "TSPA.csv"
        val runner = LocalSearchRunner(fileString)
        val nodes = FileUtil.readCSV(fileString)
        val params = SolverParameters(
            heuristicType = HeuristicType.RANDOM,
            k = 2,
            regretWeight = 0.5,
            objectiveFunctionWeight = 0.5,
            isOptimized = true,
            startingIndex = 0
        )
        val solver = GreedySolver(nodes, 0.5, DistanceType.COSTEUCLIDEAN)

        val initialSolution = solver.call(params) // Initialize your solution here
        val finalSolution = runner.runSteepestSearch(initialSolution)

        println("Final Solution: $finalSolution")



}

import enums.DistanceType
import solvers.GreedyKRegretSolver
import solvers.GreedySolver
import utils.ExperimentUtil
import utils.FileUtil

fun main(args: Array<String>) {
    val nodes = FileUtil.readCSV("TSPB.csv")
//    val solver = GreedySolver(nodes, 0.5, DistanceType.COSTEUCLIDEAN)

//    val experimentResult = ExperimentUtil.performExperiment(200, solver, enums.HeuristicType.GREEDY_CYCLE, true)
//    println(experimentResult)
//    FileUtil.exportResultToCSV("result_cycle", "ResultsTSPA", experimentResult)

    val solverKRegret = GreedyKRegretSolver(nodes, 0.5, DistanceType.COSTEUCLIDEAN)
    val params = SolverParameters(
        heuristicType = enums.HeuristicType.GREEDY_KREGRET,
        k = 2,
        regretWeight = 0.5,
        objectiveFunctionWeight = 0.5,
        isOptimized = true)
    val experimentResult = ExperimentUtil.performExperiment(200, solverKRegret, params)
    println(experimentResult)
    FileUtil.exportResultToCSV("result_kregret", "ResultsTSPB", experimentResult)

}
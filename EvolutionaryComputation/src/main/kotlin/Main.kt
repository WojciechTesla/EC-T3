import utils.ExperimentUtil
import utils.FileUtil

fun main(args: Array<String>) {
    println("Hello World!")

    // Try adding program arguments via Run/Debug configuration.
    // Learn more about running applications: https://www.jetbrains.com/help/idea/running-applications.html.
    println("Program arguments: ${args.joinToString()}")

    val nodes = FileUtil.readCSV("TSPA.csv")
    val solver = GreedySolver(nodes, 0.5, DistanceType.COSTEUCLIDEAN)

    val experimentResult = ExperimentUtil.performExperiment(200, solver, HeuristicType.GREEDY_CYCLE, true)
    println(experimentResult)
    FileUtil.exportResultToCSV("result_cycle", "ResultsTSPA", experimentResult)
}
data class ExperimentResult(val bestNodes: List<Int>, val bestObjective : Double, val averageObjective: Double, val worstObjective: Double, val timeTaken: Double, val nodeMemory: List<Array<TSPNode>>) {
    override fun toString(): String {
        return "ExperimentResult(bestObjective=$bestObjective, averageObjective=$averageObjective, worstObjective=$worstObjective, timeTaken=$timeTaken, bestNodesIndices=${bestNodes})"
    }
}

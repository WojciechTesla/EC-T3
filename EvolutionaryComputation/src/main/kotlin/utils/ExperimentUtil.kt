package utils

import ExperimentResult
import SolverParameters
import solvers.GreedySolver
import enums.HeuristicType
import TSPNode
import solvers.Solver

object ExperimentUtil {
    fun performExperiment(times: Int, solver: Solver, params: SolverParameters): ExperimentResult {
        val results: MutableList<Double> = mutableListOf()
        val nodeMemory: MutableList<Array<TSPNode>> = mutableListOf()
        val start = System.nanoTime()
        var bestSolution: Double = Double.MAX_VALUE
        var bestIndices: List<Int> = emptyList()
        for (i in 0 until times) {
            val solution = solver.call(params)
            results.add(solution.objectiveFunctionValue!!)
            nodeMemory.add(solution.nodes.toTypedArray())
            if (solution.objectiveFunctionValue!! < bestSolution) {
                bestSolution = solution.objectiveFunctionValue!!
                bestIndices = solution.indices
            }
        }
        val end = System.nanoTime()
        return ExperimentResult(bestIndices, results.minOrNull()!!, results.average(), results.maxOrNull()!!, (end - start) / 1_000_000_000.0, nodeMemory)
    }
}
package solvers

import SolverParameters
import enums.DistanceType
import TSPNode
import TSPSolution
import enums.HeuristicType
import managers.CostManager
import kotlin.math.round

class GreedyKRegretSolver(private val nodes: List<TSPNode>, private val percentage: Double, private val type: DistanceType) : Solver{
    private val solutionSize = round((nodes.size * percentage)).toInt()
    private var distanceMatrix: Array<DoubleArray>? = null
    private var insertCostMatrix: Array<Array<DoubleArray>>? = null
    val overheadTime: MutableMap<String, Double> = mutableMapOf()

    private fun generateDistanceMatrix() {
        val start = System.nanoTime()
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
        this.distanceMatrix = distanceMatrix
        val end = System.nanoTime()
        overheadTime["generateDistanceMatrix"] = (end - start) / 1_000_000_000.0
    }

    private fun generateInsertCostMatrix() {
        val start = System.nanoTime()
        if (distanceMatrix == null) {
            generateDistanceMatrix()
        }

        val insertCostMatrix = Array(nodes.size) { Array(nodes.size) { DoubleArray(nodes.size) } }

        for (firstNode in nodes.indices) {
            for (secondNode in nodes.indices) {
                if (firstNode == secondNode) {
                    Double.MAX_VALUE
                    continue
                }
                for (insertNode in nodes.indices) {
                    if (insertNode == firstNode || insertNode == secondNode) {
                        Double.MAX_VALUE
                        continue
                    }
                    insertCostMatrix[firstNode][secondNode][insertNode] = distanceMatrix!![firstNode][insertNode] + distanceMatrix!![insertNode][secondNode] - distanceMatrix!![firstNode][secondNode]
                }
            }
        }
        this.insertCostMatrix = insertCostMatrix
        val end = System.nanoTime()
        overheadTime["generateInsertCostMatrix"] = (end - start) / 1_000_000_000.0
    }

    private fun selectBestNodeToCycle(availableIndicesCopy: MutableList<Int>, usedIndicesCopy: MutableList<Int>) : Pair<Int, Int> {
        var minCost = Double.MAX_VALUE
        var minInsertIndex: Int? = null
        var minStartIndex: Int? = null
        for (i in 0 until usedIndicesCopy.size) {
            val start = usedIndicesCopy[i]
            val end = usedIndicesCopy[(i + 1) % usedIndicesCopy.size]
            for (insert in availableIndicesCopy) {
                val insertCost = insertCostMatrix!![start][end][insert]
                if (insertCost < minCost) {
                    minCost = insertCost
                    minInsertIndex = insert
                    minStartIndex = start
//                            println("Setting indices: $minInsertIndex, $minStartIndex")
                }
            }
        }
        if (minInsertIndex != null && minStartIndex != null) {
            val insertAt = usedIndicesCopy.indexOf(minStartIndex) + 1
//                    println("Min insert index: $minInsertIndex, Min start index: $minStartIndex, ${insertAt}")
            availableIndicesCopy.remove(minInsertIndex)
            usedIndicesCopy.add(insertAt, minInsertIndex)
            return Pair(insertAt, minInsertIndex)
//            greedyNodes.add(insertAt, nodes.elementAt(minInsertIndex))
//                    println("Added node $minInsertIndex at index ${usedIndices.indexOf(minStartIndex) + 1}")
//                    println("Used indices: $usedIndices")
        }
        return Pair(-1, -1)
    }

    override fun call(params: SolverParameters): TSPSolution {
        return when (params.heuristicType) {
            HeuristicType.GREEDY_KREGRET -> generateGreedy2RegretSolution(params)
            HeuristicType.GREEDY_WEIGHTED_KREGRET -> generateGreedy2WeightedRegretSolution(params)
            else -> throw IllegalArgumentException("Heuristic type for this solver not supported")
        }
    }

    private fun generateGreedyRegretSolutionImplementation(regretWeight: Double?, objectiveValueWeight: Double?, costManger: CostManager, optimized: Boolean) : TSPSolution{
        if (regretWeight == null || objectiveValueWeight == null) {
            throw IllegalArgumentException("Regret weight or objective value weight not provided")
        }
        if (insertCostMatrix == null) {
            generateInsertCostMatrix()
        }

        val greedyNodes = mutableListOf<TSPNode>()
        val availableIndices = nodes.indices.toMutableList()
        var index = (Math.random()*nodes.size).toInt()
        val usedIndices = mutableListOf<Int>()

        greedyNodes.add(nodes.elementAt(index))
        availableIndices.remove(index)
        usedIndices.add(index)

        val distances = availableIndices.map { distanceMatrix!![index][it] }
        val minDistanceIndex = distances.indexOf(distances.minOrNull())
        index = availableIndices[minDistanceIndex]
        greedyNodes.add(nodes.elementAt(index))
        availableIndices.remove(index)
        usedIndices.add(index)

        if (optimized){
            while (greedyNodes.size < solutionSize) {
                var minTotalCost = Double.MAX_VALUE
                var minInsertIndex: Int? = null
                var minStartIndex: Int? = null
                for (insert in availableIndices) {
                    val insertCosts = mutableListOf<Pair<Int, Double>>();
                    for (i in 0 until usedIndices.size) {
                        val start = usedIndices[i]
                        val end = usedIndices[(i + 1) % usedIndices.size]
                        insertCosts.add(Pair(start, insertCostMatrix!![start][end][insert]))
                    }
                    insertCosts.sortBy { it.second }
                    val totalCost = costManger.getCost(insertCosts)
                    if (totalCost < minTotalCost) {
                        minTotalCost = totalCost
                        minInsertIndex = insert
                        minStartIndex = insertCosts[0].first
                    }
                }
                if (minInsertIndex != null && minStartIndex != null) {
                    val insertAt = usedIndices.indexOf(minStartIndex) + 1
                    availableIndices.remove(minInsertIndex)
                    usedIndices.add(insertAt, minInsertIndex)
                    greedyNodes.add(insertAt, nodes.elementAt(minInsertIndex))
                }
            }
        } else {
            TODO();
        }

        return TSPSolution(greedyNodes, type, usedIndices)
    }

    private fun generateGreedy2WeightedRegretSolution(params: SolverParameters) : TSPSolution {
        val costManager = CostManager(params)
        return generateGreedyRegretSolutionImplementation(params.regretWeight, params.objectiveFunctionWeight, costManager, params.isOptimized)
    }

    private fun generateGreedy2RegretSolution(params: SolverParameters) : TSPSolution {
        val costManager = CostManager(params)
        return generateGreedyRegretSolutionImplementation(params.regretWeight, params.objectiveFunctionWeight, costManager, params.isOptimized)
    }

}
package solvers

import SolverParameters
import enums.DistanceType
import enums.HeuristicType
import TSPNode
import TSPSolution
import kotlin.math.round

class GreedySolver (private val nodes: List<TSPNode>, private val percentage: Double, private val type: DistanceType) : Solver {
    private val solutionSize = round((nodes.size * percentage)).toInt()
    var distanceMatrix: Array<DoubleArray>? = null
    var insertCostMatrix: Array<Array<DoubleArray>>? = null
    val overheadTime: MutableMap<String, Double> = mutableMapOf()

    override fun call(params: SolverParameters) : TSPSolution {
        return when (params.heuristicType) {
            HeuristicType.RANDOM -> generateRandomSolution()
            HeuristicType.GREEDY_END -> generateGreedyEndPositionSolution(params.startingIndex)
            HeuristicType.GREEDY_ANY -> generateGreedyBestPositionSolution(params.startingIndex, params.isOptimized)
            HeuristicType.GREEDY_CYCLE -> generateGreedyBestCycleSolution(params.startingIndex, params.isOptimized)
            else -> throw IllegalArgumentException("Heuristic type for this solver not supported")
        }
    }

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

    fun generateRandomSolution(): TSPSolution {
        val noOfNodes = round((nodes.size * percentage)).toInt()
        val shuffledIndices = nodes.indices.shuffled()
        val usedIndices = shuffledIndices.subList(0, noOfNodes)
        val shuffledNodes = usedIndices.map { nodes[it] }
        return TSPSolution(shuffledNodes, type, usedIndices)
    }

    fun generateGreedyEndPositionSolution(startingIndex: Int): TSPSolution {
        if (distanceMatrix == null) {
            generateDistanceMatrix()
        }

        val greedyNodes = mutableListOf<TSPNode>()
        val availableIndices = nodes.indices.toMutableList()
        val usedIndices = mutableListOf<Int>()
        //I'm sorry little one
        //startingIndex is now in SolverParameters
        var index = startingIndex



        greedyNodes.add(nodes.elementAt(index))
        availableIndices.remove(index)
        usedIndices.add(index)

        while (greedyNodes.size < solutionSize) {
            val distances = availableIndices.map { distanceMatrix!![index][it] }
            val minDistanceIndex = distances.indexOf(distances.minOrNull())

            index = availableIndices[minDistanceIndex]
            greedyNodes.add(nodes.elementAt(index))
            availableIndices.remove(index)
            usedIndices.add(index)
        }

        return TSPSolution(greedyNodes, type, usedIndices)
    }

    fun generateGreedyBestPositionSolution(startingIndex: Int, optimized: Boolean): TSPSolution {
        if (insertCostMatrix == null) {
            generateInsertCostMatrix()
        }

        val greedyNodes = mutableListOf<TSPNode>()
        val availableIndices = nodes.indices.toMutableList()
        // same here
//        var index = (Math.random()*nodes.size).toInt()
        var index = startingIndex
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
                var minCost = Double.MAX_VALUE
                var minInsertIndex: Int? = null
                var minStartIndex: Int? = null
                var beforeLoop: Boolean = false
                for (i in -1 until usedIndices.size) {
                    var start: Int
                    var end: Int
                    for (insert in availableIndices) {
                        val insertCost: Double
                        if (i == usedIndices.size - 1) {
                            end = usedIndices[i]
                            start = usedIndices[i]
                            insertCost = distanceMatrix!![end][insert]
                            beforeLoop = false
                        } else if (i == -1) {
                            start = usedIndices[0]
                            insertCost = distanceMatrix!![insert][start]
                            beforeLoop = true
                        } else {
                            end = usedIndices[i + 1]
                            start = usedIndices[i]
                            insertCost = insertCostMatrix!![start][end][insert]
                            beforeLoop = false
                        }
                        if (insertCost < minCost) {
                            minCost = insertCost
                            minInsertIndex = insert
                            if (beforeLoop) {
                                minStartIndex = 0
                            } else {
                                minStartIndex = start
                            }
                        }
                    }
                }
                if (minInsertIndex != null && minStartIndex != null) {
                    val insertAt: Int
                    if (beforeLoop){
                        insertAt = 0
                    } else {
                        insertAt = usedIndices.indexOf(minStartIndex) + 1
                    }
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

    fun generateGreedyBestCycleSolution(startingIndex: Int, optimized: Boolean): TSPSolution {
        if (insertCostMatrix == null) {
            generateInsertCostMatrix()
        }

        val greedyNodes = mutableListOf<TSPNode>()
        val availableIndices = nodes.indices.toMutableList()
        //same here
//        var index = (Math.random()*nodes.size).toInt()
        var index = startingIndex
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
                var minCost = Double.MAX_VALUE
                var minInsertIndex: Int? = null
                var minStartIndex: Int? = null
                for (i in 0 until usedIndices.size) {
                    val start = usedIndices[i]
                    val end = usedIndices[(i + 1) % usedIndices.size]
                    for (insert in availableIndices) {
                        val insertCost = insertCostMatrix!![start][end][insert]
                        if (insertCost < minCost) {
                            minCost = insertCost
                            minInsertIndex = insert
                            minStartIndex = start
                        }
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
}
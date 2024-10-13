class GreedySolver (private val nodes: List<TSPNode>, private val percentage: Double, private val type: DistanceType) {
    private val solutionSize = (nodes.size * percentage).toInt()
    var distanceMatrix: Array<DoubleArray>? = null
    var insertCostMatrix: Array<Array<DoubleArray>>? = null
    val overheadTime: MutableMap<String, Double> = mutableMapOf()

    fun call(type: HeuristicType, optimized: Boolean) : Solution{
        return when (type) {
            HeuristicType.RANDOM -> generateRandomSolution()
            HeuristicType.GREEDY_END -> generateGreedyEndPositionSolution()
            HeuristicType.GREEDY_ANY -> generateGreedyBestPositionSolution(optimized)
            HeuristicType.GREEDY_CYCLE -> generateGreedyBestCycleSolution(optimized)
        }
    }

    private fun generateDistanceMatrix() {
        val start = System.nanoTime()
        val distanceMatrix = Array(nodes.size) { DoubleArray(nodes.size) }
        for (i in nodes.indices) {
            for (j in nodes.indices) {
                if (i == j) {
                    distanceMatrix[i][j] = Double.MAX_VALUE
                    continue
                }
                distanceMatrix[i][j] = nodes[i].calculateDistanceTo(nodes[j], type)
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

    fun generateRandomSolution(): Solution {
        val noOfNodes = (nodes.size * percentage).toInt()
        val shuffledIndices = nodes.indices.shuffled()
        val usedIndices = shuffledIndices.subList(0, noOfNodes)
        val shuffledNodes = usedIndices.map { nodes[it] }
        return Solution(shuffledNodes, type, usedIndices)
    }

    fun generateGreedyEndPositionSolution(): Solution {
        if (distanceMatrix == null) {
            generateDistanceMatrix()
        }

        val greedyNodes = mutableListOf<TSPNode>()
        val availableIndices = nodes.indices.toMutableList()
        val usedIndices = mutableListOf<Int>()
        var index = (Math.random()*nodes.size).toInt()

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

        return Solution(greedyNodes, type, usedIndices)
    }

    fun generateGreedyBestPositionSolution(optimized: Boolean): Solution {
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

        // add 2nd node
        val distances = availableIndices.map { distanceMatrix!![index][it] }
        val minDistanceIndex = distances.indexOf(distances.minOrNull())
        index = availableIndices[minDistanceIndex]
        greedyNodes.add(nodes.elementAt(index))
        availableIndices.remove(index)
        usedIndices.add(index)

        if (optimized){
            while (greedyNodes.size < solutionSize) {
//                println("Used indices: $usedIndices")
                var minCost = Double.MAX_VALUE
                var minInsertIndex: Int? = null
                var minStartIndex: Int? = null
                var beforeLoop: Boolean = false
                for (i in -1 until usedIndices.size) {
//                    println("Checking index: $i")
                    var start: Int
                    var end: Int
//                    println("Checking after: $start")
                    for (insert in availableIndices) {
                        val insertCost: Double
//                        println("Checking insert $insert, with index $i")
                        if (i == usedIndices.size - 1) {
                            end = usedIndices[i]
                            start = usedIndices[i]
                            insertCost = distanceMatrix!![end][insert]
                            beforeLoop = false
                        } else if (i == -1) {
//                            println("2nd condition")
                            start = usedIndices[0]
                            insertCost = distanceMatrix!![insert][start]
                            beforeLoop = true
                        } else {
//                            println("3rd condition")
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
//                            println("Setting indices: $minInsertIndex, $minStartIndex")
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
//                    println("Min insert index: $minInsertIndex, Min start index: $minStartIndex, ${insertAt}")
                    availableIndices.remove(minInsertIndex)
                    usedIndices.add(insertAt, minInsertIndex)
                    greedyNodes.add(insertAt, nodes.elementAt(minInsertIndex))
//                    println("Added node $minInsertIndex at index ${usedIndices.indexOf(minStartIndex) + 1}")
//                    println("Used indices: $usedIndices")
                }
            }
        } else {
            TODO();
        }
        return Solution(greedyNodes, type, usedIndices)
    }

    fun generateGreedyBestCycleSolution(optimized: Boolean): Solution{
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

        // add 2nd node
        val distances = availableIndices.map { distanceMatrix!![index][it] }
        val minDistanceIndex = distances.indexOf(distances.minOrNull())
        index = availableIndices[minDistanceIndex]
        greedyNodes.add(nodes.elementAt(index))
        availableIndices.remove(index)
        usedIndices.add(index)

        if (optimized){
            while (greedyNodes.size < solutionSize) {
//                println("Used indices: $usedIndices")
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
//                            println("Setting indices: $minInsertIndex, $minStartIndex")
                        }
                    }
                }
                if (minInsertIndex != null && minStartIndex != null) {
                    val insertAt = usedIndices.indexOf(minStartIndex) + 1
//                    println("Min insert index: $minInsertIndex, Min start index: $minStartIndex, ${insertAt}")
                    availableIndices.remove(minInsertIndex)
                    usedIndices.add(insertAt, minInsertIndex)
                    greedyNodes.add(insertAt, nodes.elementAt(minInsertIndex))
//                    println("Added node $minInsertIndex at index ${usedIndices.indexOf(minStartIndex) + 1}")
//                    println("Used indices: $usedIndices")
                }
            }
        } else {
            TODO();
        }
//        for (i in usedIndices){
//            println(i)
//        }

        return Solution(greedyNodes, type, usedIndices)
    }
}
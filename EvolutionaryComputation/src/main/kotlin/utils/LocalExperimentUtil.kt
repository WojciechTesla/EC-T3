package utils

import Change
import SolverParameters
import TSPNode
import TSPSolution
import enums.DistanceType
import enums.HeuristicType
import enums.LocalSearchType
import enums.MoveType
import solvers.GreedySolver
import java.io.Serializable

object LocalExperimentUtil {

    fun performExperiment(
        searchType: LocalSearchType,
        heuristicType: HeuristicType,
        moveType: MoveType,
        fileString: String
    ): Serializable {
        return when (searchType) {  // Access 'type' directly as it's a property now
            LocalSearchType.GREEDY -> greedySearch(heuristicType, moveType, fileString)
            LocalSearchType.STEEPEST -> steepestSearch(heuristicType, moveType, fileString)
            else -> throw IllegalArgumentException("Unsupported search")
        }
    }

    private fun greedySearch(
        heuristicType: HeuristicType,
        moveType: MoveType,
        fileString: String
    ): Pair<MutableList<TSPSolution>, Double> {
        val distanceMatrix = this.getDistanceMatrix(fileString)
        val nodes = FileUtil.readCSV(fileString)
        val solver = GreedySolver(nodes, 0.5, DistanceType.COSTEUCLIDEAN)
        var totalT = 0.0
        val moves = this.getMoves(moveType)

        val solutions = emptyList<TSPSolution>().toMutableList()



        for (i in nodes.indices) {
            val params = SolverParameters(
                heuristicType = heuristicType,
                k = 2,
                regretWeight = 0.5,
                objectiveFunctionWeight = 0.5,
                isOptimized = true,
                startingIndex = i
            )

            var solution = solver.call(params)
            val l = emptyList<TSPNode>().toMutableList()
            for (node in nodes) {
                if (node in solution.nodes) {
                    continue
                } else {
                    l.add(node)
                }

            }
            var flag: Boolean = true



            println(solution.objectiveFunctionValue)

            //TODO start time here

            val start = System.nanoTime()

            while (flag) {
                var shuffledMoves = moves.shuffled()
                flag = false
                for (move in shuffledMoves) {

                    var c = Change(move, moveType, distanceMatrix)
                    var d = c.calculateDelta(solution, l)
                    if (d < 0) {
                        flag = true
                        if (moveType == MoveType.INTER_NODES) {
                            var temp = solution.nodes[move.first]
                            solution = c.getNewSolution(solution, l)
                            l[move.second] = temp
                        } else {
                            solution = c.getNewSolution(solution, l)


                        }


                    }
                }

            }

            val end = System.nanoTime()
            val t = (end - start)
            totalT += t
            solutions.add(solution)


            //TODO Save solution and time

            println(solution.objectiveFunctionValue)
        }
        println(totalT / 1_000_000_000.0)
        var results = Pair(solutions, totalT)
        return results
    }

    private fun steepestSearch(
        heuristicType: HeuristicType,
        moveType: MoveType,
        fileString: String
    ): Pair<MutableList<TSPSolution>, Double> {
        val distanceMatrix = this.getDistanceMatrix(fileString)
        val nodes = FileUtil.readCSV(fileString)
        val solver = GreedySolver(nodes, 0.5, DistanceType.COSTEUCLIDEAN)
        var totalT = 0.0
        val moves = this.getMoves(moveType)

        val solutions = emptyList<TSPSolution>().toMutableList()



        for (i in nodes.indices) {
            val params = SolverParameters(
                heuristicType = heuristicType,
                k = 2,
                regretWeight = 0.5,
                objectiveFunctionWeight = 0.5,
                isOptimized = true,
                startingIndex = i
            )

            var solution = solver.call(params)
            val l = emptyList<TSPNode>().toMutableList()
            for (node in nodes) {
                if (node in solution.nodes) {
                    continue
                } else {
                    l.add(node)
                }

            }
            var flag: Boolean = true



            println(solution.objectiveFunctionValue)

            //TODO start time here

            val start = System.nanoTime()
            var changes = moves.map { move -> Change(move, moveType, distanceMatrix) }


            while (flag) {
                flag = false
                var deltas = changes.map { change -> Pair(change.calculateDelta(solution, l), change) }
                var sorted = deltas.sortedBy { it.first }
                if (sorted[0].first < 0.0) {
                    flag = true

                    if (moveType == MoveType.INTER_NODES) {

                        var temp = solution.nodes[sorted[0].second.move.first]
                        solution = sorted[0].second.getNewSolution(solution, l)
                        l[sorted[0].second.move.second] = temp
                    } else {
                        solution = sorted[0].second.getNewSolution(solution, l)


                    }

                }

            }

            val end = System.nanoTime()
            val t = (end - start)
            totalT += t
            solutions.add(solution)


            //TODO Save solution and time

            println(solution.objectiveFunctionValue)
        }
        println(totalT / 1_000_000_000.0)
        var results = Pair(solutions, totalT)
        return results
    }

    private fun getDistanceMatrix(fileString: String): Array<DoubleArray> {
        val nodes = FileUtil.readCSV(fileString)
        val type = DistanceType.EUCLIDEAN
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
        return distanceMatrix

    }

    private fun getMoves(type: MoveType): List<Pair<Int, Int>> {
        return when (type) {  // Access 'type' directly as it's a property now
            MoveType.INTRA_NODES -> getIntraNodesMoves()
            MoveType.INTRA_EDGES -> getIntraEdgesMoves()
            MoveType.INTER_NODES -> getInterNodesMoves()
            else -> throw IllegalArgumentException("Unsupported move type")
        }
    }

    private fun getInterNodesMoves(): List<Pair<Int, Int>> {
        val l = emptyList<Pair<Int, Int>>().toMutableList()
        for (i in 0 until 100) {
            for (j in 0 until 100) {
                l.add(Pair(i, j))
            }
        }
        return l.toList()
    }


    private fun getIntraEdgesMoves(): List<Pair<Int, Int>> {
        val l = emptyList<Pair<Int, Int>>().toMutableList()
        for (i in 0 until 100) {
            for (j in i + 2 until 100) {
                if (i == 0 && j == 99) {
                    continue
                }
                l.add(Pair(i, j))
            }
        }
        return l.toList()
    }

    private fun getIntraNodesMoves(): List<Pair<Int, Int>> {
        val l = emptyList<Pair<Int, Int>>().toMutableList()
        for (i in 0 until 100) {
            for (j in i + 1 until 100) {
                l.add(Pair(i, j))
            }
        }
        return l.toList()
    }

}
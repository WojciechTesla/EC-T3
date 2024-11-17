package utils

import Change
import SolverParameters
import TSPNode
import TSPSolution
import enums.*
import solvers.GreedySolver

object CandidateMovesUtil {

    fun candidateMoves(fileString: String,NIGGER: Int): Pair<List<TSPSolution>, Double> {

        val distanceMatrix = this.getDistanceMatrix(fileString)
        val costMatrix = this.getCostMatrix(fileString)
        val nodes = FileUtil.readCSV(fileString)
        val solver = GreedySolver(nodes, 0.5, DistanceType.COSTEUCLIDEAN)
        var totalT = 0.0
        val solutions = emptyList<TSPSolution>().toMutableList()

        for (i in nodes.indices) {
            val params = SolverParameters(
                heuristicType = HeuristicType.RANDOM,
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


            val start = System.nanoTime()

            while (flag) {
                flag = false
                var allMoves = emptyList<Pair<Pair<Int, Int>, MoveType>>().toMutableList()
                (0..99).toList().map{this.candidateMovesCalculation(it,solution,l,costMatrix,NIGGER,allMoves)}
//                for (solution_n in 0 until 100) {
//                    val node = solution.nodes[solution_n]
//                    val ids = (0..199).toList()
//                    val solution_ids = solution.nodes.map { it.id }
//                    val distances = ids.map { Pair(it, costMatrix[node.id][it]) }
//                    val sorted = distances.sortedBy { it.second }
//                    var candidateNodes = sorted.take(NIGGER).map { it.first }
//
//                    for (node2 in candidateNodes) {
//                        val max = solution.nodes.size - 1
//                        val min = 0
//
//                        val n1 = if (solution_n - 1 < min) max else solution_n - 1
//                        val n2 = if (solution_n + 1 > max) min else solution_n + 1
//                        //Candidate edge already in solution
//                        if (solution.nodes[n1].id == node2 || solution.nodes[n2].id == node2 || node.id == node2) {
//                            continue;
//                        }
//                        //Candidate node in solution
//                        if (solution_ids.contains(node2)) {
//                            val solution_n2 = solution_ids.indexOf(node2)
//                            val l = if (solution_n < solution_n2) solution_n else solution_n2
//                            val h = if (solution_n < solution_n2) solution_n2 else solution_n
//
//                            val move1 = Pair(Pair(l, h - 1), MoveType.INTRA_EDGES)
//                            val move2 = Pair(Pair(l + 1, h), MoveType.INTRA_EDGES)
//                            allMoves.add(move1)
//                            allMoves.add(move2)
//
//                        } else {
//                            val l_ids = l.map { it.id }
//                            val solution_n2 = l_ids.indexOf(node2)
//                            val move1 = Pair(Pair(n1, solution_n2), MoveType.INTER_NODES)
//                            val move2 = Pair(Pair(n1, solution_n2), MoveType.INTER_NODES)
//                            allMoves.add(move1)
//                            allMoves.add(move2)
//                        }
//
//
//                    }
//
//                }
                //end of candidate moves

//                println(allMoves.size)
                var changes = allMoves.map { move -> Change(move.first, move.second, distanceMatrix) }
                var deltas = changes.map { change -> Pair(change.calculateDelta(solution, l), change) }
                var sorted = deltas.sortedBy { it.first }
                if (sorted[0].first < 0.0) {
                    flag = true

                    if (sorted[0].second.type == MoveType.INTER_NODES) {

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

        }

        var results = Pair(solutions.toList(), totalT / 1_000_000_000.0)
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

    private fun getCostMatrix(fileString: String): Array<DoubleArray> {
        val nodes = FileUtil.readCSV(fileString)
        val type = DistanceType.COSTEUCLIDEAN
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


    private fun candidateMovesCalculation(solution_n: Int, solution:TSPSolution, l:MutableList<TSPNode>, costMatrix:Array<DoubleArray>, NIGGER:Int, allMoves: MutableList<Pair<Pair<Int,Int>,MoveType>> ){

            val node = solution.nodes[solution_n]
            val ids = (0..199).toList()
            val solution_ids = solution.nodes.map { it.id }
            val distances = ids.map { Pair(it, costMatrix[node.id][it]) }
            val sorted = distances.sortedBy { it.second }
            var candidateNodes = sorted.take(NIGGER).map { it.first }

            val list = listOf<Pair<Pair<Int,Int>,MoveType>>()

            for (node2 in candidateNodes) {
                val max = solution.nodes.size - 1
                val min = 0

                val n1 = if (solution_n - 1 < min) max else solution_n - 1
                val n2 = if (solution_n + 1 > max) min else solution_n + 1
                //Candidate edge already in solution
                if (solution.nodes[n1].id == node2 || solution.nodes[n2].id == node2 || node.id == node2) {
                    continue;
                }
                //Candidate node in solution
                if (solution_ids.contains(node2)) {
                    val solution_n2 = solution_ids.indexOf(node2)
                    val l = if (solution_n < solution_n2) solution_n else solution_n2
                    val h = if (solution_n < solution_n2) solution_n2 else solution_n

                    val move1 = Pair(Pair(l, h - 1), MoveType.INTRA_EDGES)
                    val move2 = Pair(Pair(l + 1, h), MoveType.INTRA_EDGES)

                    allMoves.add(move1)
                    allMoves.add(move2)

                } else {
                    val l_ids = l.map { it.id }
                    val solution_n2 = l_ids.indexOf(node2)
                    val move1 = Pair(Pair(n1, solution_n2), MoveType.INTER_NODES)
                    val move2 = Pair(Pair(n1, solution_n2), MoveType.INTER_NODES)
                    allMoves.add(move1)
                    allMoves.add(move2)
                }


            }
    }



}
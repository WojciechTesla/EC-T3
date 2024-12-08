package utils

import Change
import SolverParameters
import TSPNode
import TSPSolution
import enums.DistanceType
import enums.HeuristicType
import enums.MoveType
import solvers.GreedySolver

class LocalSearchRunner(fileString: String) {
    private val distanceMatrix: Array<DoubleArray>
    private val nodes: List<TSPNode>
    private val allMoves: List<Pair<Pair<Int, Int>, MoveType>>

    init {
        // Precompute distance matrix
        this.nodes = FileUtil.readCSV(fileString)
        this.distanceMatrix = getDistanceMatrix()

        // Precompute all moves
        this.allMoves = getAllMoves()
    }

    /**
     * Runs the steepest search algorithm given an initial solution.
     * @param solution Initial solution.
     * @return Final optimized solution after applying steepest search.
     */
    fun runSteepestSearch(solution: TSPSolution): TSPSolution {
        val l = computeRestOfNodes(solution)
        var flag = true

        // Prepare all changes for the moves
        val changes = allMoves.map { move -> Change(move.first, move.second, distanceMatrix) }
        var currentSolution = solution

        while (flag) {
            flag = false

            // Calculate deltas and sort to find steepest improvement
            val deltas = changes.map { change ->
                Pair(change.calculateDelta(currentSolution, l), change)
            }.sortedBy { it.first }

            // If the best delta is negative, perform the move
            if (deltas.isNotEmpty() && deltas[0].first < 0.0) {
                flag = true
                val bestChange = deltas[0].second

                currentSolution = if (bestChange.type == MoveType.INTER_NODES) {
                    // Handle INTER_NODES move
                    val temp = currentSolution.nodes[bestChange.move.first]
                    val newSolution = bestChange.getNewSolution(currentSolution, l)
                    l[bestChange.move.second] = temp
                    newSolution
                } else {
                    // Handle other moves
                    bestChange.getNewSolution(currentSolution, l)
                }
            }
        }

        return currentSolution
    }

    /**
     * Computes the nodes not present in the current solution.
     */
    private fun computeRestOfNodes(solution: TSPSolution): MutableList<TSPNode> {
        val l = mutableListOf<TSPNode>()
        for (node in nodes) {
            if (node !in solution.nodes) {
                l.add(node)
            }
        }
        return l
    }

    /**
     * Generates all moves combining INTER_NODES and INTRA_EDGES.
     */
    private fun getAllMoves(): List<Pair<Pair<Int, Int>, MoveType>> {
        val allMoves = mutableListOf<Pair<Pair<Int, Int>, MoveType>>()

        val intraEdgesMoves = getMoves(MoveType.INTRA_EDGES)
        val interNodesMoves = getMoves(MoveType.INTER_NODES)

        intraEdgesMoves.forEach { move ->
            allMoves.add(Pair(move, MoveType.INTRA_EDGES))
        }

        interNodesMoves.forEach { move ->
            allMoves.add(Pair(move, MoveType.INTER_NODES))
        }

        return allMoves
    }

    /**
     * Computes the distance matrix.
     */
    private fun getDistanceMatrix(): Array<DoubleArray> {
        val distanceMatrix = Array(nodes.size) { DoubleArray(nodes.size) }
        val type = DistanceType.EUCLIDEAN

        for (i in nodes.indices) {
            for (j in nodes.indices) {
                if (i == j) {
                    distanceMatrix[i][j] = Double.MAX_VALUE
                } else {
                    distanceMatrix[i][j] = nodes[i].calculateDistanceTo(nodes[j], type)
                }
            }
        }
        return distanceMatrix
    }

    /**
     * Retrieves moves based on the type of move.
     */
    private fun getMoves(type: MoveType): List<Pair<Int, Int>> {
        return when (type) {
            MoveType.INTRA_NODES -> getIntraNodesMoves()
            MoveType.INTRA_EDGES -> getIntraEdgesMoves()
            MoveType.INTER_NODES -> getInterNodesMoves()
            else -> throw IllegalArgumentException("Unsupported move type")
        }
    }

    private fun getInterNodesMoves(): List<Pair<Int, Int>> {
        val moves = mutableListOf<Pair<Int, Int>>()
        for (i in 0 until 100) {
            for (j in 0 until 100) {
                moves.add(Pair(i, j))
            }
        }
        return moves
    }

    private fun getIntraEdgesMoves(): List<Pair<Int, Int>> {
        val moves = mutableListOf<Pair<Int, Int>>()
        for (i in 0 until 100) {
            for (j in i + 2 until 100) {
                if (i == 0 && j == 99) continue
                moves.add(Pair(i, j))
            }
        }
        return moves
    }

    private fun getIntraNodesMoves(): List<Pair<Int, Int>> {
        val moves = mutableListOf<Pair<Int, Int>>()
        for (i in 0 until 100) {
            for (j in i + 1 until 100) {
                moves.add(Pair(i, j))
            }
        }
        return moves
    }
}

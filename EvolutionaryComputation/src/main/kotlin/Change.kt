import enums.MoveType
import java.util.Collections.swap

class Change(val move: Pair<Int, Int>, val type: MoveType, val distanceMatrix: Array<DoubleArray>) {

    fun calculateDelta(solution: TSPSolution, leftNodes: List<TSPNode>): Double {
        return when (type) {  // Access 'type' directly as it's a property now
            MoveType.INTRA_NODES -> intraNodesDelta(solution)
            MoveType.INTRA_EDGES -> intraEdgesDelta(solution)
            MoveType.INTER_NODES -> interNodesDelta(solution, leftNodes)
            else -> throw IllegalArgumentException("Move type not supported")
        }
    }


    private fun intraNodesDelta(solution: TSPSolution): Double {
        val max = solution.nodes.size - 1
        val min = 0
        val idf = solution.nodes[move.first].id
        val ids = solution.nodes[move.second].id
        val f1 = if (move.first - 1 < min) max else move.first - 1
        val idf1 = solution.nodes[f1].id
        val f2 = if (move.first + 1 > max) min else move.first + 1
        val idf2 = solution.nodes[f2].id
        val s1 = if (move.second - 1 < min) max else move.second - 1
        val ids1 = solution.nodes[s1].id
        val s2 = if (move.second + 1 > max) min else move.second + 1
        val ids2 = solution.nodes[s2].id


        var delta: Double = 0.0

        if (move.second - move.first == 1) {
            delta -= distanceMatrix[idf1][idf]
            delta -= distanceMatrix[ids][ids2]
            delta += distanceMatrix[idf1][ids]
            delta += distanceMatrix[idf][ids2]
            return delta
        }


        delta -= distanceMatrix[idf1][idf]

        delta -= distanceMatrix[idf][idf2]

        delta -= distanceMatrix[ids1][ids]

        delta -= distanceMatrix[ids][ids2]


        delta += distanceMatrix[idf1][ids]

        delta += distanceMatrix[ids][idf2]

        delta += distanceMatrix[ids1][idf]

        delta += distanceMatrix[idf][ids2]


        return delta
    }

    private fun intraEdgesDelta(solution: TSPSolution): Double {

        val max = solution.nodes.size - 1
        val min = 0
        val idf = solution.nodes[move.first].id
        val ids = solution.nodes[move.second].id
        val f2 = if (move.first + 1 > max) min else move.first + 1
        val idf2 = solution.nodes[f2].id
        val s2 = if (move.second + 1 > max) min else move.second + 1
        val ids2 = solution.nodes[s2].id

        var delta: Double = 0.0

        delta -= distanceMatrix[idf][idf2]
        delta -= distanceMatrix[ids][ids2]

        delta += distanceMatrix[idf][ids]
        delta += distanceMatrix[idf2][ids2]

        return delta
    }

    private fun interNodesDelta(solution: TSPSolution, leftNodes: List<TSPNode>): Double {

        val max = solution.nodes.size - 1
        val min = 0

        val idf = solution.nodes[move.first].id
        val ids = leftNodes[move.second].id

        val f1 = if (move.first - 1 < min) max else move.first - 1
        val idf1 = solution.nodes[f1].id
        val f2 = if (move.first + 1 > max) min else move.first + 1
        val idf2 = solution.nodes[f2].id

        var delta: Double = 0.0

        delta -= distanceMatrix[idf1][idf]
        delta -= distanceMatrix[idf][idf2]
        delta -= solution.nodes[move.first].cost

        delta += distanceMatrix[idf1][ids]
        delta += distanceMatrix[ids][idf2]
        delta += leftNodes[move.second].cost

        return delta
    }

    fun getNewSolution(solution: TSPSolution, leftNodes: List<TSPNode>): TSPSolution {
        return when (type) {  // Access 'type' directly as it's a property now
            MoveType.INTRA_NODES -> intraNodesSolution(solution)
            MoveType.INTRA_EDGES -> intraEdgesSolution(solution)
            MoveType.INTER_NODES -> interNodesSolution(solution, leftNodes)
            else -> throw IllegalArgumentException("Move type not supported")
        }
    }


    private fun intraNodesSolution(solution: TSPSolution): TSPSolution {
        val nodes = solution.nodes
        val indices = solution.indices
        swap(nodes, move.first, move.second)
        swap(indices, move.first, move.second)
        return TSPSolution(nodes, solution.type, indices)
    }

    private fun intraEdgesSolution(solution: TSPSolution): TSPSolution {
        val nodes = solution.nodes.toMutableList()
        val indices = solution.indices.toMutableList()
        val subListN = nodes.subList(move.first + 1, move.second + 1).reversed()
        val subListI = indices.subList(move.first + 1, move.second + 1).reversed()

        for (i in 0 until subListN.size) {
            nodes[move.first + i + 1] = subListN[i]
            indices[move.first + i + 1] = subListI[i]
        }


        return TSPSolution(nodes, solution.type, indices)
    }

    private fun interNodesSolution(solution: TSPSolution, leftNodes: List<TSPNode>): TSPSolution {


        val nodes = solution.nodes.toMutableList()
        val indices = solution.indices.toMutableList()

        nodes[move.first] = leftNodes[move.second]
        indices[move.first] = leftNodes[move.second].id

        return TSPSolution(nodes, solution.type, indices)
    }

}

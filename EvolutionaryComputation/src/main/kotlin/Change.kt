import enums.MoveType
import java.util.Collections.swap
import kotlin.math.pow
import kotlin.time.times

class Change(val move: Pair<Int, Int>, val type: MoveType, val distanceMatrix: Array<DoubleArray>) {

    fun getHash(solution: TSPSolution, leftNodes: List<TSPNode>): Array<Pair<Int, Int>> {
        val p = Pair(1,2)
        var hash: Array<Pair<Int,Int>> = arrayOf(p,p,p,p)

        if (type == MoveType.INTRA_EDGES){

            val l = move.first
            val h = move.second
            val l1 = if (l-1<0) solution.nodes.size-1 else l-1;
            val h1 = h-1

            val il1 = solution.nodes[l1].id
            val il = solution.nodes[l].id
            val ih1 = solution.nodes[h1].id
            val ih = solution.nodes[h].id


            val R1 = if (il1 < il) Pair(il1,il) else Pair(il,il1)
            val R2 = if (ih1 < ih) Pair(ih1,ih) else Pair(ih,ih1)

            val A1 = if (il1 < ih1) Pair(il1, ih1) else Pair(ih1,il1)
            val A2 = if (il < ih) Pair(il,ih) else Pair(ih,il)

            if (R1.first < R2.first){
                hash[0] = R1
                hash[1] = R2
            }
            else {
                hash[0] = R2
                hash[1] = R1
            }

            if (A1.first < A2.first){
                hash[2] = A1
                hash[3] = A2
            }
            else
            {
                hash[2] = A2
                hash[3] = A1
            }
            return hash



        }
        else
        {
            val f = move.first
            val s = move.second
            val f1 = if (f-1 < 0) solution.nodes.size-1 else f-1
            val f2 = if (f+1 > solution.nodes.size-1) 0 else f+1

            val If = solution.nodes[f].id
            val If1 = solution.nodes[f1].id
            val If2 = solution.nodes[f2].id
            val Is = leftNodes[s].id

            val R1 = if (If1 < If) Pair(If1,If) else Pair(If,If1)
            val R2 = if (If < If2) Pair(If,If2) else Pair(If2,If)

            val A1 = if (If1 < Is) Pair(If1, Is) else Pair(Is,If1)
            val A2 = if (Is < If2) Pair(Is,If2) else Pair(If2,Is)

            if (R1.first < R2.first){
                hash[0] = R1
                hash[1] = R2
            }
            else {
                hash[0] = R2
                hash[1] = R1
            }

            if (A1.first < A2.first){
                hash[2] = A1
                hash[3] = A2
            }
            else
            {
                hash[2] = A2
                hash[3] = A1
            }
            return hash


        }

    }


    fun checkWithElders(solution: TSPSolution, leftNodes: List<TSPNode>, hashMap: HashMap<Array<Pair<Int, Int>>, Double>): Double {

        val hash = this.getHash(solution, leftNodes)
        if (hashMap.containsKey(hash)) {
            return hashMap[hash]!!
        }
        val delta = calculateDelta(solution, leftNodes)
        hashMap[hash] = delta
        return delta
    }

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

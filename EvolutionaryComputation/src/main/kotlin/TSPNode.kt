import kotlin.math.pow
import kotlin.math.round
import kotlin.math.sqrt

data class TSPNode(val x: Double, val y: Double, val cost: Double) {
    fun calculateDistanceTo(other: TSPNode, type: DistanceType): Double {
        if (type == DistanceType.EUCLIDEAN) {
            return euclideanDistance(other)
        } else if (type == DistanceType.COSTEUCLIDEAN) {
            return costEuclideanDistance(other)
        } else {
            throw IllegalArgumentException("Invalid distance type")
        }
    }

    private fun euclideanDistance(other: TSPNode): Double {
        return round(sqrt((x - other.x).pow(2.0) + (y - other.y).pow(2.0)))
    }

    private fun costEuclideanDistance(other: TSPNode): Double {
        return euclideanDistance(other) + other.cost
    }

}

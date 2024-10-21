import enums.DistanceType

data class TSPSolution (val nodes: List<TSPNode>, val type: DistanceType, val indices: List<Int>) {
    var objectiveFunctionValue: Double? = null

    init {
        if (objectiveFunctionValue == null) {
            this.objectiveFunctionValue = calculateObjectiveFunctionValue()
        }
    }

    private fun calculateObjectiveFunctionValue(): Double {
        var totalCost = 0.0
        for (i in 0 until nodes.size - 1) {
            totalCost += nodes[i].calculateDistanceTo(nodes[i + 1], type)
        }
        totalCost += nodes.last().calculateDistanceTo(nodes.first(), type)
        return totalCost
    }

    override fun toString(): String {
        return "Solution(objectiveFunctionValue=$objectiveFunctionValue, Number of nodes: ${nodes.size}, nodes=$nodes)"
    }
}
package managers

import SolverParameters
import enums.HeuristicType

class CostManager (private val params: SolverParameters) {
    private fun getRegretCost(sortedInsertCosts: List<Pair<Int, Double>>): Double {
        var lastIndex = if (params.k > sortedInsertCosts.size-1) sortedInsertCosts.size-1 else params.k-1
        return sortedInsertCosts[0].second - sortedInsertCosts[lastIndex].second
    }
    fun getCost(sortedInsertCosts: List<Pair<Int, Double>>): Double {
        return when (params.heuristicType) {
            HeuristicType.GREEDY_KREGRET -> {
                getRegretCost(sortedInsertCosts)
            }
            HeuristicType.GREEDY_WEIGHTED_KREGRET -> {
                params.regretWeight!!*getRegretCost(sortedInsertCosts) + params.objectiveFunctionWeight!!*sortedInsertCosts[0].second
            }
            else -> {
                throw IllegalArgumentException("Heuristic type for this solver not supported")
            }
        }
    }
}
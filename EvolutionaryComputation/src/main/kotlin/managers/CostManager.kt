package managers

import SolverParameters
import enums.HeuristicType

class CostManager (private val params: SolverParameters) {
    private fun getRegretCost(sortedInsertCosts: List<Pair<Int, Double>>): Double {
        var regretCost = 0.0;
        for (i in 0 until params.k-1) {
            regretCost += sortedInsertCosts[i].second - sortedInsertCosts[i+1].second
        }
        return regretCost
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
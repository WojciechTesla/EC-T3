import enums.HeuristicType

class SolverParameters (val heuristicType: HeuristicType, val k: Int, val regretWeight: Double?, val objectiveFunctionWeight: Double?, val isOptimized: Boolean) {
    override fun toString(): String {
        return "SolverParameters(heuristicType=$heuristicType, isOptimized=$isOptimized, regretWeight=$regretWeight, objectiveFunctionWeight=$objectiveFunctionWeight)"
    }
}
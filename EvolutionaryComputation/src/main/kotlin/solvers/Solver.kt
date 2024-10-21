package solvers

import SolverParameters
import TSPSolution

interface Solver {
    fun call(params: SolverParameters): TSPSolution
}
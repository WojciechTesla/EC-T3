import enums.HeuristicType
import enums.LocalSearchType
import enums.MoveType
import utils.LocalExperimentUtil

fun main(args: Array<String>) {


    var st = listOf("TSPA.csv","TSPB.csv")
    for (val1 in HeuristicType.values())
    {
        for (val2 in LocalSearchType.values())
        {
            for (val3 in MoveType.values())
            {
                for (s in st){
                    var results = LocalExperimentUtil.performExperiment(LocalSearchType.STEEPEST, heuristicType = HeuristicType.RANDOM,MoveType.INTRA_EDGES,"TSPA.csv")
                    var totalTime = results.second
                    var objectives = results.first.map {result -> result.objectiveFunctionValue!!}
                    var pp = results.first.map {result -> Pair(result.objectiveFunctionValue,result)}
                    var sorted = pp.sortedBy {it.first}
                    var nodeMemory: MutableList<Array<TSPNode>> = mutableListOf()
                    for (res in results.first)
                    {
                        nodeMemory.add(res.nodes.toTypedArray())
                    }
                    var e = ExperimentResult(sorted[0].second.indices, sorted[0].first!!, objectives.average(),sorted[sorted.size-1].first!! , totalTime, nodeMemory)
                }
            }
        }
    }


}

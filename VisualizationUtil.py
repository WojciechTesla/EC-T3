import pandas as pd
import matplotlib.pyplot as plt

def splitCSV(filePath):
    dataframes = []
    currentData = []
    headers = None

    with open(filePath, 'r') as file:
        for line in file:
            if line.strip():
                if headers is None:
                    headers = line.strip().split(',')
                else:
                    currentData.append(line.strip().split(','))
            else:
                if currentData:
                    df = pd.DataFrame(currentData, columns=headers)
                    dataframes.append(df)
                    currentData = []

    if currentData:
        df = pd.DataFrame(currentData, columns=headers)
        dataframes.append(df)

    return dataframes

def plotNodesAndEdges(df, indices, index):
    print("Columns in DataFrame:", df.columns)
    df['X'] = pd.to_numeric(df['X'])
    df['Y'] = pd.to_numeric(df['Y'])
    df['Cost'] = pd.to_numeric(df['Cost'])
    
    plt.figure(figsize=(10, 6))
    
    scatter = plt.scatter(df['X'], df['Y'], c=df['Cost'], cmap='viridis', marker='o')
    plt.colorbar(scatter, label='Cost')
    

    num_rows = len(df)
    for i in range(len(indices)):
        print(f"Index {i}: {indices[i]}")
        start_x = df.iloc[indices[i]-1]['X']
        start_y = df.iloc[indices[i]-1]['Y']
        end_x = df.iloc[indices[(i + 1) % len(indices)]-1]['X']
        end_y = df.iloc[indices[(i + 1) % len(indices)]-1]['Y']
        plt.plot([start_x, end_x], [start_y, end_y], 'k-')
    
    plt.title('Node and Edge Visualization')
    plt.xlabel('X Coordinate')
    plt.ylabel('Y Coordinate')
    plt.grid(True)
    plt.savefig(f'tspb_kregret_visualization.png')
    plt.close()

def createMetricTable():
    resultsRandom = pd.read_csv('EvolutionaryComputation/ResultsTSPB/result_random.csv')
    resultsNNDf = pd.read_csv('EvolutionaryComputation/ResultsTSPB/result_nn.csv')
    resultsNNAnyDf = pd.read_csv('EvolutionaryComputation/ResultsTSPB/result_nnany.csv')
    resultsCycleDf = pd.read_csv('EvolutionaryComputation/ResultsTSPB/result_cycle.csv')
    resultsKregretDf = pd.read_csv('EvolutionaryComputation/ResultsTSPB/result_kregret.csv')
    resultsWeightedKregretDf = pd.read_csv('EvolutionaryComputation/ResultsTSPB/result_weighted_kregret.csv')

    resultsRandom['Method'] = 'Random'
    resultsNNDf['Method'] = 'Nearest Neighbor'
    resultsNNAnyDf['Method'] = 'Nearest Neighbor Any'
    resultsCycleDf['Method'] = 'Cycle'
    resultsKregretDf['Method'] = 'K-Regret'
    resultsWeightedKregretDf['Method'] = 'Weighted K-Regret'

    aggregatedResults = pd.concat([resultsRandom, resultsNNDf, resultsNNAnyDf, resultsCycleDf, resultsKregretDf, resultsWeightedKregretDf])

    print(aggregatedResults)

def main():

    nodes_df = pd.read_csv('EvolutionaryComputation/src/main/resources/TSPB.csv', delimiter=';')
    nodes_df.columns = ['X', 'Y', 'Cost']
    
    chosen_nodes_df = pd.read_csv('EvolutionaryComputation/ResultsTSPB/result_kregret_best_nodes.csv')
    
    chosen_indices = chosen_nodes_df['Index'].tolist()
    print("Chosen indices:", chosen_indices)
    filtered_nodes_df = nodes_df.loc[chosen_indices].reset_index(drop=True)
    print("Test", nodes_df.iloc[chosen_indices[0]-1])
    print("Filtered nodes DataFrame:")
    print(filtered_nodes_df)
    
    plotNodesAndEdges(nodes_df, chosen_indices, 0)
    
    # createMetricTable()

if __name__ == '__main__':
    main()
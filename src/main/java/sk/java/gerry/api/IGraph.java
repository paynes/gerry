package sk.java.gerry.api;

import java.util.List;
import java.util.Map;

/**
 *
 * @author paynes
 */
public interface IGraph {
    
    public String getGraphID();
    
    /**
     * 
     * @param vertex
     * @param id
     */
    public void addVertex(IVertex vertex, String id);
    
    /**
     * 
     * @param graph
     */
    public void addSubGprah(IGraph graph);
    
    /**
     * 
     * @param id
     * @return 
     */
    public IGraph getSubGraph(String id);
    
    /**
     * 
     * @return 
     */
    public Map<String, IGraph> getSubGraphs();
    
    /**
     * 
     * @param id
     */
    public void removeSubGraph(String id);
    
    /**
     * 
     * @param vertex removed vertex from graph
     */
    public void removeVertex(IVertex vertex);
    

    /**
     * 
     * @param vertexId
     * @return 
     */
    public IVertex findVertex(Integer vertexId);
    
    /**
     * 
     */
    public void build();
    
    /**
     * 
     * @param vertex1
     * @param vertex2
     * @return true if connection is deleted between vertices
     */
    public boolean deleteConnectionBetweenVertices(IVertex vertex1, IVertex vertex2);
    
    /**
     * 
     * @return all graphs vertices
     */
    public List<IVertex> getGraphVertices();
    
    /**
     * 
     * @return number of electors in graph
     */
    public Integer getSumOfElectors();
    
    /**
     * 
     * @param id
     * @return number of electors in subgraph
     */
    public Integer getSumOfElectorsOfSubGraph(String id);
    
    /**
     * 
     * @param repDem
     * @return 
     */
    public Integer getSumEvaluation(boolean repDem);
    
    /**
     * 
     * @param id
     * @param repDem true for republicans evaluation, false for democrats
     * @return evaluation in subgraph
     */
    public Integer getSumEvaluationOfSubGraph(String id, boolean repDem);
    
    /**
     * 
     * @return true if graph is connected
     */
    public boolean isGraphConnected();
}

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
     * @param vertex removed vertex from graph
     */
    public void removeVertex(IVertex vertex);
    
    /**
     * 
     * @param id
     * @return whether is the vertex in the graph
     */
    public IVertex findVertex(Integer id);
    
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
     * @param id vertex id
     * @return vertex with given id or null
     */
    public IVertex getVertexById(int id);
    
    /**
     * 
     * @return number of electors in graph
     */
    public Integer getSumOfElectors();
    
    /**
     * 
     * @param i number of subgraph
     * @return number of electors in subgraph
     */
    public Integer getSumOfElectorsOfSubGraph(int i);
    
    /**
     * 
     * @param i index of vertex cover
     * @param repDem true for republicans evaluation, false for democrats
     * @return evaluation in subgraph
     */
    public Integer getSumEvaluationOfSubGraph(int i, boolean repDem);
    
    /**
     * 
     * @return true if graph is connected
     */
    public boolean isGraphConnected();
}

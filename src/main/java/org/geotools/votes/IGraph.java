/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geotools.votes;

import java.util.ArrayList;

/**
 *
 * @author paynes
 */
public interface IGraph {
    
    /**
     * 
     * @param v add new vertex v in the graph
     */
    public void addVertex(IVertex vertex);
    
    /**
     * 
     * @param vertex removed vertex from graph
     */
    public void removeVertex(IVertex vertex);
    
    /**
     * 
     * @param vertex wanted vertex
     * @return whether is the vertex in the graph
     */
    public boolean findVertex(IVertex vertex);
    
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
    public ArrayList<IVertex> getV();
    
    /**
     * 
     * @param id vertex id
     * @return vertex with given id or null
     */
    public IVertex getVertexById(int id);
    
    /**
     * 
     * @return true if graph is connected
     */
    public boolean isGraphConnected();
}

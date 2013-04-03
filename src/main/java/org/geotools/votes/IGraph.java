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
     * @param vertex remove vertex from graph
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
     * @param edge add new edge in the graph
     */
    public void addEdge(Edge edge);
    
    /**
     * 
     * @param edge remove edge from the graph
     */
    public void removeEdge(Edge edge);
    
    /**
     * 
     * @return all graphs vertices
     */
    public ArrayList<IVertex> getV();
    
    /**
     * 
     * @return all graphs edges
     */
    public ArrayList<IEdge> getE();
    
    /**
     * 
     * @param id vertex id
     * @return vertex with given id or null
     */
    public IVertex getVertexById(int id);
}

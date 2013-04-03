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

public interface IVertex {
    
    /**
     * 
     * @return Vertex id
     */
    public Integer getId();
    
    /**
     * 
     * @param vertex add neighbour vertex
     */
    public void addNeighbourVertex(IVertex vertex);
    
    /**
     * 
     * @param vertex remove neighbour vertex
     */
    public void removeNeighbourVertex(IVertex vertex);
    
    /**
     * 
     * @return neighbour vertices
     */
    public ArrayList<IVertex> getNeighbourVertices();
    
    /**
     * 
     * @return number of democrats electors
     */
    public Integer getDemRes();
    
    /**
     * 
     * @param res set number of democrats electors
     */
    public void setDemRes(Integer res);
    
    /**
     * 
     * @return number of republicans electors
     */
    public Integer getRepRes();
    
    /**
     * 
     * @param res set number of republicans electors
     */
    public void setRepRes(Integer res);
    
    /**
     * 
     * @param cov set number of region
     */
    public void setCover(Integer cov);
    
    /**
     * 
     * @return number of region
     */
    public Integer getCover();
    
    /**
     * 
     * @return total electors
     */
    public Integer getSumOfElectors();
    
    /**
     * 
     * @return difference the number of democrats and number of republicans (dem - rep)
     */
    public Integer getDemEvaluation();
    
    /**
     * 
     * @return difference the number of republicans and democrats (rep - dem)
     */
    public Integer getRepEvaluation();
    
    /**
     * Indicates whether we can remove vertex from graph and graph still remains connected
     * @return true or false
     */
    public boolean getCut();
    
    /**
     * Indicates whether we can remove vertex from graph and graph still remains connected
     * 
     * @param cut set true or false
     */
    public void setCut(boolean cut);
}

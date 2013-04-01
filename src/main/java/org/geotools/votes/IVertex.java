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
    
    public Integer getId();
    
    public void addNeighbourVertex(Vertex vertex);
    
    public void removeNeighbourVertex(Vertex vertex);
    
    public ArrayList<IVertex> getNeighbourVertices();
    
    public Integer getDemRes();
    
    public void setDemRes(Integer res);
    
    public Integer getRepRes();
    
    public void setRepRes(Integer res);
    
    public void setCover(Integer cov);
    
    public Integer getCover();
    
    public Integer getScale();
    
    public Integer getDemEvaluation();
    
    public Integer getRepEvaluation();
    
    public boolean getCut();
    
    public void setCut(boolean cut);
}

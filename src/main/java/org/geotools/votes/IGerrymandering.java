/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geotools.votes;

/**
 *
 * @author paynes
 */
public interface IGerrymandering {
    
    public Integer getSumOfElectors();
    
    //upravit na private
    public Integer getDemEvaluation();
    
    //upravit na private
    public Integer getRepEvaluation();
    
    public Integer getEvaluation(boolean repDem);
}

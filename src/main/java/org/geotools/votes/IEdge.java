/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geotools.votes;

/**
 *
 * @author paynes
 */
public interface IEdge {
    
    /**
     * 
     * @return first vertex of edge e(v1,v2)
     */
    public IVertex getV1();
    
    /**
     * 
     * @return second vertex of edge e(v1,v2)
     */
    public IVertex getV2();
    
}

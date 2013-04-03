/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geotools.votes;

/**
 *
 * @author paynes
 */
public class Main {
    public static void main(String[] args) throws Exception {
        Graph g = new Graph("graph");
        Vertex v = new Vertex(5);
        Vertex v2 = new Vertex(2);
        Vertex v3 = new Vertex(1);
        try {
            g.addVertex(v);
            g.removeVertex(v);
            System.out.println(g.getV());
        } catch(NullPointerException ex) {
            System.out.println(ex);
        }

    }
}

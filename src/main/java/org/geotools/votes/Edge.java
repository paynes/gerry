package org.geotools.votes;

public class Edge implements IEdge{

    private IVertex v1;
    private IVertex v2;
	
    public Edge(Vertex v1, Vertex v2) {
        this.v1 = v1;
	this.v2 = v2;
    }

    public IVertex getV1() {
	return this.v1;
    }
	
    public IVertex getV2() {
	return this.v2;
    }
    
    @Override
    public String toString() {
	return "v1:" + this.v1.getId() + " v2:" + this.v2.getId();
    }
	
    @Override
    public boolean equals(Object o) {
	if (this == o) return true;
        if (!(o instanceof Edge)) return false;
        
        Edge e = (Edge) o;
        
        if (!(this.v1.equals(e.getV1()))) return false;
        if (!(this.v2.equals(e.getV2()))) return false;
        
	return true;
    }
	
    @Override
    public int hashCode() {
    	int hash = 17;
    	hash = 37 * hash + v1.hashCode();
    	hash = 37 * hash + v2.hashCode();
    	return hash;
    }
		
}

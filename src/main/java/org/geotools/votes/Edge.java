package org.geotools.votes;

public class Edge {

	private Vertex v1;
	private Vertex v2;
	
	public Edge(Vertex v1, Vertex v2) {
		this.v1 = v1;
		this.v2 = v2;
	}

	public Vertex getV1() {
		return this.v1;
	}
	
	public Vertex getV2() {
		return this.v2;
	}
	
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj instanceof Edge) {
			Edge e = (Edge) obj;
			return (e.getV1().equals(getV1()) && e.getV2().equals(getV2())); 
		}
		return false;
	}
	
    public int hashCode() {
    	int hash = 17;
    	hash = 37 * hash + v1.hashCode();
    	hash = 37 * hash + v2.hashCode();
    	return hash;
        }
	
	public String toString() {
		return v1.getId() + " " + v2.getId();
	}	
}

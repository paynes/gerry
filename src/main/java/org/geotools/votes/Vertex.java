package org.geotools.votes;

import java.util.ArrayList;

public class Vertex {

	private Integer id;
	private Integer cover;
	private Integer demRes;
	private Integer repRes;
	private boolean cut;
	private ArrayList<Integer> childVertices = null;
	
	public Vertex(Integer id, Integer repRes, Integer demRes) {
		this.id = id;
		this.demRes = demRes;
		this.repRes = repRes;
	}
	
	public Vertex(Integer id) {
		this.id = id;
		this.cover = 0;
		this.repRes = 0;
		this.demRes = 0;
		this.cut = false;
		this.childVertices = new ArrayList<Integer>();
	}

	public Integer getId() {
		return this.id;
	}
	
	public void addChild(Integer i) {
		if (!this.childVertices.contains(i))
			this.childVertices.add(i);
	}
	
	public void removeChild(Integer index) {
		for (int i = 0; i < this.childVertices.size();i++) {
			if (childVertices.get(i).equals(index)) {
				childVertices.remove(i);
			}
		}
	}
/*	public Vertex findChild(Integer i) {
		Vertex v
		for (int j = 0; j < this.childVertices.size(); j++) {
			if ()
		}
			
		return v;
	}*/
	
	public void removeEdge(Integer i) {
		this.childVertices.remove(i);
	}
	
	public Vertex copy() {
		Vertex vertex = new Vertex(this.id);
		vertex.setCover(this.cover);
		vertex.setDemRes(this.demRes);
		vertex.setRepRes(this.repRes);
		vertex.setCut(cut);
		//for (Integer i : this.childVertices) {
			//vertex.addChild(i);
		//}
		return vertex;
	}
	
	public ArrayList<Integer> getChildVertices() {
		return this.childVertices;
	}
	
	public Integer getDemRes() {
		return this.demRes;
	}
	
	public void setDemRes(Integer res) {
		demRes = res;
	}
	
	public Integer getRepRes() {
		return this.repRes;
	}
	
	public void setRepRes(Integer res) {
		repRes = res;
	}
	
	public void setCover(Integer cov) {
		cover = cov;
	}
	
	public Integer getCover() {
		return this.cover;
	}
	
	public Integer getWeight() {
		return demRes + repRes;
	}
	
	public Integer getEvaluation() {
		
		//tu prehod na repRes - demRes
		return demRes - repRes;
	}
	
	public boolean getCut() {
		return this.cut;
	}
	
	public void setCut(boolean c) {
		this.cut = c;
	}
	
	public String toString() {
		return id.toString() +  " " + this.cover.toString() +  " " + this.repRes.toString() + " " + this.demRes.toString() + " " + this.cut;
		//return id.toString();// + " " + this.repRes.toString() + " " + this.demRes.toString();
	}
	
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj instanceof Vertex) {
            Vertex v = (Vertex) obj;
            return v.id == id;
        }
        return false;
    }
	
    public int hashCode() {
        int hash = 17;
        hash = 37 * hash + id;
        return hash;
    }
}

package org.geotools.votes;

import java.util.ArrayList;

public class Vertex implements IVertex{

    private Integer id;
    private Integer cover;
    private Integer demRes;
    private Integer repRes;
    private boolean cut;
    private ArrayList<IVertex> neighbourVertices;
    
    public Vertex(Integer id) {
        this.id = id;
        this.cover = -1;
        this.repRes = -1;
        this.demRes = -1;
        this.cut = false;
        this.neighbourVertices = new ArrayList<IVertex>();
    }

    public Integer getId() {
        return this.id;
    }

    public void addNeighbourVertex(IVertex vertex) {
        this.neighbourVertices.add(vertex);
    }

    public void removeNeighbourVertex(IVertex vertex) {
        if (!(this.neighbourVertices.remove(vertex))) throw new NullPointerException("Vrchol nesusedi s vrcholom " + vertex.getId());
    }

    public ArrayList<IVertex> getNeighbourVertices() {
        return this.neighbourVertices;
    }

    public Integer getDemRes() {
        return this.demRes;
    }

    public void setDemRes(Integer res) {
        this.demRes = res;
    }

    public Integer getRepRes() {
        return this.repRes;
    }

    public void setRepRes(Integer res) {
        this.repRes = res;
    }

    public void setCover(Integer cov) {
        this.cover = cov;
    }

    public Integer getCover() {
        return this.cover;
    }

    public Integer getSumOfElectors() {
        return this.repRes + this.demRes;
    }

    public Integer getDemEvaluation() {
        return this.demRes - this.repRes;
    }

    public Integer getRepEvaluation() {
        return this.repRes - this.demRes;
    }

    public boolean getCut() {
        return this.cut;
    }

    public void setCut(boolean cut) {
        this.cut = cut;
    }
    
    @Override
    public String toString(){
        return "id:" + this.id.toString() + " cover:" + this.cover.toString() + " repRes:" + this.repRes.toString() + " demRes:" + this.demRes.toString();
        
    }
    
    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (!(o instanceof Vertex)) return false;
        
        Vertex vertex = (Vertex) o;
        
        if (this.id != vertex.getId()) return false;
        
        return true;
    }
    
    @Override
    public int hashCode(){
        int result = (int) (this.id ^ (this.id >>> 32));
        return result;
    }
}

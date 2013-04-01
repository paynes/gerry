package org.geotools.votes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;

import org.geotools.filter.expression.ThisPropertyAccessorFactory;

public class Graph {
	
	private String id;
	private ArrayList<Vertex> V = null;
	private ArrayList<Edge> E = null;
	
	public Graph(String id) {
		this.id = id;
		this.V = new ArrayList<Vertex>();
		this.E = new ArrayList<Edge>();
	}
	
	
	public void addVertex(Vertex v) {
		if (!this.V.contains(v))
			this.V.add(v);
	}
	
	public Vertex findVertex(Integer index) {
		for (Vertex v : this.V)
			if (v.getId().equals(index))
				return v;
		return null;
	}	
	
	public void removeVertex(Vertex v) {
		ArrayList<Edge> edges = new ArrayList<Edge>();
		edges = this.findEdges(v);
		for (Edge e : edges) 
				E.remove(e);
		for (Vertex ve : this.V) {
			ve.removeChild(v.getId());
		}
		this.V.remove(v);
	}
	
	public void addEdge(Edge e) {
		if (!this.E.contains(e))
			this.E.add(e);
	}
	
	
	public void removeEdge(Edge e) {
		this.E.remove(e);
	}
	
	public String getId() {		
		return this.id;
	}
	
	public ArrayList<Vertex> getV() {
		return this.V;
	}
	
	public ArrayList<Edge> getE() {
		return this.E;
	}
	
	public Graph clone() {
		Graph graph = new Graph(this.id);
		graph.E = extractedE();
		graph.V = extractedV();
		return graph;
	}


	private ArrayList<Vertex> extractedV() {
		return (ArrayList<Vertex>) this.V.clone();
	}


	private ArrayList<Edge> extractedE() {
		return (ArrayList<Edge>) this.E.clone();
	}
	

	
	public ArrayList<Edge> findEdges(Vertex v) {
		ArrayList<Edge> edgeL = new ArrayList<Edge>();
		for (Edge e : getE()) {
			if ((e.getV1().equals(v)) || (e.getV2().equals(v))) {
				edgeL.add(e);
			}
		}
		return edgeL;
	}
	
	//returns id of all districts
	public ArrayList<Integer> getDistricts() {
		ArrayList<Integer> districts = new ArrayList<Integer>();
		for (Vertex v : getV()) {
			if (districts == null) {
				districts.add(v.getCover());
			}
			if (!(districts.contains(v.getCover()))) {
				districts.add(v.getCover());
			}
		}
		return districts;
	}
	
	//divide graph to subVertex by districts
	public HashMap<Integer,Graph> getSubVertex() {
		HashMap<Integer,Graph> subVertex = new HashMap<Integer,Graph>();
		for (Vertex v : getV()) {
			if (subVertex.isEmpty()) {
				String s = String.valueOf(v.getCover());
				Graph graph = new Graph(s);
				graph.addVertex(v.copy());
				subVertex.put(v.getCover(), graph);
				continue;
			}
			if (subVertex.containsKey(v.getCover())) {
				subVertex.get(v.getCover()).addVertex(v.copy());
			} else {
				String s = String.valueOf(v.getCover());
				Graph graph = new Graph(s);
				graph.addVertex(v.copy());
				subVertex.put(v.getCover(), graph);
			}
		}		
		return subVertex;
	}
	
	
	public HashMap<Integer,Graph> getSubEdge(HashMap<Integer,Graph> subVertex) {
		for (Integer i : getDistricts()) {
			for (int index = 0; index < subVertex.get(i).getV().size(); index++) {
				Vertex v = subVertex.get(i).getV().get(index).copy();
				for (int j = 0; j < subVertex.get(i).getV().size(); j++) {
					Edge e = new Edge(v,subVertex.get(i).getV().get(j));
					if (E.contains(e)) {
						subVertex.get(i).getV().get(index).addChild((subVertex.get(i).getV().get(j).getId()));
						subVertex.get(i).addEdge(e);
					}
				}
			}
			//System.out.println(i);
			//subVertex.get(i).cutVertices();
		}
		return subVertex;
	}
	
	public Integer getSumWeight() {
		Integer weight = 0;
		for (Vertex v : getV()) {
			weight = weight + v.getWeight();
		}
		return weight;
	}
	
	public Graph cutVertices() {
		ArrayList<Vertex> vertices = new ArrayList<Vertex>();
		ArrayList<Integer> visitVertex = new ArrayList<Integer>();
		Stack<Integer> stack = new Stack<Integer>();
		
		for (Vertex v : this.getV()) {
			visitVertex.clear();
			vertices.clear();
			vertices.addAll(this.getV());
			if (vertices.size() == 1) {
				v.setCut(false);
				break;
			}
			//System.out.println("Chyba");
			//System.out.println(v);
			//System.out.println(v.getChildVertices());
			stack.add(v.getChildVertices().get(0));
			stack.removeElement(v.getId());
			visitVertex.add(v.getId());
			vertices.remove(v);
			while (!(stack.isEmpty())) {
				int index = 0;
				for (int i = 0; i < vertices.size();i++) {
					if ((vertices.get(i).getId().equals(stack.peek())) && (!(visitVertex.contains(stack.peek())))) {
						index = stack.pop();
						visitVertex.add(index);
						break;
					}
				}
				for (int j = 0; j < this.findVertex(index).getChildVertices().size();j++) {
					if (!(visitVertex.contains(this.findVertex(index).getChildVertices().get(j))) && (!(stack.contains(this.findVertex(index).getChildVertices().get(j))))) {
						stack.push(this.findVertex(index).getChildVertices().get(j));
					}
				}
			}
			for (Vertex vertex : this.getV()) {
				if (visitVertex.contains(vertex.getId())) {
					v.setCut(true);
				} else {
					v.setCut(false);
					break;
				}
			}	
		}
		return this;
	}
	
	public Integer getSumEvaluation() {
		Integer evaluation = 0;
		for (Vertex v : getV()) {
			evaluation = evaluation + v.getEvaluation();
		}
		return evaluation;
	}
	
	private Integer max(Integer num1, Integer num2) {
		if (num1 >= num2) {
			return num1;
		}
		return num2;
	}
	
	public ArrayList<Integer> losingDistricts() {
		ArrayList<Integer> dist = new ArrayList<Integer>();
		HashMap<Integer,Graph> subVertex = this.getSubEdge(this.getSubVertex());
		for(int i : subVertex.keySet()) {
			if (subVertex.get(i).getSumEvaluation() < 0) {
				dist.add(i);
			}
		}
		return dist;
	}
	
	public int atLeastVotes(ArrayList<Integer> dist) {
		int district = dist.get(0);
		HashMap<Integer,Graph> subVertex = this.getSubEdge(this.getSubVertex());
		for (int i = 1; i < dist.size(); i++) {
			if (subVertex.get(district).getSumEvaluation() < subVertex.get(dist.get(i)).getSumEvaluation()) {
				district = dist.get(i);
			}
		}
		return district;
	}
	
	public Integer getUnbalancedGraph(HashMap<Integer,Graph> subVertex, Integer upper,Integer lower) {
		Integer weight = 0;
		int numDist = -1;
		for (int i : getDistricts()) {
			if (max(subVertex.get(i).getSumWeight() - upper, lower - subVertex.get(i).getSumWeight()) <= 0 ) {
				continue;
			}
			if (max(subVertex.get(i).getSumWeight() - upper, lower - subVertex.get(i).getSumWeight()) > 0 ) {
				Integer helpInt = weight;
				weight = max(weight,max(subVertex.get(i).getSumWeight() - upper, lower - subVertex.get(i).getSumWeight()));
				if (weight != helpInt) {
					numDist = i;
				}
			}
		}
		return numDist;
	}
	
	public boolean getConnectedComponents() {
		ArrayList<Vertex> vertices = new ArrayList<Vertex>();
		ArrayList<Integer> visitVertex = new ArrayList<Integer>();
		Stack<Integer> stack = new Stack<Integer>();
		
		Vertex v = this.getV().get(0);
		vertices.addAll(this.getV());
		if (v.getChildVertices().isEmpty()) {
			return false;
		}
		stack.add(v.getChildVertices().get(0));
		stack.removeElement(v.getId());
		visitVertex.add(v.getId());
		//vertices.remove(v);
		while (!(stack.isEmpty())) {
			int index = 0;
			for (int i = 0; i < vertices.size();i++) {
				if ((vertices.get(i).getId().equals(stack.peek())) && (!(visitVertex.contains(stack.peek())))) {
					index = stack.pop();
					visitVertex.add(index);
					break;
				}
			}
			for (int j = 0; j < this.findVertex(index).getChildVertices().size();j++) {
				if (!(visitVertex.contains(this.findVertex(index).getChildVertices().get(j))) && (!(stack.contains(this.findVertex(index).getChildVertices().get(j))))) {
					stack.push(this.findVertex(index).getChildVertices().get(j));
				}
			}
		}
		//System.out.println(visitVertex);
		//System.out.println(vertices);
		boolean check = false;
		for (Vertex vertex : vertices) {
			if (visitVertex.contains(vertex.getId())) {
				check = true;		
			} else {
				check = false;
				break;
			}
			
		}
		return check;
	}
	
	public void makeConnectedComponents(Graph graph) {
		ArrayList<Integer> vertices = new ArrayList<Integer>();
		Stack<Integer> stack = new Stack<Integer>();
		ArrayList<ArrayList<Integer>> candidateComponents = new ArrayList<ArrayList<Integer>>();
		ArrayList<Integer> check = new ArrayList<Integer>();
		int change = -1;
		
		for (int i = 0; i < this.getV().size(); i++) {
			Vertex v = (Vertex) this.getV().get(i).copy();
			vertices.add(v.getId());
		}
		while (!(vertices.isEmpty())) {
			stack.add(vertices.get(0));
			ArrayList<Integer> component = new ArrayList<Integer>();
			while (!(stack.isEmpty())) {
				Integer vertex = null;
				for (int j = 0; j < vertices.size(); j++) {
					if (vertices.get(j) == stack.peek()) {
						vertex = vertices.get(j);
						vertices.remove(j);
						component.add(vertex);
						check.add(vertex);
						stack.pop();
						break;
					}
				}
				for (int k = 0; k < this.findVertex(vertex).getChildVertices().size(); k++) {
					if (!(stack.contains(this.findVertex(vertex).getChildVertices().get(k))) && (!(check.contains(this.findVertex(vertex).getChildVertices().get(k))))) {
						stack.push(this.findVertex(vertex).getChildVertices().get(k));
					}
				}
				
			}
			candidateComponents.add(component);
		}
		//System.out.println(candidateComponents.get(0));
		//System.out.println(candidateComponents.get(1));
		//System.out.println(candidateComponents.get(2));
		while (candidateComponents.size() != 1) {
			int population = 0;
			int population2 = -1;
			int cover = -1;
			for (int index = 0; index < candidateComponents.size(); index++) {
				
				for (int vert = 0; vert < candidateComponents.get(index).size(); vert++) {
					population = this.findVertex(candidateComponents.get(index).get(vert)).getWeight();
					
				}
				if ((population2 > population) || (population > -1)) {
					population2 = population;
					cover = index;
				}
			}
			for (int index = 0; index < candidateComponents.get(cover).size(); index ++) {
				for (int k = 0; k < graph.findVertex(candidateComponents.get(cover).get(index)).getChildVertices().size(); k++) {
					int useCover = graph.findVertex(graph.findVertex(candidateComponents.get(cover).get(index)).getChildVertices().get(k)).getCover();
					if (useCover != graph.findVertex(candidateComponents.get(cover).get(k)).getCover()) {
						change = useCover;
						break;
					}
				}
				if (change != -1) {
					break;
				}
			}
			for (int index = 0; index < candidateComponents.get(cover).size(); index++) {
				for (int k = 0; k < candidateComponents.get(cover).get(index); k++) {
					graph.findVertex(candidateComponents.get(cover).get(index)).setCover(change);
				}
			}
			candidateComponents.remove(cover);
			
		}
		//System.out.println(candidateComponents.get(0));
	}
	
	public HashMap<Integer,Graph> repairUnconnectedGraph(HashMap<Integer,Graph> subGraph) {
		for (Integer i : this.getDistricts()) {
			if (!(subGraph.get(i).getConnectedComponents())) {
				subGraph.get(i).makeConnectedComponents(this);
			}
		}
		return subGraph;
	}
	
	
	public Graph gerryMandering(Integer upper, Integer lower) throws CloneNotSupportedException {
		//HashMap<Integer,Graph> subVertex = this.getSubEdge(this.getSubVertex());
		int check = 0;
		ArrayList<Vertex> vertices = new ArrayList<Vertex>();
		ArrayList<Integer> lostsDistricts = new ArrayList<Integer>();
		ArrayList<Integer> visitSubGraph = new ArrayList<Integer>();
		ArrayList<Vertex> candidateOtherVertices = new ArrayList<Vertex>();
		ArrayList<Vertex> candidateOtherVertices2 = new ArrayList<Vertex>();
		HashMap<Integer,ArrayList<Integer>> candidateVertices = new HashMap<Integer,ArrayList<Integer>>();
		HashMap<Integer,ArrayList<Integer>> candidateVertices2 = new HashMap<Integer,ArrayList<Integer>>();
		lostsDistricts = this.losingDistricts();
		while (!(lostsDistricts.isEmpty())) {
			HashMap<Integer,Graph> subVertex = this.getSubEdge(this.getSubVertex());
			for (Integer i : this.getDistricts()) {
				subVertex.get(i).cutVertices();
			}
			candidateVertices.clear();
			candidateVertices2.clear();
			candidateOtherVertices.clear();
			candidateOtherVertices2.clear();
			//System.out.println(lostsDistricts);
	        //System.out.println(subVertex.get(1).getSumEvaluation());
	        //System.out.println(subVertex.get(2).getSumEvaluation());
	        //System.out.println(subVertex.get(3).getSumEvaluation());
	        //System.out.println(subVertex.get(4).getSumEvaluation());
	        //System.out.println(subVertex.get(5).getSumEvaluation());
	        //System.out.println(subVertex.get(6).getSumEvaluation());
	        //System.out.println(subVertex.get(7).getSumEvaluation());
	        //System.out.println(subVertex.get(8).getSumEvaluation());
	        //System.out.println(subVertex.get(9).getSumEvaluation());
	        //System.out.println(subVertex.get(10).getSumEvaluation());
	        //System.out.println(subVertex.get(11).getSumEvaluation());
	        //System.out.println(subVertex.get(12).getSumEvaluation());
	        //System.out.println(subVertex.get(13).getSumEvaluation());
	        //System.out.println("dalsi");
			Integer lostDistrict = this.atLeastVotes(lostsDistricts);;
			if (check == subVertex.get(lostDistrict).getSumEvaluation()) {
				lostDistrict = this.atLeastVotes(lostsDistricts);
				lostsDistricts.remove(lostDistrict);
				continue;
			}
			Stack<Vertex> stack = new Stack<Vertex>();
			stack.addAll(subVertex.get(lostDistrict).getV());
			vertices = getVerticesWithBorderEdge(stack);
			int compute = 0;
			int compute2 = 0;
			for (Vertex v : vertices) {
				visitSubGraph.clear();
				if (v.getEvaluation() < 0){
					for (int index = 0; index < this.findVertex(v.getId()).getChildVertices().size(); index++) {
						int cover = this.findVertex(this.findVertex(v.getId()).getChildVertices().get(index)).getCover();
						if ((cover != lostDistrict) && (!(visitSubGraph.contains(cover))) && v.getCut() && ((subVertex.get(cover).getSumEvaluation() > -v.getEvaluation()) || (subVertex.get(cover).getSumEvaluation() < 0))) {
							visitSubGraph.add(cover);
							ArrayList<Integer> candidateVertex = new ArrayList<Integer>();
							candidateVertex.add(v.getId());
							candidateVertex.add(cover);
							if (v.getEvaluation() < subVertex.get(lostDistrict).getSumEvaluation()) {
								candidateVertices.put(compute, candidateVertex);
								compute = compute + 1;
							} else {
								candidateVertices2.put(compute2, candidateVertex);
							}
						}	
					}	
				}
			}
			for (Vertex v : vertices) {
				for (int index = 0; index < this.findVertex(v.getId()).getChildVertices().size(); index++) {
					Vertex vertex = this.findVertex(this.findVertex(v.getId()).getChildVertices().get(index));
					vertex = subVertex.get(vertex.getCover()).findVertex(vertex.getId());
					int cover = vertex.getCover();
					if ((cover != lostDistrict) && (!(visitSubGraph.contains(vertex.getId()))) && vertex.getCut() && vertex.getEvaluation() > 0 && ((subVertex.get(vertex.getCover()).getSumEvaluation() > vertex.getEvaluation()) || (subVertex.get(vertex.getCover()).getSumEvaluation() < 0))) {
						visitSubGraph.add(vertex.getId());
						if (vertex.getEvaluation() > -subVertex.get(lostDistrict).getSumEvaluation()) {
							candidateOtherVertices.add(vertex);
						} else {
							candidateOtherVertices2.add(vertex);
						}
					}
				}	
			}
			Vertex v1;
			if (!(candidateOtherVertices.isEmpty())) {
				v1 = candidateOtherVertices.get(0);
				for (int i = 1; i < candidateOtherVertices.size();i++) {
					if (max(subVertex.get(lostDistrict).getSumWeight() + v1.getWeight() - upper,lower - subVertex.get(v1.getCover()).getSumWeight() + v1.getWeight()) > max(subVertex.get(lostDistrict).getSumWeight() + candidateOtherVertices.get(i).getWeight() - upper,lower - subVertex.get(candidateOtherVertices.get(i).getCover()).getSumWeight() + candidateOtherVertices.get(i).getWeight()))
						v1 = candidateOtherVertices.get(i);
				}
				if (!(candidateVertices.isEmpty())) {
					int index = -1;
					for (int j = 0; j < candidateVertices.size(); j++) {
						if (max(subVertex.get(v1.getCover()).getSumWeight() + v1.getWeight() - upper,lower - subVertex.get(lostDistrict).getSumWeight() + v1.getWeight()) > max(subVertex.get(candidateVertices.get(j).get(1)).getSumWeight() + this.findVertex(candidateVertices.get(j).get(0)).getWeight() - upper,lower - subVertex.get(lostDistrict).getSumWeight() + this.findVertex(candidateVertices.get(j).get(0)).getWeight())) {
							v1 = this.findVertex(candidateVertices.get(j).get(0));
							index = candidateVertices.get(j).get(1);
						} 
					}
					if (index != -1) {
						check = subVertex.get(lostDistrict).getSumEvaluation();
						this.findVertex(v1.getId()).setCover(index);
						subVertex = this.getSubEdge(this.getSubVertex());
				        //System.out.println(subVertex.get(1).getSumEvaluation());
				        //System.out.println(subVertex.get(2).getSumEvaluation());
				        //System.out.println(subVertex.get(3).getSumEvaluation());
						this.rebalancing(upper, lower);
						lostsDistricts.remove(lostDistrict);
						lostsDistricts = this.losingDistricts();
						continue;
					} else {
						check = subVertex.get(lostDistrict).getSumEvaluation();
						this.findVertex(v1.getId()).setCover(lostDistrict);
						this.rebalancing(upper, lower);
						lostsDistricts.remove(lostDistrict);
						lostsDistricts = this.losingDistricts();
						continue;
					}
				} else {
					check = subVertex.get(lostDistrict).getSumEvaluation();
					this.findVertex(v1.getId()).setCover(lostDistrict);
					subVertex = this.getSubEdge(this.getSubVertex());
			        //System.out.println(subVertex.get(1).getSumEvaluation());
			        //System.out.println(subVertex.get(2).getSumEvaluation());
			        //System.out.println(subVertex.get(3).getSumEvaluation());
					this.rebalancing(upper, lower);
					lostsDistricts.remove(lostDistrict);
					lostsDistricts = this.losingDistricts();
					continue;
				}
			} else {
				if (!(candidateVertices.isEmpty())) {
					v1 = this.findVertex(candidateVertices.get(0).get(0));
					int index = candidateVertices.get(0).get(1);
					for (int j = 0; j < candidateVertices.size(); j++) {
						if (max(subVertex.get(v1.getCover()).getSumWeight() + v1.getWeight() - upper,lower - subVertex.get(lostDistrict).getSumWeight() + v1.getWeight()) > max(subVertex.get(candidateVertices.get(j).get(1)).getSumWeight() + this.findVertex(candidateVertices.get(j).get(0)).getWeight() - upper,lower - subVertex.get(lostDistrict).getSumWeight() + this.findVertex(candidateVertices.get(j).get(0)).getWeight())) {
							v1 = this.findVertex(candidateVertices.get(j).get(0));
							index = candidateVertices.get(j).get(1);
						} 
					}
					//presun v1 do districtu
					//System.out.println(v1);
					check = subVertex.get(lostDistrict).getSumEvaluation();
					this.findVertex(v1.getId()).setCover(index);
					this.rebalancing(upper, lower);
					lostsDistricts.remove(lostDistrict);
					lostsDistricts = this.losingDistricts();
					continue;
				}
			}	
			if (!(candidateOtherVertices2.isEmpty())) {
				v1 = candidateOtherVertices2.get(0);
				for (int i = 1; i < candidateOtherVertices2.size();i++) {
					if (max(subVertex.get(lostDistrict).getSumWeight() + v1.getWeight() - upper,lower - subVertex.get(v1.getCover()).getSumWeight() + v1.getWeight()) > max(subVertex.get(lostDistrict).getSumWeight() + candidateOtherVertices2.get(i).getWeight() - upper,lower - subVertex.get(candidateOtherVertices2.get(i).getCover()).getSumWeight() + candidateOtherVertices2.get(i).getWeight()))
						v1 = candidateOtherVertices2.get(i);
				}
				if (!(candidateVertices2.isEmpty())) {
					int index = -1;
					for (int j = 0; j < candidateVertices2.size(); j++) {
						if (max(subVertex.get(v1.getCover()).getSumWeight() + v1.getWeight() - upper,lower - subVertex.get(lostDistrict).getSumWeight() + v1.getWeight()) > max(subVertex.get(candidateVertices2.get(j).get(1)).getSumWeight() + this.findVertex(candidateVertices2.get(j).get(0)).getWeight() - upper,lower - subVertex.get(lostDistrict).getSumWeight() + this.findVertex(candidateVertices2.get(j).get(0)).getWeight())) {
							v1 = this.findVertex(candidateVertices2.get(j).get(0));
							index = candidateVertices2.get(j).get(1);
						} 
					}
					if (index != -1) {
						this.findVertex(v1.getId()).setCover(index);
						//lostsDistricts = this.losingDistricts();
						//System.out.println("1:");
						continue;
					} else {
						this.findVertex(v1.getId()).setCover(lostDistrict);
						//lostsDistricts = this.losingDistricts();
						//System.out.println(v1);
						//System.out.println("2:");
						continue;
					}
				} else {
					this.findVertex(v1.getId()).setCover(lostDistrict);
					//lostsDistricts = this.losingDistricts();
					//System.out.println("3:");
					continue;
				}
				//presun v1 do districtu
				//System.out.println(v1);
			} else {
				if (!(candidateVertices2.isEmpty())) {
					v1 = this.findVertex(candidateVertices2.get(0).get(0));
					int index = candidateVertices2.get(0).get(1);
					for (int j = 0; j < candidateVertices2.size(); j++) {
						if (max(subVertex.get(v1.getCover()).getSumWeight() + v1.getWeight() - upper,lower - subVertex.get(lostDistrict).getSumWeight() + v1.getWeight()) > max(subVertex.get(candidateVertices2.get(j).get(1)).getSumWeight() + this.findVertex(candidateVertices2.get(j).get(0)).getWeight() - upper,lower - subVertex.get(lostDistrict).getSumWeight() + this.findVertex(candidateVertices2.get(j).get(0)).getWeight())) {
							v1 = this.findVertex(candidateVertices2.get(j).get(0));
							index = candidateVertices2.get(j).get(1);
						} 
					}
					//presun v1 do districtu
					//System.out.println(v1);
					this.findVertex(v1.getId()).setCover(index);
					//lostsDistricts = this.losingDistricts();
					//System.out.println("4:");
					continue;
				} else {
					//System.out.println("5:");
					//System.out.println(lostsDistricts);
					lostsDistricts.remove(lostDistrict);
					//lostsDistricts = this.losingDistricts();
					//System.out.println(lostsDistricts);
				}
			}
		}
		
		//System.out.println(candidateOtherVertices);
		//System.out.println(candidateOtherVertices2);
		//System.out.println(candidateVertices);
		//System.out.println(candidateVertices2);
		this.rebalancing(upper, lower);
		return this;
	}
	
	private ArrayList<Vertex> getVerticesWithBorderEdge(Stack<Vertex> stack) {
		ArrayList<Vertex> vertices = new ArrayList<Vertex>();
		while (!(stack.isEmpty())) {
			Vertex v = stack.pop();
			if (!(v.getChildVertices().equals(this.findVertex(v.getId()).getChildVertices()))) {
				vertices.add(v);
			}
			
		}
		return vertices;
	}
	
	public boolean rebalancing(Integer upper, Integer lower) {
		Graph graph = this.clone();
		ArrayList<Vertex> vertices = new ArrayList<Vertex>();
		ArrayList<Integer> visitSubGraph = new ArrayList<Integer>();
		while (this.getUnbalancedGraph(this.getSubEdge(this.getSubVertex()), upper, lower) != -1) {
			int i = this.getUnbalancedGraph(this.getSubEdge(this.getSubVertex()), upper, lower);
			HashMap<Integer,Graph> subVertex = this.getSubEdge(this.getSubVertex());
			for (Integer dist : this.getDistricts()) {
				subVertex.get(dist).cutVertices();
			}
			if (subVertex.get(i).getSumWeight() > upper) {
				HashMap<Integer,ArrayList<Integer>> upperCandidateVertices = new HashMap<Integer,ArrayList<Integer>>();
				HashMap<Integer,ArrayList<Integer>> upperBothCondition = new HashMap<Integer,ArrayList<Integer>>();
				HashMap<Integer,ArrayList<Integer>> upperOneCondition = new HashMap<Integer,ArrayList<Integer>>();
				Stack<Vertex> stack = new Stack<Vertex>();
				stack.addAll(subVertex.get(i).getV());
				vertices = getVerticesWithBorderEdge(stack);
				int compute = 0;
				for (Vertex v : vertices) {
					visitSubGraph.clear();
					for (int index = 0; index < this.findVertex(v.getId()).getChildVertices().size(); index++) {
						int cover = this.findVertex(this.findVertex(v.getId()).getChildVertices().get(index)).getCover();
						if ((cover != i) && (!(visitSubGraph.contains(cover))) && v.getCut()) {
							visitSubGraph.add(cover);
							ArrayList<Integer> candidateVertex = new ArrayList<Integer>();
							candidateVertex.add(v.getId());
							candidateVertex.add(cover);
							upperCandidateVertices.put(compute, candidateVertex);
							compute = compute + 1;
						}
					}
				}
				int indexBoth = 0;
				int indexOne = 0;
				for (int f = 0; f < upperCandidateVertices.size(); f++) {
					Vertex v = this.findVertex(upperCandidateVertices.get(f).get(0));
					Integer cover = upperCandidateVertices.get(f).get(1);
					if (subVertex.get(cover).getSumWeight() + v.getWeight() < subVertex.get(i).getSumWeight() ) {
						if (((subVertex.get(i).getSumEvaluation() > 0) && (subVertex.get(cover).getSumEvaluation() > 0) && (v.getEvaluation() < subVertex.get(i).getSumEvaluation()) && (v.getEvaluation() > - subVertex.get(cover).getSumEvaluation())) 
								|| ((subVertex.get(i).getSumEvaluation() > 0) && (subVertex.get(cover).getSumEvaluation() <= 0) && (v.getEvaluation() < subVertex.get(i).getSumEvaluation())) 
								|| ((subVertex.get(i).getSumEvaluation() > 0) && (subVertex.get(cover).getSumEvaluation() <= 0) && (v.getEvaluation() > -subVertex.get(cover).getSumEvaluation())) 
								|| ((subVertex.get(i).getSumEvaluation() <= 0) && (subVertex.get(cover).getSumEvaluation() > 0) && (v.getEvaluation() > -subVertex.get(cover).getSumEvaluation())) 
								|| ((subVertex.get(i).getSumEvaluation() <= 0) && (subVertex.get(cover).getSumEvaluation() > 0) && (v.getEvaluation() < subVertex.get(i).getSumEvaluation())) 
								|| ((subVertex.get(i).getSumEvaluation() <= 0) && (subVertex.get(cover).getSumEvaluation() <= 0))) 
						{
							ArrayList<Integer> upperBothConditionArray = new ArrayList<Integer>();
							upperBothConditionArray.add(v.getId());
							upperBothConditionArray.add(cover);
							upperBothCondition.put(indexBoth,upperBothConditionArray);
							indexBoth = indexBoth + 1;
						} else {
							ArrayList<Integer> upperOneConditionArray = new ArrayList<Integer>();
							upperOneConditionArray.add(v.getId());
							upperOneConditionArray.add(cover);
							upperOneCondition.put(indexOne,upperOneConditionArray);
							indexOne = indexOne + 1;
						}
					} else {
						if (((subVertex.get(i).getSumEvaluation() > 0) && (subVertex.get(cover).getSumEvaluation() > 0) && (v.getEvaluation() < subVertex.get(i).getSumEvaluation()) && (v.getEvaluation() > - subVertex.get(cover).getSumEvaluation())) 
								|| ((subVertex.get(i).getSumEvaluation() > 0) && (subVertex.get(cover).getSumEvaluation() <= 0) && (v.getEvaluation() < subVertex.get(i).getSumEvaluation())) 
								|| ((subVertex.get(i).getSumEvaluation() > 0) && (subVertex.get(cover).getSumEvaluation() <= 0) && (v.getEvaluation() > - subVertex.get(cover).getSumEvaluation())) 
								|| ((subVertex.get(i).getSumEvaluation() <= 0) && (subVertex.get(cover).getSumEvaluation() > 0) && (v.getEvaluation() > - subVertex.get(cover).getSumEvaluation())) 
								|| ((subVertex.get(i).getSumEvaluation() <= 0) && (subVertex.get(cover).getSumEvaluation() > 0) && (v.getEvaluation() < subVertex.get(i).getSumEvaluation())) 
								|| ((subVertex.get(i).getSumEvaluation() <= 0) && (subVertex.get(cover).getSumEvaluation() <= 0))) 
						{
							ArrayList<Integer> upperOneConditionArray = new ArrayList<Integer>();
							upperOneConditionArray.add(v.getId());
							upperOneConditionArray.add(cover);
							upperOneCondition.put(indexOne,upperOneConditionArray);
							indexOne = indexOne + 1;
						}
					}
				}
				if (!(upperBothCondition.isEmpty())) {
					Vertex v = this.findVertex(upperBothCondition.get(0).get(0));
					int cover = upperBothCondition.get(0).get(1);
					for (int j = 1; j < upperBothCondition.size(); j++) {
						Vertex v2 = this.findVertex(upperBothCondition.get(j).get(0));
						if (max(subVertex.get(i).getSumWeight() - v.getWeight() - upper, subVertex.get(cover).getSumWeight() + v.getWeight() - upper) > max(subVertex.get(i).getSumWeight() - v2.getWeight() - upper, subVertex.get(upperBothCondition.get(j).get(1)).getSumWeight() + v2.getWeight() - upper)) {
							v = v2;
							cover = upperBothCondition.get(j).get(1);
						}
					}
					v.setCover(cover);
				} else {
					if (!(upperOneCondition.isEmpty())) {
						Vertex v = this.findVertex(upperOneCondition.get(0).get(0));
						int cover = upperOneCondition.get(0).get(1);
						for (int j = 1; j < upperOneCondition.size(); j++) {
							Vertex v2 = this.findVertex(upperOneCondition.get(j).get(0));
							if (max(subVertex.get(i).getSumWeight() - v.getWeight() - upper, subVertex.get(cover).getSumWeight() + v.getWeight() - upper) > max(subVertex.get(i).getSumWeight() - v2.getWeight() - upper, subVertex.get(upperOneCondition.get(j).get(1)).getSumWeight() + v2.getWeight() - upper)) {
								v = v2;
								cover = upperOneCondition.get(j).get(1);
							}
						}
						v.setCover(cover);
					} else {
						this.E = graph.E;
						this.V = graph.V;
						this.id = graph.id;
						return false;
					}
				}
			}
			
			if(subVertex.get(i).getSumWeight() < lower) {
				ArrayList<Vertex> candidateVertices = new ArrayList<Vertex>();
				ArrayList<Vertex> bothConditions = new ArrayList<Vertex>();
				ArrayList<Vertex> oneCondition = new ArrayList<Vertex>();
				Stack<Vertex> stack = new Stack<Vertex>();
				stack.addAll(subVertex.get(i).getV());
				vertices = getVerticesWithBorderEdge(stack);
				visitSubGraph.clear();
				for (Vertex v : vertices) {
					for (int index = 0; index < this.findVertex(v.getId()).getChildVertices().size(); index++) {
						Vertex vertex = this.findVertex(this.findVertex(v.getId()).getChildVertices().get(index));
						vertex = subVertex.get(vertex.getCover()).findVertex(vertex.getId());
						int cover = vertex.getCover();
						if ((cover != i) && (!(visitSubGraph.contains(vertex.getId()))) && vertex.getCut()) {
							visitSubGraph.add(vertex.getId());
							candidateVertices.add(vertex);
						}
					}
				}
				for (int f = 0; f < candidateVertices.size(); f++) {
					if (subVertex.get(candidateVertices.get(f).getCover()).getSumWeight() - candidateVertices.get(f).getWeight() > subVertex.get(i).getSumWeight()) {
						if (((subVertex.get(i).getSumEvaluation() > 0) && (subVertex.get(candidateVertices.get(f).getCover()).getSumEvaluation() > 0) && (candidateVertices.get(f).getEvaluation() < subVertex.get(candidateVertices.get(f).getCover()).getSumEvaluation()) && (candidateVertices.get(f).getEvaluation() > -subVertex.get(i).getSumEvaluation()) 
								|| ((subVertex.get(i).getSumEvaluation() > 0) && (subVertex.get(candidateVertices.get(f).getCover()).getSumEvaluation() <= 0) && (candidateVertices.get(f).getEvaluation() < subVertex.get(candidateVertices.get(f).getCover()).getSumEvaluation())) 
								|| ((subVertex.get(i).getSumEvaluation() > 0) && (subVertex.get(candidateVertices.get(f).getCover()).getSumEvaluation() <= 0) && (candidateVertices.get(f).getEvaluation() > -subVertex.get(i).getSumEvaluation())) 
								|| ((subVertex.get(i).getSumEvaluation() <= 0) && (subVertex.get(candidateVertices.get(f).getCover()).getSumEvaluation() > 0) && (candidateVertices.get(f).getEvaluation() > -subVertex.get(i).getSumEvaluation())) 
								|| ((subVertex.get(i).getSumEvaluation() <= 0) && (subVertex.get(candidateVertices.get(f).getCover()).getSumEvaluation() > 0) && (candidateVertices.get(f).getEvaluation() < subVertex.get(candidateVertices.get(f).getCover()).getSumEvaluation()))) 
								|| ((subVertex.get(i).getSumEvaluation() <= 0) && (subVertex.get(candidateVertices.get(f).getCover()).getSumEvaluation() <= 0))) 
						{
							bothConditions.add(candidateVertices.get(f));	
						} else {
							oneCondition.add(candidateVertices.get(f));
						}
					} else {
						if ((       (subVertex.get(i).getSumEvaluation() > 0) && (subVertex.get(candidateVertices.get(f).getCover()).getSumEvaluation() > 0) && (candidateVertices.get(f).getEvaluation() < subVertex.get(candidateVertices.get(f).getCover()).getSumEvaluation()) && (candidateVertices.get(f).getEvaluation() > -subVertex.get(i).getSumEvaluation()) 
								|| ((subVertex.get(i).getSumEvaluation() > 0) && (subVertex.get(candidateVertices.get(f).getCover()).getSumEvaluation() <= 0) && (candidateVertices.get(f).getEvaluation() < subVertex.get(candidateVertices.get(f).getCover()).getSumEvaluation())) 
								|| ((subVertex.get(i).getSumEvaluation() > 0) && (subVertex.get(candidateVertices.get(f).getCover()).getSumEvaluation() <= 0) && (candidateVertices.get(f).getEvaluation() > -subVertex.get(i).getSumEvaluation())) 
								|| ((subVertex.get(i).getSumEvaluation() <= 0) && (subVertex.get(candidateVertices.get(f).getCover()).getSumEvaluation() > 0) && (candidateVertices.get(f).getEvaluation() > -subVertex.get(i).getSumEvaluation())) 
								|| ((subVertex.get(i).getSumEvaluation() <= 0) && (subVertex.get(candidateVertices.get(f).getCover()).getSumEvaluation() > 0) && (candidateVertices.get(f).getEvaluation() < subVertex.get(candidateVertices.get(f).getCover()).getSumEvaluation()))) 
								|| ((subVertex.get(i).getSumEvaluation() <= 0) && (subVertex.get(candidateVertices.get(f).getCover()).getSumEvaluation() <= 0))) 
						{
							oneCondition.add(candidateVertices.get(f));
						}
					}
				}
				if (!(bothConditions.isEmpty())) {
					Vertex v = bothConditions.get(0);
					for (int j = 1; j < bothConditions.size(); j++) {
						if (max(lower - subVertex.get(v.getCover()).getSumWeight() + v.getWeight(),lower - subVertex.get(i).getSumWeight() - v.getWeight()) > max(lower - subVertex.get(bothConditions.get(j).getCover()).getSumWeight() + bothConditions.get(j).getWeight(),lower - subVertex.get(i).getSumWeight() - bothConditions.get(j).getWeight())) {
							v = bothConditions.get(j);
						}
					}
					v = this.findVertex(v.getId());
					v.setCover(i);
				} else {
					if (!(oneCondition.isEmpty())) {
						Vertex v = oneCondition.get(0);
						for (int j = 1; j < oneCondition.size(); j++) {
							if (max(lower - subVertex.get(v.getCover()).getSumWeight() + v.getWeight(),lower - subVertex.get(i).getSumWeight() - v.getWeight()) > max(lower - subVertex.get(oneCondition.get(j).getCover()).getSumWeight() + oneCondition.get(j).getWeight(),lower - subVertex.get(i).getSumWeight() - oneCondition.get(j).getWeight())) {
								v = oneCondition.get(j);
							}
						}
						v = this.findVertex(v.getId());
						v.setCover(i);
					} else {
						this.E = graph.E;
						this.V = graph.V;
						this.id = graph.id;
						return false;
					}
				}
			}
		}
		return true;
	}
	

	public String toString() {
		return this.id;
	}
	
	
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj instanceof Graph) {
            Graph v = (Graph) obj;
            return v.equals(id);
        }
        return false;
    }
    
    public int hashCode() {
        int hash = 17;
        hash = 37 * hash + id.hashCode();
        return hash;
    }
}

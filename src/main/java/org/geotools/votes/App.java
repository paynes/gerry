package org.geotools.votes;

import java.awt.SystemColor;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.geotools.data.CachingFeatureSource;
import org.geotools.data.FeatureSource;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.map.DefaultMapContext;
import org.geotools.map.MapContext;
import org.geotools.swing.JMapFrame;
import org.geotools.swing.data.JFileDataStoreChooser;
import org.opengis.feature.simple.SimpleFeature;

import com.vividsolutions.jts.geom.Geometry;

public class App {
	
	public static Graph loadVertex(Object[] objs) {
		Graph dual = new Graph("Dual");
		for (int i = 0; i < objs.length; i++) {
			SimpleFeature f1 = (SimpleFeature) objs[i];
			Vertex v1 = dual.findVertex(Integer.parseInt(f1.getAttribute(1).toString()));
			Integer id = Integer.parseInt(f1.getAttribute(1).toString());
			//System.out.println(id);
			if (v1 == null) {
				v1 = new Vertex(id);
				//System.out.println(v1);
			    dual.addVertex(v1);			   
			}    
		}
		return dual;
	}
	
	public static Graph assignResults(Graph dual, HashMap<Integer, Integer[]> results) {
		//ArrayList<Vertex> vertexs = new ArrayList<Vertex>();
		for (Vertex v : dual.getV()) {
			if (results.get(v.getId()) == null) {
				v.setRepRes(0);
				v.setDemRes(0);
				//dual.removeVertex(v);
				//vertexs.add(v);
			} else {
				v.setRepRes(results.get(v.getId())[0]);
				v.setDemRes(results.get(v.getId())[1]);
			}
		}	
		/*for (int i = 0; i < vertexs.size(); i++) {
			Vertex v = vertexs.get(i); 
			dual.removeVertex(v);
		}*/
		return dual;
	}
	
	public static Graph createEdges(Graph dual,Object[] objs) {
		for (int i = 0; i < objs.length;i++) {
			SimpleFeature f1= (SimpleFeature) objs[i];
			Geometry g1 = (Geometry) f1.getDefaultGeometry();
			for (int j = 0; j < objs.length; j++) {
				SimpleFeature f2= (SimpleFeature) objs[j];
				Geometry g2 = (Geometry) f2.getDefaultGeometry();
				if (g1.intersection(g2).getNumPoints() > 0) {
					if ((dual.findVertex(Integer.parseInt(f1.getAttribute(1).toString())) != null) &&(dual.findVertex(Integer.parseInt(f1.getAttribute(1).toString())) != null)) {
						if (dual.findVertex(Integer.parseInt(f1.getAttribute(1).toString())) != dual.findVertex(Integer.parseInt(f2.getAttribute(1).toString()))) {
							Edge e = new Edge(dual.findVertex(Integer.parseInt(f1.getAttribute(1).toString())),dual.findVertex(Integer.parseInt(f2.getAttribute(1).toString())));
							dual.findVertex(Integer.parseInt(f1.getAttribute(1).toString())).addChild((Integer.parseInt(f2.getAttribute(1).toString())));
							dual.addEdge(e);
						}	
					}
				}	
			}
		}
		return dual;
	}
	
	public static Graph setCover(Graph dual,HashMap<Integer, Integer> cover) {
		for(Vertex v : dual.getV()) {
			v.setCover(cover.get(v.getId()));
		}
		return dual;
	}


	/*public static Graph constructDual(Object[] objs, HashMap<Integer, Integer[]> results, HashMap<Integer, Integer> cover) {

		System.out.println("Dual graph construction...");
		Graph dual = new Graph("Dual");
		for (int i = 0; i < objs.length; i++) {
			SimpleFeature f1 = (SimpleFeature) objs[i];
			Geometry g1 = (Geometry) f1.getDefaultGeometry();
			//System.out.println(g1.getDimension());
			Vertex v1 = dual.findVertex(Integer.parseInt(f1.getAttribute(1).toString()));
			Integer id = Integer.parseInt(f1.getAttribute(1).toString());
			if (v1 == null) { 
			    if (results.get(id) == null){
			    	v1 = new Vertex(id, -1, -1);
			    	dual.addVertex(v1);	    	
			    } else {
			    	v1 = new Vertex(id, results.get(id)[0], results.get(id)[1]);
			    	dual.addVertex(v1);
			    }	
			}
            for (int j = i + 1; j < objs.length -1; j++) {            	           
            	SimpleFeature f2 = (SimpleFeature) objs[j];            	            		                
                Geometry g2 = (Geometry) f2.getDefaultGeometry();
                //System.out.println(g2.getDimension());
                Vertex v2 = dual.findVertex(Integer.parseInt(f2.getAttribute(1).toString()));
    			if (v2 == null) {
    				id = Integer.parseInt(f1.getAttribute(1).toString()); 
    			    if (results.get(id) == null) {
    			    	v2 = new Vertex(id, -1, -1);
    			    	dual.addVertex(new Vertex(id, -1, -1));
    			    	//v2 = dual.findVertex(id);
    			    } else {
    			    	v2 = new Vertex(id, results.get(id)[0], results.get(id)[1]);
    			    	dual.addVertex(v2);
    			    	//v2 = dual.findVertex(id);
    			    }	
    			}
    			//if (g1.intersection(g2).getDimension() == 1)
       				System.out.printf("%d,%d,%d\n",v1.getId(),v2.getId(),g2.intersection(g1).getNumPoints());
    				//System.out.printf("-%d,%d",v1.getId(),v2.getId() );
    				//System.out.println(v2);
                	//dual.addEdge(new Edge(v1, v2)); 
            }
		}
		SimpleFeature fh = (SimpleFeature) objs[0];
		SimpleFeature fh2 = (SimpleFeature) objs[3];
		Geometry gh = (Geometry) fh.getDefaultGeometry();
		Geometry gh2 = (Geometry) fh2.getDefaultGeometry();
		//System.out.printf("%d,%d,%d\n",Integer.parseInt(fh.getAttribute(1).toString()),Integer.parseInt(fh2.getAttribute(1).toString()),gh.intersection(gh2).getNumPoints());
		//System.out.println(dual.getE().toString());
		return dual;
	}*/
	
	public static HashMap<Integer, Integer[]> getVotesResults(File file) throws IOException {

		BufferedReader fh = new BufferedReader(new FileReader(file));
		int rep_idx = -1, dem_idx = -1, id_idx = -1;        
        String header[] = fh.readLine().split("\t");
        for (int i = 0; i < header.length; i++) 
        	if (header[i].equals("G84H_RV")) 
        		rep_idx = i;
            else if (header[i].equals("G84H_DV"))
            	dem_idx = i;
            else if (header[i].equals("MCDGRP"))
            	id_idx = i;      	
        HashMap<Integer, Integer[]> votesResults = new HashMap<Integer, Integer[]>();
        String s;
        int count = 0;
        while ((s = fh.readLine())!=null){
            String line[] = s.split("\t");
            if (line[id_idx].equals("NA") || line[rep_idx].equals("NA") || line[dem_idx].equals("NA"))
            	continue;
            Integer results[] = { Integer.parseInt(line[rep_idx]), Integer.parseInt(line[dem_idx]) };
            votesResults.put(fixInt(line[id_idx],count), results);
            count = fixInt(line[id_idx],count);
        }
        fh.close();
        return votesResults;
	}
	
	public static HashMap<Integer, Integer> getCoverRelation(Object[] objsA, Object[] objsB) {
    	HashMap<Integer, Geometry> intersectionRelation = new HashMap<Integer, Geometry>();
    	HashMap<Integer, Integer> coverRelation = new HashMap<Integer, Integer>();
		for (int i = 0; i < objsA.length; i++) {
			SimpleFeature fA = (SimpleFeature) objsA[i];
			Geometry gA = (Geometry) fA.getDefaultGeometry();
			intersectionRelation.put(Integer.parseInt(fA.getAttribute(1).toString()), null);
			coverRelation.put(Integer.parseInt(fA.getAttribute(1).toString()), -1);
			for (int j = 0; j < objsB.length; j++) {								
				SimpleFeature fB = (SimpleFeature) objsB[j];            				            	            		                
                Geometry gB = (Geometry) fB.getDefaultGeometry();
                if (gB.intersects(gA)) {
                	Geometry intersection = gB.intersection(gA);                
                	Geometry last = intersectionRelation.get(Integer.parseInt(fA.getAttribute(1).toString()));                	
                	if (last == null || (last != null && intersection.getArea() > last.getArea())) {
            			intersectionRelation.put(Integer.parseInt(fA.getAttribute(1).toString()), intersection);
            			coverRelation.put(Integer.parseInt(fA.getAttribute(1).toString()), Integer.parseInt(fB.getAttribute(2).toString()));
                	}
                }
            }
		}
		return coverRelation;
	}
	
	private static int fixInt(String index, int count) {
		int intIndex = 0;
		//System.out.println(index);
		if (Integer.parseInt(index)%30 == 0) {
			intIndex = Integer.parseInt(index) / 30;
		} else {
			intIndex = Integer.parseInt(index);
		}	
		//System.out.println(intIndex);
		return intIndex;
	}
	
	
    public static void main(String[] args) throws Exception {
    	

    	// Open shapefile with mcdgrps.
//    	File fileA = JFileDataStoreChooser.showOpenFile("shp", null);
    	File fileA = new File("/home/paynes/new jersey/mapa kvazi zup/sumnj.shp");
    	//File fileA = new File("C:/Users/Payne/Downloads/jozo/new jersey/mapa kvazi zup/sumnj.shp");
    	//File fileA = new File("C:/Users/Payne/Downloads/jozo/mississippi/mapa kvazi zup/summs.shp");
    	//File fileA = new File("c:/Users/Payne/Downloads/jozo/ohio/mapa kvazi zup/sumoh.shp");
    	FileDataStore storeA = FileDataStoreFinder.getDataStore(fileA);
        Object[] objsA = storeA.getFeatureSource().getFeatures().toArray();
        
    	// Open shapefile with "volebne obvody".
//    	File fileB = JFileDataStoreChooser.showOpenFile("shp", null);
    	File fileB = new File("/home/paynes/new jersey/mapa volebnych obvodov/tl_2009_34_cd108.shp");
    	//File fileB = new File("C:/Users/Payne/Downloads/jozo/new jersey/mapa volebnych obvodov/tl_2009_34_cd108.shp");
    	//File fileB = new File("C:/Users/Payne/Downloads/jozo/mississippi/mapa volebnych obvodov/tl_2009_28_cd108.shp");
    	//File fileB = new File("C:/Users/Payne/Downloads/jozo/ohio/mapa volebnych obvodov/tl_2009_39_cd108.shp");
    	FileDataStore storeB = FileDataStoreFinder.getDataStore(fileB);
        Object[] objsB = storeB.getFeatureSource().getFeatures().toArray();   	    

    	// Open tab file with election results.
    	//File fileC = JFileDataStoreChooser.showOpenFile("tab", null);
        File fileC = new File("/home/paynes/new jersey/volebne vysledky/mg_nj.tab");
        //File fileC = new File("C:/Users/Payne/Downloads/jozo/new jersey/volebne vysledky/mg_nj.tab");
        //File fileC = new File("C:/Users/Payne/Downloads/jozo/mississippi/volebne vysledky/mg_ms.tab");
        //File fileC = new File("C:/Users/Payne/Downloads/jozo/ohio/volebne vysledky/mg_oh.tab");
        
    	// Get election results.
    	HashMap<Integer, Integer[]> results = getVotesResults(fileC);    	

        // Get mcdgrp <-> "volebny obvod" relationship, prve cislo je MCDGRP a druhe ID volebneho obvodu.
        HashMap<Integer, Integer> coverRelation = getCoverRelation(objsA, objsB);

        // Dual graph construction.
        System.out.println("Dual graph construction");
        //Graph dual = (assignResults(loadVertex(objsA),results));
        //Graph dual = createEdges(assignResults(loadVertex(objsA),results),objsA);
        Graph dual = setCover(createEdges(assignResults(loadVertex(objsA),results),objsA),coverRelation);
        //dual.removeVertex(dual.findVertex(20));
        HashMap<Integer,Graph> subVertex = dual.getSubEdge(dual.getSubVertex());
        //Graph graph = dual.clone();
        //System.out.println(graph.getV().get(0));
        //dual.removeVertex(dual.findVertex(1));
        //System.out.println(graph.getV().get(0));
        //System.out.println(dual.getV().get(0));
        
        //dual.repairUnconnectedGraph(subVertex);
        //subVertex = dual.getSubEdge(dual.getSubVertex());
/*        subVertex.get(1).cutVertices();
        subVertex.get(2).cutVertices();
        subVertex.get(3).cutVertices();
        subVertex.get(4).cutVertices();
        subVertex.get(5).cutVertices();
        subVertex.get(6).cutVertices();
        subVertex.get(7).cutVertices();
        subVertex.get(8).cutVertices();
        subVertex.get(9).cutVertices();
        subVertex.get(10).cutVertices();
        subVertex.get(11).cutVertices();
        subVertex.get(12).cutVertices();
        subVertex.get(13).cutVertices();*/
        //dual.getSubEdge(dual.getSubVertex()).get(1).cutVertices();
        //dual.removeVertex(dual.findVertex(20));
        //dual.getSubEdge(dual.getSubVertex()).get(2).cutVertices();
        //System.out.println(subVertex.get(2).findVertex(2).getChildVertices());
        //System.out.println(subVertex.get(2).getV());
        //dual.getSubEdge(dual.getSubVertex()).get(3).cutVertices();
        //dual.cutVertices();
        //System.out.println(subVertex.get(3).getV().get(11).getChildVertices());
        //dual.removeVertex(dual.findVertex(24));
        //dual.removeVertex(dual.findVertex(22));
        //dual.removeVertex(dual.findVertex(21));
        //subVertex = dual.getSubEdge(dual.getSubVertex());
        //dual.repairUnconnectedGraph(subVertex);
        /*System.out.println(subVertex.get(1).getId());
        System.out.println(subVertex.get(1).getSumEvaluation());
        System.out.println(subVertex.get(1).getSumWeight());
        System.out.println(subVertex.get(1).getV());
        System.out.println(subVertex.get(2).getId());
        System.out.println(subVertex.get(2).getSumEvaluation());
        System.out.println(subVertex.get(2).getSumWeight());
        System.out.println(subVertex.get(2).getV());
        System.out.println(subVertex.get(3).getId());
        System.out.println(subVertex.get(3).getSumEvaluation());
        System.out.println(subVertex.get(3).getSumWeight());
        System.out.println(subVertex.get(3).getV());*/
        //System.out.println(subVertex.get(3).getV());
        //System.out.println(subVertex.get(3).getV().get(4).getChildVertices());
        //System.out.println(subVertex.get(1).getConnectedComponents());
        //System.out.println(subVertex.get(2).getConnectedComponents());
        //System.out.println(subVertex.get(3).getConnectedComponents());
        //subVertex.get(3).makeConnectedComponents(dual);
        //subVertex = dual.getSubEdge(dual.getSubVertex());
        //System.out.println(subVertex.get(1).getV());
        //System.out.println(subVertex.get(2).getV());
        //System.out.println(subVertex.get(3).getV());
        //System.out.println(subVertex.get(1).getSumEvaluation());
        //System.out.println(subVertex.get(2).getSumEvaluation());
        //System.out.println(subVertex.get(3).getSumEvaluation());
/*        System.out.println(subVertex.get(1).getSumEvaluation());
        System.out.println(subVertex.get(2).getSumEvaluation());
        System.out.println(subVertex.get(3).getSumEvaluation());
        System.out.println(subVertex.get(4).getSumEvaluation());
        System.out.println(subVertex.get(5).getSumEvaluation());
        System.out.println(subVertex.get(6).getSumEvaluation());
        System.out.println(subVertex.get(7).getSumEvaluation());
        System.out.println(subVertex.get(8).getSumEvaluation());
        System.out.println(subVertex.get(9).getSumEvaluation());
        System.out.println(subVertex.get(10).getSumEvaluation());
        System.out.println(subVertex.get(11).getSumEvaluation());
        System.out.println(subVertex.get(12).getSumEvaluation());
        System.out.println(subVertex.get(13).getSumEvaluation());*/
        //System.out.println(subVertex.get(8).getV());
        //subVertex = dual.getSubEdge(dual.getSubVertex());
        //System.out.println(subVertex.get(1).getConnectedComponents());
        //System.out.println(subVertex.get(2).getConnectedComponents());
        //System.out.println(subVertex.get(3).getConnectedComponents());
        //System.out.println(subVertex.get(4).getConnectedComponents());
        //System.out.println(subVertex.get(5).getConnectedComponents());
        //System.out.println(subVertex.get(6).getConnectedComponents());
        //System.out.println(subVertex.get(7).getConnectedComponents());
        //System.out.println(subVertex.get(8).getConnectedComponents());
        //System.out.println(subVertex.get(9).getConnectedComponents());
        //System.out.println(subVertex.get(10).getConnectedComponents());
        //System.out.println(subVertex.get(11).getConnectedComponents());
        //System.out.println(subVertex.get(12).getConnectedComponents());
        //System.out.println(subVertex.get(13).getConnectedComponents());
        //System.out.println(subVertex.get(1).getId());
        //System.out.println(subVertex.get(1).getSumEvaluation());
        //System.out.println(subVertex.get(1).getSumWeight());
        //System.out.println(subVertex.get(2).getId());
        //System.out.println(subVertex.get(2).getSumEvaluation());
        //System.out.println(subVertex.get(2).getSumWeight());
        //System.out.println(subVertex.get(3).getId());
        //System.out.println(subVertex.get(3).getSumEvaluation());
        //System.out.println(subVertex.get(3).getSumWeight());
        
        
        //dual.gerryMandering(125000,110000);
        //dual.gerryMandering(120000,85000);
        //dual.rebalancing(125000, 110000);
        //dual.gerryMandering(230000,210000);
        //subVertex = dual.getSubEdge(dual.getSubVertex());
        //subVertex.get(10).makeConnectedComponents();
        System.out.println(subVertex.get(1).getId());
        System.out.println(subVertex.get(1).getSumEvaluation());
        System.out.println(subVertex.get(1).getSumWeight());
        System.out.println(subVertex.get(1).getV());
        System.out.println(subVertex.get(2).getId());
        System.out.println(subVertex.get(2).getSumEvaluation());
        System.out.println(subVertex.get(2).getSumWeight());
        System.out.println(subVertex.get(2).getV());
        System.out.println(subVertex.get(3).getId());
        System.out.println(subVertex.get(3).getSumEvaluation());
        System.out.println(subVertex.get(3).getSumWeight());
        System.out.println(subVertex.get(3).getV());
        
        //odtial zakomentuj ak by si skusal new mexico
        //System.out.println(subVertex.get(4).getId());
        //System.out.println(subVertex.get(4).getSumEvaluation());
        //System.out.println(subVertex.get(4).getSumWeight());
        //System.out.println(subVertex.get(4).getV());
        //dual.rebalancing(230000 , 210000);
        //dual.rebalancing(123000, 109249);

        
        dual.rebalancing(125000, 110000);
        //subVertex = dual.getSubEdge(dual.getSubVertex());
/*        System.out.println(subVertex.get(1).getSumEvaluation());
        System.out.println(subVertex.get(2).getSumEvaluation());
        System.out.println(subVertex.get(3).getSumEvaluation());
        System.out.println(subVertex.get(4).getSumEvaluation());
        System.out.println(subVertex.get(5).getSumEvaluation());
        System.out.println(subVertex.get(6).getSumEvaluation());
        System.out.println(subVertex.get(7).getSumEvaluation());
        System.out.println(subVertex.get(8).getSumEvaluation());
        System.out.println(subVertex.get(9).getSumEvaluation());
        System.out.println(subVertex.get(10).getSumEvaluation());
        System.out.println(subVertex.get(11).getSumEvaluation());
        System.out.println(subVertex.get(12).getSumEvaluation());
        System.out.println(subVertex.get(13).getSumEvaluation());*/
        //Vertex v = dual.findVertex(20);
        //dual.removeVertex(v);
        //ArrayList<Edge> edges = new ArrayList<Edge>();
        //edges = dual.findEdges(v);
        //for (Edge e : edges) {
        	//dual.removeEdge(e);
        //}
    	//Graph dual = constructDual(objsA, results, coverRelation);
        System.out.printf("|V|=%d, |E|=%d\n", dual.getV().size(), dual.getE().size()/2);
        //dual.rebalancing(110000, 80000);
        //dual.gerryMandering(100000, 90000);
        
        //gerry na new mexico...odkomentuj ak to budes pouzivat...ked budes chciet obratit vysledky tak chod do Vertex tam najdes dalsi koment
        //dual.gerryMandering(125000, 110000);
        subVertex = dual.getSubEdge(dual.getSubVertex());
        System.out.println(subVertex.get(1).getId());
        System.out.println(subVertex.get(1).getSumEvaluation());
        System.out.println(subVertex.get(1).getSumWeight());
        System.out.println(subVertex.get(1).getV());
        System.out.println(subVertex.get(2).getId());
        System.out.println(subVertex.get(2).getSumEvaluation());
        System.out.println(subVertex.get(2).getSumWeight());
        System.out.println(subVertex.get(2).getV());
        System.out.println(subVertex.get(3).getId());
        System.out.println(subVertex.get(3).getSumEvaluation());
        System.out.println(subVertex.get(3).getSumWeight());
        System.out.println(subVertex.get(3).getV());
        
        //tu tiez zakomentuj keby si skusal new mexico
        //System.out.println(subVertex.get(4).getId());
        //System.out.println(subVertex.get(4).getSumEvaluation());
        //System.out.println(subVertex.get(4).getSumWeight());
        //System.out.println(subVertex.get(4).getV());
        //System.out.println(subVertex.get(1).getSumWeight());
        //System.out.println(subVertex.get(2).getSumWeight());
        //System.out.println(subVertex.get(3).getSumWeight());
        //System.out.println(subVertex.get(4).getSumWeight());
        //System.out.println(subVertex.get(5).getSumWeight());
        //System.out.println(subVertex.get(6).getSumWeight());
        //System.out.println(subVertex.get(7).getSumWeight());
        //System.out.println(subVertex.get(8).getSumWeight());
        //System.out.println(subVertex.get(8).getV());
        //System.out.println(subVertex.get(9).getSumWeight());
        //System.out.println(subVertex.get(10).getSumWeight());
        //System.out.println(subVertex.get(11).getSumWeight());
        //System.out.println(subVertex.get(12).getSumWeight());
        //System.out.println(subVertex.get(13).getSumWeight());
	
		
        // How to draw map, just uncomment :-).
        //MapContext map = new DefaultMapContext();
        //map.addLayer(store.getFeatureSource(), null);
        //JMapFrame.showMap(map);
    }

    public void usingFeatureCaching() throws Exception {
        File file = JFileDataStoreChooser.showOpenFile("shp", null);
        FileDataStore store = FileDataStoreFinder.getDataStore(file);
        FeatureSource featureSource = store.getFeatureSource();
        
        CachingFeatureSource cache = new CachingFeatureSource(featureSource);
        MapContext map = new DefaultMapContext();        
        map.addLayer(cache, null);
        JMapFrame.showMap(map);
    }
}

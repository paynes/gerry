package sk.java.gerry;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

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
import org.geotools.votes.Graph;
import sk.java.gerry.api.IGraph;
import sk.java.gerry.api.IVertex;
import org.geotools.votes.Vertex;

public class App {
    
    private Graph graph;
    
    public App() {
        this.graph = new Graph("Dual");
        //loadVertex(objs);
    }
    
    public IGraph getGraph() {
        return this.graph;
    }
	
    private void loadVertex(Object[] objs) {
        for (int i = 0; i < objs.length; i++) {
            SimpleFeature f1 = (SimpleFeature) objs[i];
            Vertex v1 = new Vertex(Integer.parseInt(f1.getAttribute(2).toString()));
            if (!(this.graph.findVertex(v1))) {
                this.graph.addVertex(v1);			   
            }    
        }
    }
    
    public HashMap<Integer, Integer[]> getVotesResults(File file) throws IOException {
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
    
    public HashMap<Integer, Integer> getCoverRelation(Object[] objsA, Object[] objsB) {
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
	
    private void assignResults(IVertex vertex, HashMap<Integer, Integer[]> results) {
        if (results.get(vertex.getId()) == null) {
            vertex.setRepRes(0);
            vertex.setDemRes(0);
        } else {
            vertex.setRepRes(results.get(vertex.getId())[0]);
            vertex.setDemRes(results.get(vertex.getId())[1]);
        }
    }
	
    public void createGraph(Object[] objs, HashMap<Integer, Integer[]> results, HashMap<Integer, Integer> cover) {
        for (int i = 0; i < objs.length;i++) {
            SimpleFeature f1= (SimpleFeature) objs[i];
            Geometry g1 = (Geometry) f1.getDefaultGeometry();
            IVertex v1 = new Vertex(Integer.parseInt(f1.getAttribute(1).toString()));
            if (this.graph.getVertexById(v1.getId()) == null) {
                setCover(v1,cover);
                assignResults(v1,results);
                this.graph.addVertex(v1);
            } else {
                v1 = this.graph.getVertexById(v1.getId());
            }
            for (int j = i + 1; j < objs.length; j++) {
                SimpleFeature f2= (SimpleFeature) objs[j];
		Geometry g2 = (Geometry) f2.getDefaultGeometry();
		if (g1.intersection(g2).getNumPoints() > 0) {
                    IVertex v2 = new Vertex(Integer.parseInt(f2.getAttribute(1).toString()));
                    if (this.graph.getVertexById(v2.getId()) == null) {
                        setCover(v2,cover);
                        assignResults(v2,results);
                        v2.addNeighbourVertex(v1);
                        v1.addNeighbourVertex(v2);
                        this.graph.addVertex(v2);
                    } else {
                        v2 = this.graph.getVertexById(v2.getId());
                        v2.addNeighbourVertex(v1);
                        v1.addNeighbourVertex(v2);
                    }
		}	
            }
	}
    }
    
    private void setCover(IVertex vertex, HashMap<Integer, Integer> cover) {
        vertex.setCover(cover.get(vertex.getId()));
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
        
        App app = new App();
    	

    	// Open shapefile with mcdgrps.
//    	File fileA = JFileDataStoreChooser.showOpenFile("shp", null);
    	//File fileA = new File("/home/paynes/new mexico/mapa kvazi zup/sumnm.shp");
        File fileA = new File("/home/paynes/new jersey/mapa kvazi zup/sumnj.shp");
    	FileDataStore storeA = FileDataStoreFinder.getDataStore(fileA);
        Object[] objsA = storeA.getFeatureSource().getFeatures().toArray();
        
    	// Open shapefile with "volebne obvody".
//    	File fileB = JFileDataStoreChooser.showOpenFile("shp", null);
    	//File fileB = new File("/home/paynes/new mexico/mapa volebnych obvodov/tl_2009_35_cd108.shp");
        File fileB = new File("/home/paynes/new jersey/mapa volebnych obvodov/tl_2009_34_cd108.shp");
    	FileDataStore storeB = FileDataStoreFinder.getDataStore(fileB);
        Object[] objsB = storeB.getFeatureSource().getFeatures().toArray();   	    

    	// Open tab file with election results.
    	//File fileC = JFileDataStoreChooser.showOpenFile("tab", null);
        //File fileC = new File("/home/paynes/new mexico/volebne vysledky/mg_nm.tab");
        File fileC = new File("/home/paynes/new jersey/volebne vysledky/mg_nj.tab");
        
    	// Get election results.
    	HashMap<Integer, Integer[]> results = app.getVotesResults(fileC);    	

        // Get mcdgrp <-> "volebny obvod" relationship, prve cislo je MCDGRP a druhe ID volebneho obvodu.
        HashMap<Integer, Integer> coverRelation = app.getCoverRelation(objsA, objsB);

        // Dual graph construction.
        System.out.println("Dual graph construction");
        app.createGraph(objsA,results,coverRelation);
        
        
        //IVertex v1 = app.getGraph().getVertexById(21);
        //IVertex v2 = app.getGraph().getVertexById(23);
        //IVertex v0 = app.getGraph().getVertexById(6);
          //      v0.removeNeighbourVertex(v1);
          //      v0.removeNeighbourVertex(v2);
        //app.loadVertex(objsB);
        for (IVertex v : app.getGraph().getV()) {
            System.out.println("id:" + v.getId() + " susedia " + v.getNeighbourVertices());
            //System.out.println(v);
        }
        
        System.out.println(app.getGraph().isGraphConnected());

        
        //System.out.println(app.getGraph().getV().get(0).getNeighbourVertices());
        //Graph dual = (assignResults(loadVertex(objsA),results));
        //Graph dual = createEdges(assignResults(loadVertex(objsA),results),objsA);
        //Graph dual = app.setCover(app.createEdges(app.assignResults(app.loadVertex(objsA),results),objsA),app.coverRelation);
        //dual.removeVertex(dual.findVertex(20));
        //HashMap<Integer,Graph> subVertex = dual.getSubEdge(dual.getSubVertex());
        //Graph graph = dual.clone();
        //System.out.println(graph.getV().get(0));
        //dual.removeVertex(dual.findVertex(1));
        //System.out.println(graph.getV().get(0));
        //System.out.println(dual.getV().get(0));
        
        //dual.repairUnconnectedGraph(subVertex);
        //subVertex = dual.getSubEdge(dual.getSubVertex());

        
        
        
        //dual.gerryMandering(125000,110000);
        //dual.gerryMandering(120000,85000);
        //dual.rebalancing(125000, 110000);
        //dual.gerryMandering(230000,210000);
        //subVertex = dual.getSubEdge(dual.getSubVertex());
        //subVertex.get(10).makeConnectedComponents();
        

        
        //dual.rebalancing(125000, 110000);
        
        
	
		
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

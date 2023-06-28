package it.polito.tdp.itunes.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.alg.util.Pair;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import it.polito.tdp.itunes.db.ItunesDAO;

public class Model {
	
	//per la ricorsione
	private int dimMax;
	private List<Album> setMax;
	
	private Graph<Album, DefaultEdge> grafo;
	
	public void creaGrafo(Double duration) {
		
		this.grafo = new SimpleGraph<>(DefaultEdge.class);
		
		ItunesDAO dao = new ItunesDAO();
		
		Graphs.addAllVertices(this.grafo, dao.getAlbumsWithDuration(duration));
		
		Map<Integer, Album> albumIdMap = new HashMap<>();
		for(Album a: this.grafo.vertexSet()) {
			albumIdMap.put(a.getAlbumId(), a);
		}
		
		List<Pair<Integer, Integer>> archi = dao.getArchi();
		for(Pair<Integer, Integer> arco: archi) {
			if(albumIdMap.containsKey(arco.getFirst()) && albumIdMap.containsKey(arco.getSecond()) && !arco.getFirst().equals(arco.getSecond())) {
				this.grafo.addEdge(albumIdMap.get(arco.getFirst()), albumIdMap.get(arco.getSecond()));
			}
		}
		
		System.out.println("Vertici: "+grafo.vertexSet().size()+"\nArchi: "+grafo.edgeSet().size());
		
	}
	
	public List<Album> getAlbums(){
		List<Album> list = new ArrayList<>(this.grafo.vertexSet());
		Collections.sort(list);
		return list;
	}
	
	public Set<Album> getComponenteConnessa(Album a1){
		
		ConnectivityInspector<Album, DefaultEdge> ci = new ConnectivityInspector<>(this.grafo);
		
		return ci.connectedSetOf(a1);
	}
	
	public Set<Album> ricercaSetMassimo(Album a1, double dTot){
		
		if(a1.getDurata()>dTot) {
			return null;
		}	
		
		List<Album> parziale = new ArrayList<>();	
		//parziale.add(a1);
		
		List<Album> tutti = new ArrayList<>(getComponenteConnessa(a1));
		tutti.remove(a1);
		
		dimMax = 1;
		setMax = new ArrayList<>(parziale);
		
		cerca(parziale, 0, 0.0, dTot-a1.getDurata(), tutti);
		
		Set<Album> risultato = new HashSet<>(setMax);
		risultato.add(a1);
		return risultato;
		
	}
	
	public void cerca(List<Album> parziale, int livello, double durataParziale, double dTot, List<Album> tutti) {
		
		if(parziale.size()>dimMax) {
			dimMax = parziale.size();
			setMax = new ArrayList<>(parziale);
		}
		
		for(Album nuovo: tutti) {
			if((livello==0 || nuovo.getAlbumId()>parziale.get(parziale.size()-1).getAlbumId()) && durataParziale+nuovo.getDurata()<=dTot) {
				parziale.add(nuovo);
				cerca(parziale, livello+1, durataParziale+nuovo.getDurata(), dTot, tutti);
				parziale.remove(parziale.size()-1);
			}
		}
		
	}
	
	
}

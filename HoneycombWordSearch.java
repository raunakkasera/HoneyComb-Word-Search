/**
* The HoneycombWordSearch program implements an application that
* that efficiently finds a set of words in the honeycomb structure. 
*
* @author  Raunak Kasera
* @version 1.0
* @since   11/7/2015 
*/

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

/**
* Vertex class models an abstraction of the honeycomb structure wherein 
* each letter is a vertex in a graph of vertices
*/
class Vertex implements Comparable<Vertex>
{
	public char vname;
	public int layer;
	public int pos;
	public ArrayList<Vertex> nlist;
	public boolean visited;

	/**
	* Compares calling object's layer and position within the layer with 
	* that of the object passed as an argument
	*
	* @param 	v 	vertex in the honey-comb graph, which is an instance 
	*				of the {@link Vertex} class
	* @return		1 if the two vertices are the same, 0 otherwise.
	*/
	@Override
	public int compareTo(Vertex v) {
		boolean isEqual = v.layer == layer && v.pos == pos;
		return isEqual ? 1 : 0;
	}

	/**
	* Constructor of class Vertex allocates a new vertex at a given layer 
	* and position within the later with the passed name.
	*
	* @param	layer 	Layer of the new vertex.
	* @param	pos 	Position within the layer for the new vertex. 
	* @param	vname	Name of the new vertex.
	* @return			Nothing.
	*/
	public Vertex(int layer, int pos, char vname) {
		this.vname = vname;
		this.layer = layer;
		this.pos = pos;
		nlist = new ArrayList<Vertex>();
	}

	/**
	* addNeighbour method adds a vertex to the neightbour list of the calling 
	* vertex instance if it doesn't already exists.
	*
	* @param	v 	Vertex in the honey-comb graph, which is an instance 
	*				of the {@link Vertex} class.
	* @return		Nothing.
	*/
	public void addNeighbor(Vertex v) {
		if(!nlist.contains(v))
			nlist.add(v);
		if(!v.nlist.contains(this))
			v.nlist.add(this);
	}
}

/** 
* This class models the problem statement with operations to find if a list of 
* strings can be found in a honeycomb structure.
*/
public class HoneycombWordSearch {
	
	/**
	* This method checks if a word can be formed starting from a given vertex. 
	* If a given character of the word occurs at the passed vertex or 
	* its neighbours, the method is called recursively with the next character 
	* of the word. It stops till a given character of the word can't 
	* be found around the vertex.
	*
	* @param	v 		Current vertex, which is an instance of the 
	*					{@link Vertex} class.
	* @param	word 	String to be found in the honeycomb structure.
	* @param	index	Current character in the string we are at.
	* @return	boolean	true, if word is found in the honeycomb structure 
	*					starting from current vertex;
	*					false, otherwise.
	*/
	public static boolean canSolve(Vertex v, String word, int index) {
		if(word.length() == index)
			return true;
		char needed = word.charAt(index);
		ArrayList<Vertex> neighbors = v.nlist;
		v.visited = true;

		// Searches the neightbours of the given vertex for a given character 
		// in the word string
		for(int i = 0; i < neighbors.size(); i++)
		{
			Vertex w = neighbors.get(i);
			if(w.vname == needed && w.visited == false && canSolve(w, word, index+1))
			{
				v.visited = false;
				return true;
			}
		}

		v.visited = false;
		return false;
	}
	
	/**
	* This method checks if a given word can be formed starting from any vertex 
	* in the passed list of vertices. 
	*
	* @param	word 	String to be found in the honeycomb structure.
	* @param	vlist	List of vertices to start the search of the given word with.
	* @return	boolean	true, if word is found in the honeycomb structure starting 
	*					from any one of the vertices in the passed list;
	*					false, otherwise.
	*/
	public static boolean canFind(String word, ArrayList<ArrayList<Vertex>> vlist) {
		char firstChar = word.charAt(0);
		
		// Searches the honeycomb structure for the first character of the word string.
		// If found, it attempts to find the remaining characters by using the 
		// canSolve method starting at that vertex.
		for(int i = 0; i < vlist.size(); i++)
		{
			ArrayList<Vertex> clayer = vlist.get(i);

			for(int j = 0; j < clayer.size(); j++)
			{
				Vertex v = clayer.get(j);
				if(v.vname == firstChar)
				{
					if(canSolve(v, word, 1))
						return true;
				}
			}
		}

		return false;
	}

	/** 
	*This method returns the number of vertices in a given layer.
	*
	* @param	n 			Layer number, starting with layer 0.
	* @return 	booolean    Returns 1 for layer 0 and 6*n for layer n.
	*/
	public static int getLayerSize(int n) {
		return (n==0) ? 1 : 6*n;
	}

	/** 
	* This method finds the neighbours of a vertex in the same layer, the vertex
	* belongs to, and adds it to the vertex's neighbour list.
	* @param	v 			Current vertex, which is an instance of the 
	*						{@link Vertex} class.
	* @param	vlist		Entire list of vertices of the honeycomb graph to 
	*						make it more generic.
	* @return 				Nothing.
	*/
	public static void getCurrNeighbors(Vertex v, ArrayList<ArrayList<Vertex>> vlist) {
		int layer = v.layer;
		int pos = v.pos;
		ArrayList<Vertex> clayer = vlist.get(layer);
		int lsize = getLayerSize(layer);
		int j = (pos - 1) % (lsize);
		Vertex n1 = clayer.get((pos + 1) % (lsize));
		v.addNeighbor(n1);
	}

	/** 
	* This method finds the neighbours of a vertex of layer N in the inner layer N-1
	* and adds it to the vertex's neighbour list.
	* @param	v 			Current vertex, which is an instance of the 
	*						{@link Vertex} class.
	* @param	vlist		Entire list of vertices of the honeycomb graph to
	*						make it more generic.
	* @return 				Nothing.
	*/
	public static void getInnerNeighbors(Vertex v, ArrayList<ArrayList<Vertex>> vlist) {
		int layer = v.layer;
		int pos = v.pos;
		int offset = pos % layer;
		if(offset == 0) {
			ArrayList<Vertex> llayer = vlist.get(layer - 1);
			int npos = (pos / layer)*(layer - 1);
			Vertex neighbor = llayer.get(npos);
			v.addNeighbor(neighbor);
		}
		else {
			ArrayList<Vertex> llayer = vlist.get(layer - 1);
			int npos = ((pos - offset) / layer) * (layer - 1);
			int lnsize = getLayerSize(layer - 1);
			Vertex n1 = llayer.get((npos + offset) % lnsize);
			Vertex n2 = llayer.get(npos + offset - 1);
			v.addNeighbor(n1);
			v.addNeighbor(n2);
		}
	}

	/** 
	* This method finds the neighbours of a vertex of layer N (in the layers N and N-1) 
	* and adds it to the vertex's neighbour list.
	* @param	v 			Current vertex, which is an instance of the 
	*						{@link Vertex} class.
	* @param	vlist		Entire list of vertices of the honeycomb graph to 
	*						make it more generic.
	* @return 				Nothing.
	*/
	public static void getNeighbors(Vertex v, ArrayList<ArrayList<Vertex>> vlist) {
		int layer = v.layer;
		int pos = v.pos;
		int nlayers = vlist.size();
		if(layer == 0) {
			return;
		}
		else {
			getInnerNeighbors(v, vlist);
			getCurrNeighbors(v, vlist);
		}
	}

	/**
	* This is the main method which makes use of the methods of classes HoneycombWordSearch
	* to search for a list of strings in the honeycomb structure.
	* @param		args					Honeycomb and Dictionary input files.
	* @return 								Nothing.
	* @exception 	FileNotFoundException 	Thrown if input files don't exist.
	* @see 			FileNotFoundException
	*/
	public static void main(String[] args) throws FileNotFoundException {
		
		ArrayList<ArrayList<Vertex>> vertices = new ArrayList<ArrayList<Vertex>>();
		ArrayList<String> words = new ArrayList<String>();
		ArrayList<String> solved = new ArrayList<String>();
		File beehive = new File(args[0]);
		Scanner sc = new Scanner(beehive);
		int numLayers = sc.nextInt();
		sc.nextLine();
		String[] layers = new String[numLayers];

		// Builds an array of strings wherein the ith vertex stores the 
		// list of vertices in layer i as a string
		for(int i = 0; i < numLayers; i++) {
			layers[i] = sc.nextLine();
		}

		// Constructs vertices of the honeycomb graph layer by layer
		for(int i = 0; i < numLayers; i++) {
			ArrayList<Vertex> slayer = new ArrayList<Vertex>();
			String clayer = layers[i];
			int csize = getLayerSize(i);
			for(int j = 0; j < csize; j++) {
				Vertex v = new Vertex(i, j, clayer.charAt(j));
				slayer.add(v);
			}
			vertices.add(slayer);
		}

		// Models the edges between the vertices of the honeycomb graph. 
		// An edge u -> v exists if u and v are adjacent to each other.
		for(int i = 0; i < numLayers; i++) {
			int csize = getLayerSize(i);
			ArrayList<Vertex> slayer = vertices.get(i);
			for(int j = 0; j < csize; j++) {
				Vertex v = slayer.get(j);
				getNeighbors(v, vertices);
			}
		}

		File dic = new File(args[1]);
		Scanner snw = new Scanner(dic);

		//Builds a list of words from the  Dictionary input file
		while(snw.hasNextLine()) {
			words.add(snw.nextLine());
		}

		//Checks if a list of strings can be found in the honeycomb graph, word by word
		for(int i = 0; i < words.size(); i++) {
			String cword = words.get(i);
			if (canFind(cword, vertices))
				solved.add(cword);
		}

        //Sorts the list of words found in the honeycomb structure.
		Collections.sort(solved);

		//Prints out the found words
		for(int i = 0; i < solved.size(); i++) {
			System.out.println(solved.get(i));
		}
	}
}

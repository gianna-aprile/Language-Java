package apps;

import structures.*;
import java.util.ArrayList;

public class MST {

	/**
	 * Initializes the algorithm by building single-vertex partial trees
	 * 
	 * @param graph
	 *            Graph for which the MST is to be found
	 * @return The initial partial tree list
	 */

	// steps 1 & 2 in the instructions
	public static PartialTreeList initialize(Graph graph) {

		// Step 1: Create an empty list L of partial trees.
		PartialTreeList L = new PartialTreeList();

		for (Vertex V : graph.vertices) {

			// Create a partial tree T containing only v.
			PartialTree T = new PartialTree(V);

			// Mark v as belonging to T
			 V.parent = T.getRoot();

			// Create a priority queue (heap) P
			MinHeap<PartialTree.Arc> P = new MinHeap<>();

			// Associate it with T.
			P = T.getArcs();

			for (Vertex.Neighbor neigh = V.neighbors; neigh != null; neigh = neigh.next) {

				PartialTree.Arc currArc = new PartialTree.Arc(V, neigh.vertex, neigh.weight);

				// Insert all of the arcs (edges) connected to v into P
				P.insert(currArc);

			}

			// Add the partial tree T to the partial tree list L.
			L.append(T);
		}

		return L;
	}

	/**
	 * Executes the algorithm on a graph, starting with the initial partial tree
	 * list
	 * 
	 * @param ptlist
	 *            Initial partial tree list
	 * @return Array list of all arcs that are in the MST - sequence of arcs is
	 *         irrelevant
	 */

	public static ArrayList<PartialTree.Arc> execute(PartialTreeList ptlist) {

		ArrayList<PartialTree.Arc> ret = new ArrayList<>();
		PartialTreeList L = ptlist;
		
		while (L.size() > 1) {

			// Remove the first partial tree PTX from ptList.
			PartialTree PTX = L.remove();

			// Let PQX be PTX's priority queue.
			MinHeap<PartialTree.Arc> PQX = PTX.getArcs();

			// Remove the highest-priority arc from PQX. Say this arc is α.
			PartialTree.Arc a = PQX.deleteMin();

		
			// If v2 also belongs to PTX, go back to Step 4 and pick the next highest priority arc,
			// otherwise continue to the next step.
			while( PTX.getRoot().equals(a.v2.getRoot())) {
				
				a = PQX.deleteMin();
			}
			
			// Report α - this is a component of the minimum spanning tree.
			ret.add(a);
			
			// Find the partial tree PTY to which v2 belongs. Remove PTY from the partial tree list L. 
			PartialTree PTY = L.removeTreeContaining(a.v2.parent);
			
			// Let PQY be PTY's priority queue.
			MinHeap<PartialTree.Arc> PQY = PTY.getArcs();
			
			// Combine PTX and PTY. This includes merging the priority queues PQX and PQY into a single priority queue. 
			
			if( PTY != null ) {
				
				PTY.getRoot().parent = PTX.getRoot();
				//PTX.merge(PTY);
				PQX.merge(PQY);
				L.append(PTX);
			}
			
		}
		return ret;
	}
}

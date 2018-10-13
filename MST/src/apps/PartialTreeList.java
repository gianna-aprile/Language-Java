package apps;

import java.util.Iterator;
import java.util.NoSuchElementException;

import structures.Vertex;

public class PartialTreeList implements Iterable<PartialTree> {

	/**
	 * Inner class - to build the partial tree circular linked list
	 * 
	 */
	public static class Node {
		/**
		 * Partial tree
		 */
		public PartialTree tree;

		/**
		 * Next node in linked list
		 */
		public Node next;

		/**
		 * Initializes this node by setting the tree part to the given tree, and setting
		 * next part to null
		 * 
		 * @param tree
		 *            Partial tree
		 */
		public Node(PartialTree tree) {
			this.tree = tree;
			next = null;
		}
	}

	/**
	 * Pointer to last node of the circular linked list
	 */
	private Node rear;

	/**
	 * Number of nodes in the CLL
	 */
	private int size;

	/**
	 * Initializes this list to empty
	 */
	public PartialTreeList() {
		rear = null;
		size = 0;
	}

	/**
	 * Adds a new tree to the end of the list
	 * 
	 * @param tree
	 *            Tree to be added to the end of the list
	 */
	public void append(PartialTree tree) {
		Node ptr = new Node(tree);
		if (rear == null) {
			ptr.next = ptr;
		} else {
			ptr.next = rear.next;
			rear.next = ptr;
		}
		rear = ptr;
		size++;
	}

	/**
	 * Removes the tree that is at the front of the list.
	 * 
	 * @return The tree that is removed from the front
	 * @throws NoSuchElementException
	 *             If the list is empty
	 */
	public PartialTree remove() throws NoSuchElementException {

		// if the CLL is empty
		if (rear == null) {

			throw new NoSuchElementException();
		}

		// if there's only one Node in the CLL
		if (rear == rear.next) {

			Node temp = rear;
			rear = null;
			size--;
			return temp.tree;
		}

		// if the item is in the CLL, and size > 1
		Node front = rear.next;
		rear.next = front.next;
		size--;

		return front.tree;
	}

	/**
	 * Removes the tree in this list that contains a given vertex.
	 * 
	 * @param vertex
	 *            Vertex whose tree is to be removed
	 * @return The tree that is removed
	 * @throws NoSuchElementException
	 *             If there is no matching tree
	 */
	public PartialTree removeTreeContaining(Vertex vertex) throws NoSuchElementException {

		// if the CLL is empty
		if (rear == null) {

			throw new NoSuchElementException();
		}

		// if there is only one element
//		if( rear == rear.next ) {
//			
//			Node temp = rear;
//			
//			if( temp.tree.getRoot().name.equals(vertex.getRoot().name)) {
//				
//				rear = null;
//				size = 0;
//				return temp.tree;
//			}
//		}
		
		Node prev = rear;
		
		// front of CLL
		Node ptr = rear.next;

		do {

			if( ptr.tree.getRoot().name.equals(vertex.getRoot().name)) {
			// 	
			//	if (ptr.tree.getRoot() == vertex) {
			// 
			// if (ptr.tree.getRoot().name.equals(vertex.getRoot().name)) {

				// edge case: if the tree is the rear of the CLL

					// PartialTree ret = new PartialTree(ptr.tree.getRoot());
					PartialTree temp = ptr.tree;
					prev.next = ptr.next;
					size--;
					return temp;
				
			}
			
			prev = ptr;
			ptr = ptr.next;

		} while (ptr != rear);

		if (ptr == rear && ptr.tree.getRoot().name.equals(vertex.getRoot().name)) {
			
			// PartialTree ret = new PartialTree(ptr.tree.getRoot());
			PartialTree temp = ptr.tree;
			rear = ptr.next;
			prev.next = rear;
			size--;
			return temp;
			
		}
		
		// there is no matching tree
		else if (ptr == rear && ptr.tree.getRoot().name.equals(vertex.getRoot().name)) {

			throw new NoSuchElementException();
		}

		return null;
	}

	/**
	 * Gives the number of trees in this list
	 * 
	 * @return Number of trees
	 */
	public int size() {
		return size;
	}

	/**
	 * Returns an Iterator that can be used to step through the trees in this list.
	 * The iterator does NOT support remove.
	 * 
	 * @return Iterator for this list
	 */
	public Iterator<PartialTree> iterator() {
		return new PartialTreeListIterator(this);
	}

	private class PartialTreeListIterator implements Iterator<PartialTree> {

		private PartialTreeList.Node ptr;
		private int rest;

		public PartialTreeListIterator(PartialTreeList target) {
			rest = target.size;
			ptr = rest > 0 ? target.rear.next : null;
		}

		public PartialTree next() throws NoSuchElementException {
			if (rest <= 0) {
				throw new NoSuchElementException();
			}
			PartialTree ret = ptr.tree;
			ptr = ptr.next;
			rest--;
			return ret;
		}

		public boolean hasNext() {
			return rest != 0;
		}

		public void remove() throws UnsupportedOperationException {
			throw new UnsupportedOperationException();
		}

	}
}

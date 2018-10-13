package structures;

import java.util.*;

/**
 * This class implements an HTML DOM Tree. Each node of the tree is a TagNode,
 * with fields for tag/text, first child and sibling.
 * 
 */
public class Tree {

	/**
	 * Root node
	 */
	TagNode root = null;

	/**
	 * Scanner used to read input HTML file when building the tree
	 */
	Scanner sc;

	/**
	 * Initializes this tree object with scanner for input HTML file
	 * 
	 * @param sc
	 *            Scanner for input HTML file
	 */
	public Tree(Scanner sc) {
		this.sc = sc;
		root = null;
	}

	/**
	 * Builds the DOM tree from input HTML file, through scanner passed in to the
	 * constructor and stored in the sc field of this object.
	 * 
	 * The root of the tree that is built is referenced by the root field of this
	 * object.
	 */
	public void build() {

		Stack<TagNode> tag = new Stack<TagNode>();

		String str = "";

		while (sc.hasNextLine()) {

			str = sc.nextLine();

			if (root == null) {

				root = new TagNode(str.substring(1, str.length() - 1), null, null);
				tag.push(root);
			}

			// opening tag Ex: <html>
			else if (str.charAt(0) == '<' && str.charAt(1) != '/') {

				TagNode temp = new TagNode(str.substring(1, str.length() - 1), null, null);

				if (!tag.isEmpty()) {

					TagNode parent = tag.pop();
					// if parent doesn't have a child make temp it's child
					if (parent.firstChild == null) {

						parent.firstChild = temp;
						tag.push(parent);
						// push temp bc temp is a tag
						tag.push(temp);

					} else {
						// put parent back on the stack bc we need its child instead
						tag.push(parent);
						// ptr is parent's child
						TagNode ptr = tag.peek().firstChild;

						if (!tag.isEmpty()) {

							while (ptr.sibling != null) {
								// find child's sibling that is null to insert
								ptr = ptr.sibling;
							}
							ptr.sibling = temp;
							tag.push(temp);

						}
					}
				}

				// closing tag Ex: </html>
			} else if (str.charAt(0) == '<' && str.charAt(1) == '/') {

				if (!tag.isEmpty()) {
					// take out the corresponding opening tag
					tag.pop();
				}

				// words -- no tags
			} else if (str.charAt(0) != '<') {

				TagNode temp = new TagNode(str, null, null);

				if (!tag.isEmpty()) {

					TagNode parent = tag.pop();

					if (parent.firstChild == null) {

						parent.firstChild = temp;
						tag.push(parent);

					} else {

						tag.push(parent);

						if (!tag.isEmpty()) {

							TagNode ptr = tag.peek().firstChild;

							while (ptr.sibling != null) {

								ptr = ptr.sibling;
							}
							// do NOT push temp bc it isn't a tag
							ptr.sibling = temp;
						}

					}
				}
			}
		}

	}

	/**
	 * Replaces all occurrences of an old tag in the DOM tree with a new tag
	 * 
	 * @param oldTag
	 *            Old tag
	 * @param newTag
	 *            Replacement tag
	 */
	public void replaceTag(String oldTag, String newTag) {

		replace(oldTag, newTag, root);

	}

	// helper method for replaceTag
	private void replace(String oldTag, String newTag, TagNode root) {

		// iterate through the tree by moving ptr to its sibling
		for (TagNode ptr = root; ptr != null; ptr = ptr.sibling) {

			if (root == null) {

				return;
			}
			// switch tag name if ptr is the target
			if (ptr.tag.equals(oldTag) && ptr.firstChild != null) {

				ptr.tag = newTag;
			}

			if (ptr.firstChild != null) {
				// check the next level down for any oldTag matches
				replace(oldTag, newTag, ptr.firstChild);
			}
		}
	}

	/**
	 * Boldfaces every column of the given row of the table in the DOM tree. The
	 * boldface (b) tag appears directly under the td tag of every column of this
	 * row.
	 * 
	 * @param row
	 *            Row to bold, first row is numbered 1 (not 0).
	 */
	public void boldRow(int row) {

		bold(root, row);

	}

	// helper method for boldRow
	public void bold(TagNode root, int row) {

		// iterate through the tree by moving ptr to its sibling
		for (TagNode ptr = root; ptr != null; ptr = ptr.sibling) {

			if (root == null) {

				return;
			}
			// if ptr is the start of the table
			if (ptr.tag.equals("table")) {

				int count = 1;

				// this makes sibPtr the first row (tr)
				TagNode parent = ptr;
				TagNode sibPtr = null;
				TagNode currPtr = parent.firstChild;

				while (count != row) {

					sibPtr = currPtr;
					currPtr = currPtr.sibling;
					count++;
				}

				TagNode td = currPtr.firstChild;

				while (td != null) {

					TagNode tdKid = td.firstChild;
					td.firstChild = new TagNode("b", tdKid, null);

					td = td.sibling;
				}

			}

			if (ptr.firstChild != null) {
				// check the next level down for td
				bold(ptr.firstChild, row);
			}
		}

	}

	/**
	 * Remove all occurrences of a tag from the DOM tree. If the tag is p, em, or b,
	 * all occurrences of the tag are removed. If the tag is ol or ul, then All
	 * occurrences of such a tag are removed from the tree, and, in addition, all
	 * the li tags immediately under the removed tag are converted to p tags.
	 * 
	 * @param tag
	 *            Tag to be removed, can be p, em, b, ol, or ul
	 */
	public void removeTag(String tag) {

		if (tag.equals("p") || tag.equals("em") || tag.equals("b")) {

			removeCase1(tag, null, root);

		} else if (tag.equals("ol") || tag.equals("ul")) {

			removeCase2(tag, null, root);
		}
	}

	// remove tag helper method if tag is p, em, or b
	private void removeCase1(String tag, TagNode prev, TagNode root) {

		TagNode ptr = root;

		if (root == null) {

			return;
		}

		// if ptr = tag
		if (ptr.tag.equals(tag) && ptr.firstChild != null) {

			// Scenario 1: if ptr is the child of the previous ptr
			if (prev.firstChild == ptr) {

				TagNode tempParent = ptr;
				TagNode tempKid = ptr.firstChild;

				// reset the first child of prev to the ptr's child
				// this step deletes the tag
				prev.firstChild = tempKid;
				// reset ptr
				ptr = prev.firstChild;

				// if ptr's first child has siblings
				if (tempKid.sibling != null) {

					prev.firstChild.sibling = tempKid.sibling;

					TagNode tempSib = tempKid.sibling;

					// set tempSib to tempKid's last sibling
					while (tempSib.sibling != null) {

						tempSib = tempSib.sibling;
					}

					tempSib.sibling = tempParent.sibling;

					// if ptr's first child does not have siblings
				} else {

					prev.firstChild.sibling = tempParent.sibling;

				}

				// Scenario 2: if ptr is prev's sibling
			} else if (prev.sibling == ptr) {

				TagNode tempParent = ptr;
				TagNode tempKid = ptr.firstChild;

				prev.sibling = tempKid;
				// reset ptr
				ptr = prev.sibling;

				if (tempKid.sibling != null) {

					prev.sibling.sibling = tempKid.sibling;

					TagNode tempSib = prev.sibling.sibling;

					while (tempSib.sibling != null) {

						tempSib = tempSib.sibling;
					}

					tempSib.sibling = tempParent.sibling;

				} else {

					prev.sibling.sibling = tempParent.sibling;
				}
			}

		}

		prev = ptr;

		if (ptr.firstChild != null) {

			removeCase1(tag, prev, ptr.firstChild);
		}

		if (ptr.sibling != null) {

			removeCase1(tag, prev, ptr.sibling);
		}

	}

	// remove tag helper method if tag is ul or ol
	private void removeCase2(String tag, TagNode prev, TagNode root) {

		TagNode ptr = root;

		if (root == null) {

			return;
		}

		// if ptr = tag
		if (ptr.tag.equals(tag) && ptr.firstChild != null) {

			// Scenario 1: if ptr is the child of the previous ptr
			if (prev.firstChild == ptr) {

				TagNode tempParent = ptr;
				TagNode tempKid = ptr.firstChild;

				TagNode tkReset = tempKid;

				while (tkReset != null) {

					if (tkReset.tag.equals("li")) {

						tkReset.tag = "p";
					}
					tkReset = tkReset.sibling;
				}

				prev.firstChild = tempKid;

				// if ptr's first child has siblings
				if (tempKid.sibling != null) {

					prev.firstChild.sibling = tempKid.sibling;

					TagNode tempSib = tempKid.sibling;

					// set tempSib to tempKid's last sibling

					while (tempSib.sibling != null) {

						tempSib = tempSib.sibling;

					}

					tempSib.sibling = tempParent.sibling;

					// if ptr's first child does not have siblings
				} else {

					prev.firstChild.sibling = tempParent.sibling;

				}

				// Scenario 2: if ptr is prev's sibling
			} else if (prev.sibling == ptr) {

				TagNode tempParent = ptr;
				TagNode tempKid = ptr.firstChild;

				TagNode tkReset = tempKid;

				while (tkReset != null) {

					if (tkReset.tag.equals("li")) {

						tkReset.tag = "p";
					}
					tkReset = tkReset.sibling;
				}

				prev.sibling = tempKid;

				if (tempKid.sibling != null) {

					prev.sibling.sibling = tempKid.sibling;

					TagNode tempSib = prev.sibling.sibling;

					while (tempSib.sibling != null) {

						tempSib = tempSib.sibling;
					}

					tempSib.sibling = tempParent.sibling;

				} else {

					prev.sibling.sibling = tempParent.sibling;
				}
			}

		}

		prev = ptr;

		if (ptr.firstChild != null) {

			removeCase2(tag, prev, ptr.firstChild);
		}

		if (ptr.sibling != null) {

			removeCase2(tag, prev, ptr.sibling);
		}

	}

	/**
	 * Adds a tag around all occurrences of a word in the DOM tree.
	 * 
	 * @param word
	 *            Word around which tag is to be added
	 * @param tag
	 *            Tag to be added
	 */
	public void addTag(String word, String tag) {

		add(word, tag, root, null);

	}

	private void add(String word, String tag, TagNode root, TagNode prev) {

		TagNode ptr = root;

		if (root == null) {

			return;
		}

		String lowercasePtr = ptr.tag.toLowerCase();
		String lowercaseWord = word.toLowerCase();

		// CASE 1
		if (compareWord(lowercaseWord, lowercasePtr)) {

			TagNode newTag = new TagNode(tag, null, null);

			// Scenario 1: if ptr is the child of the previous ptr
			if (prev.firstChild == ptr) {

				TagNode tk = ptr;
				prev.firstChild = newTag;
				newTag.firstChild = tk;
				newTag.sibling = tk.sibling;
				prev.firstChild.firstChild.sibling = null;

				if (prev.firstChild.sibling != null) {

					ptr = prev.firstChild.sibling;
				}

			}
			// Scenario 2: if ptr is prev's sibling
			else if (prev.sibling == ptr) {

				TagNode tk = ptr;
				prev.sibling = newTag;
				newTag.firstChild = tk;
				newTag.sibling = tk.sibling;
				prev.sibling.firstChild.sibling = null;

				if (prev.sibling.sibling != null) {

					ptr = prev.sibling.sibling;
				}

			}
		}

		// CASE 2, CASE 3, CASE 4
		else if (containWord(word, ptr.tag) == true) {

			// index of space before word
			int beginIndex = beginIndex(ptr.tag, word);
			System.out.println(beginIndex);
			// index of space after word
			int endIndex = endIndex(ptr.tag, word);

			// CASE 2 -- if word is the first word in the sentence
			if (beginIndex == 0) {

				TagNode newTag = new TagNode(tag, null, null);

				TagNode wordNode = new TagNode(ptr.tag.substring(beginIndex, endIndex), null, null);
				TagNode restSentence = new TagNode(ptr.tag.substring(endIndex), null, null);

				TagNode holdPtr = ptr;

				// Scenario 1: if ptr is the child of the previous ptr
				if (prev.firstChild == ptr) {

					prev.firstChild = newTag;
					prev.firstChild.firstChild = wordNode;

					prev.firstChild.sibling = restSentence;

					// if ptr's first child has siblings
					if (holdPtr.sibling != null) {

						prev.firstChild.sibling.sibling = holdPtr.sibling;
					}

					// update ptr
					if (prev.firstChild.sibling != null) {

						ptr = prev.firstChild.sibling;
					}

				}
				// Scenario 2: if ptr is prev's sibling
				else if (prev.sibling == ptr) {

					prev.sibling = newTag;
					prev.sibling.firstChild = wordNode;

					prev.sibling.sibling = restSentence;

					// if ptr's first child has siblings
					if (holdPtr.sibling != null) {

						prev.firstChild.sibling.sibling = holdPtr.sibling;
					}

					// update ptr
					if (prev.sibling.sibling != null) {

						ptr = prev.sibling.sibling;
					}

				}

				// CASE 3 -- if it is in the middle of a sentence
			} else if (beginIndex > 0 && endIndex < ptr.tag.length()) {

				TagNode newTag = new TagNode(tag, null, null);
				TagNode beginSentence = new TagNode(ptr.tag.substring(0, beginIndex), null, null);
				TagNode wordNode = new TagNode(ptr.tag.substring(beginIndex, endIndex), null, null);
				TagNode restSentence = new TagNode(ptr.tag.substring(endIndex), null, null);

				// Scenario 1: if ptr is the child of the previous ptr
				if (prev.firstChild == ptr) {

					prev.firstChild = beginSentence;
					beginSentence.sibling = newTag;
					newTag.firstChild = wordNode;
					newTag.sibling = restSentence;

					// if ptr's first child has siblings
					if (ptr.sibling != null) {

						restSentence.sibling = ptr.sibling;
					}

					if (prev.firstChild.sibling != null) {

						ptr = newTag.sibling;
					}

				}

				// Scenario 2: if ptr is prev's sibling
				else if (prev.sibling == ptr) {

					prev.sibling = beginSentence;
					beginSentence.sibling = newTag;
					newTag.firstChild = wordNode;
					newTag.sibling = restSentence;

					// if ptr's first child has siblings
					if (ptr.sibling != null) {

						restSentence.sibling = ptr.sibling;
					}

					if (prev.sibling.sibling != null) {

						ptr = newTag.sibling;

					}

				}

				// CASE 4 -- if the word is the end of the sentence
			} else {

				TagNode newTag = new TagNode(tag, null, null);
				TagNode beginSentence = new TagNode(ptr.tag.substring(0, beginIndex), null, null);
				TagNode wordNode = new TagNode(ptr.tag.substring(beginIndex, endIndex), null, null);

				// Scenario 1: if ptr is the child of the previous ptr
				if (prev.firstChild == ptr) {

					prev.firstChild = beginSentence;
					beginSentence.sibling = newTag;
					newTag.firstChild = wordNode;

					if (ptr.sibling != null) {

						newTag.sibling = ptr.sibling;
					}

					if (prev.firstChild.sibling != null) {

					//	ptr = prev.sibling.sibling;

					}
				}

				// Scenario 2: if ptr is prev's sibling
				else if (prev.sibling == ptr) {

					prev.sibling = beginSentence;
					beginSentence.sibling = newTag;
					newTag.firstChild = wordNode;

					// if ptr's first child has siblings
					if (ptr.sibling != null) {

						newTag.sibling = ptr.sibling;
					}

					if (prev.sibling.sibling != null) {

						ptr = newTag.sibling;

					}

				}
			}

		}
		prev = ptr;

		if (ptr.firstChild != null) {

			add(word, tag, ptr.firstChild, prev);
		}

		if (ptr.sibling != null) {

			add(word, tag, ptr.sibling, prev);
		}

	}

	private int beginIndex(String str, String word) {

		int countSpaces = 0;
		String delim = " ";
		StringTokenizer tk = new StringTokenizer(str, delim, true);
		String curr = tk.nextToken();

		while (curr != null) {

			if (compareWord(curr, word) == true) {

				break;

			} else if (curr.equals(" ")) {

				countSpaces++;
			}

			if (tk.hasMoreTokens()) {

				curr = tk.nextToken();

			} else {

				curr = null;
			}

		}

		int count = 0;
		int begin = 0;

		for (int i = 0; (count != countSpaces && i < str.length()); i++) {

			if (str.charAt(i) == ' ') {

				count++;
				begin = i + 1;

			} else {

				begin = i;
			}

		}

		return begin;

	}

	private int endIndex(String str, String word) {

		int secondSpace = 0;
		String delim = " ";
		StringTokenizer tk = new StringTokenizer(str, delim, true);
		String curr = tk.nextToken();

		while (curr != null) {

			if (compareWord(curr, word) == true) {

				break;

			} else if (curr.equals(" ")) {

				secondSpace++;
			}

			if (tk.hasMoreTokens()) {

				curr = tk.nextToken();

			} else {

				curr = null;
			}

		}

		int count = 0;
		int end = 0;

		for (int i = 0; (count != secondSpace + 1 && i < str.length()); i++) {

			if (str.charAt(i) == ' ') {

				count++;
			}

			end = i;
		}
		return end + 1;
	}

	// checks to see if word matches ptr CASE 1
	private boolean compareWord(String word, String ptr) {

		if (word.equalsIgnoreCase(ptr)) {

			return true;

		} else if (word.equalsIgnoreCase(ptr.substring(0, ptr.length() - 1))
				&& (ptr.charAt(ptr.length() - 1) == ',' || ptr.charAt(ptr.length() - 1) == '.'
						|| ptr.charAt(ptr.length() - 1) == '!' || ptr.charAt(ptr.length() - 1) == '?'
						|| ptr.charAt(ptr.length() - 1) == ';' || ptr.charAt(ptr.length() - 1) == ':')

		) {

			return true;

		} else {

			return false;
		}
	}

	// checks if one token (word) is in a string
	private boolean containWord(String word, String ptr) {

		StringTokenizer tk = new StringTokenizer(ptr, " ", true);
		String curr = tk.nextToken();

		while (curr != null) {

			if (curr.toLowerCase().equals(word.toLowerCase())) {

				return true;
			
			} else if (word.equalsIgnoreCase(curr.substring(0, curr.length() - 1))
					&& (curr.charAt(curr.length() - 1) == ',' || curr.charAt(curr.length() - 1) == '.'
							|| curr.charAt(curr.length() - 1) == '!' || curr.charAt(curr.length() - 1) == '?'
							|| curr.charAt(curr.length() - 1) == ';' || curr.charAt(curr.length() - 1) == ':')

			) {

				return true;

			}

			else {

				if (tk.hasMoreTokens()) {

					curr = tk.nextToken();

				} else {

					curr = null;
				}
			}
		}
		return false;

	}

	/**
	 * Gets the HTML represented by this DOM tree. The returned string includes new
	 * lines, so that when it is printed, it will be identical to the input file
	 * from which the DOM tree was built.
	 * 
	 * @return HTML string, including new lines.
	 */
	public String getHTML() {
		StringBuilder sb = new StringBuilder();
		getHTML(root, sb);
		return sb.toString();
	}

	private void getHTML(TagNode root, StringBuilder sb) {
		for (TagNode ptr = root; ptr != null; ptr = ptr.sibling) {
			if (ptr.firstChild == null) {
				sb.append(ptr.tag);
				sb.append("\n");
			} else {
				sb.append("<");
				sb.append(ptr.tag);
				sb.append(">\n");
				getHTML(ptr.firstChild, sb);
				sb.append("</");
				sb.append(ptr.tag);
				sb.append(">\n");
			}
		}
	}

	/**
	 * Prints the DOM tree.
	 *
	 */
	public void print() {
		print(root, 1);
	}

	private void print(TagNode root, int level) {
		for (TagNode ptr = root; ptr != null; ptr = ptr.sibling) {
			for (int i = 0; i < level - 1; i++) {
				System.out.print("      ");
			}
			;
			if (root != this.root) {
				System.out.print("|---- ");
			} else {
				System.out.print("      ");
			}
			System.out.println(ptr.tag);
			if (ptr.firstChild != null) {
				print(ptr.firstChild, level + 1);
			}
		}
	}
}

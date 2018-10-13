package lse;

import java.io.*;
import java.util.*;

/**
 * This class builds an index of keywords. Each keyword maps to a set of pages
 * in which it occurs, with frequency of occurrence in each page.
 *
 */
public class LittleSearchEngine {

	/**
	 * This is a hash table of all keywords. The key is the actual keyword, and the
	 * associated value is an array list of all occurrences of the keyword in
	 * documents. The array list is maintained in DESCENDING order of frequencies.
	 */
	HashMap<String, ArrayList<Occurrence>> keywordsIndex;

	/**
	 * The hash set of all noise words.
	 */
	HashSet<String> noiseWords;

	/**
	 * Creates the keyWordsIndex and noiseWords hash tables.
	 */
	public LittleSearchEngine() {
		keywordsIndex = new HashMap<String, ArrayList<Occurrence>>(1000, 2.0f);
		noiseWords = new HashSet<String>(100, 2.0f);
	}

	/**
	 * Scans a document, and loads all keywords found into a hash table of keyword
	 * occurrences in the document. Uses the getKeyWord method to separate keywords
	 * from other words.
	 * 
	 * @param docFile
	 *            Name of the document file to be scanned and loaded
	 * @return Hash table of keywords in the given document, each associated with an
	 *         Occurrence object
	 * @throws FileNotFoundException
	 *             If the document file is not found on disk
	 */
	public HashMap<String, Occurrence> loadKeywordsFromDocument(String docFile) throws FileNotFoundException {

		try {

			Scanner temp = new Scanner(new File(docFile));

		} catch (Exception e) {

			throw new FileNotFoundException();
		}

		HashMap<String, Occurrence> keyWords = new HashMap<String, Occurrence>(1000, 2.0f);

		Scanner sc = new Scanner(new File(docFile));

		while (sc.hasNext()) {

			String curr = sc.next();
			String newCurr = getKeyword(curr);

			if (newCurr != null) {

				newCurr = newCurr.toLowerCase();
			}

			// already exists in keyWord file
			if (newCurr != null && keyWords.containsKey(newCurr)) {

				// update the number frequency
				keyWords.get(newCurr).frequency = keyWords.get(newCurr).frequency + 1;

				// if this is the first occurrence of it
			} else if (newCurr != null && (getKeyword(newCurr).equals(newCurr))) {

				Occurrence currOb = new Occurrence(docFile, 1);

				keyWords.put(newCurr, currOb);

			}
		}

		sc.close();

		return keyWords;
	}

	/**
	 * Merges the keywords for a single document into the master keywordsIndex hash
	 * table. For each keyword, its Occurrence in the current document must be
	 * inserted in the correct place (according to descending order of frequency) in
	 * the same keyword's Occurrence list in the master hash table. This is done by
	 * calling the insertLastOccurrence method.
	 * 
	 * @param kws
	 *            Keywords hash table for a document
	 */
	public void mergeKeywords(HashMap<String, Occurrence> kws) {

		for (String key : kws.keySet()) {

			if (keywordsIndex.containsKey(key)) {

				ArrayList<Occurrence> occ = keywordsIndex.get(key);
				occ.add(kws.get(key));
				keywordsIndex.put(key, occ);
				insertLastOccurrence(occ);

			} else {
				ArrayList<Occurrence> occ = new ArrayList<Occurrence>();
				occ.add(kws.get(key));
				keywordsIndex.put(key, occ);
			}
		}
	}

	/**
	 * Given a word, returns it as a keyword if it passes the keyword test,
	 * otherwise returns null. A keyword is any word that, after being stripped of
	 * any trailing punctuation, consists only of alphabetic letters, and is not a
	 * noise word. All words are treated in a case-INsensitive manner.
	 * 
	 * Punctuation characters are the following: '.', ',', '?', ':', ';' and '!'
	 * 
	 * @param word
	 *            Candidate word
	 * @return Keyword (word without trailing punctuation, LOWER CASE)
	 */
	public String getKeyword(String word) {

		String finalWord = wordTest(word);

		// if word is a noise word Example: the
		if (noiseWords.contains(finalWord)) {

			return null;

			// word is either a key word OR null
		} else {

			return finalWord;
		}

	}

	// helper for getKeyword
	private String wordTest(String word) {

		// error check if this word cannot be a keyWord
		for (int i = 0; i < word.length(); i++) {

			if (!Character.isAlphabetic(word.charAt(i)) && !(word.charAt(i) == '.') && !(word.charAt(i) == ',')
					&& !(word.charAt(i) == '?') && !(word.charAt(i) == ':') && !(word.charAt(i) == ';')
					&& !(word.charAt(i) == '!')) {

				return null;
			}
		}

		String delims = ".,?:;!";
		StringTokenizer tk = new StringTokenizer(word, delims, true);

		String curr = tk.nextToken();
		String next = null;

		if (tk.hasMoreTokens()) {

			next = tk.nextToken();
		}

		String ret = "";

		// check words that have puncutation in the middle
		// should return "the"
		// example: what,ever

		while (curr != null) {

			if ((curr.equals(".") || curr.equals(",") || curr.equals("?") || curr.equals(":") || curr.equals(";")
					|| curr.equals("!")) && (next != null && Character.isAlphabetic(next.charAt(0)))) {

				return null;
			} else if (!curr.equals(".") && !curr.equals(",") && !curr.equals("?") && !curr.equals(":")
					&& !curr.equals(";") && !curr.equals("!")) {

				ret += curr;
			}

			curr = next;

			if (tk.hasMoreTokens()) {

				next = tk.nextToken();
			} else {

				next = null;
			}
		}

		if (ret != null && ret.length() > 0) {

			ret = ret.toLowerCase();
		}
		return ret;
	}

	/**
	 * Inserts the last occurrence in the parameter list in the correct position in
	 * the list, based on ordering occurrences on descending frequencies. The
	 * elements 0..n-2 in the list are already in the correct order. Insertion is
	 * done by first finding the correct spot using binary search, then inserting at
	 * that spot.
	 * 
	 * @param occs
	 *            List of Occurrences
	 * @return Sequence of mid point indexes in the input list checked by the binary
	 *         search process, null if the size of the input list is 1. This
	 *         returned array list is only used to test your code - it is not used
	 *         elsewhere in the program.
	 */
	public ArrayList<Integer> insertLastOccurrence(ArrayList<Occurrence> occs) {

		if (occs.size() == 1) {

			return null;
		}

		ArrayList<Integer> mids = new ArrayList<Integer>();

		/*
		 * Check for edge cases (i.e. first or last)
		 */
		int low = 0;
		int high = occs.size() - 2;
		Occurrence key = occs.get(occs.size() - 1);
		int middle = 0;

		while (high >= low) {

			middle = (low + high) / 2;
			System.out.println(middle);
			mids.add(middle);

			if (occs.get(middle).frequency == key.frequency) {

				break;
			}

			if (occs.get(middle).frequency < key.frequency) {

				high = middle - 1;

			}

			if (occs.get(middle).frequency > key.frequency) {

				low = middle + 1;

			}
		}

		Occurrence hold = occs.get(occs.size() - 1);
		occs.remove(occs.size() - 1);

		occs.add(low, hold);

		return mids;
	}

	/**
	 * This method indexes all keywords found in all the input documents. When this
	 * method is done, the keywordsIndex hash table will be filled with all
	 * keywords, each of which is associated with an array list of Occurrence
	 * objects, arranged in decreasing frequencies of occurrence.
	 * 
	 * @param docsFile
	 *            Name of file that has a list of all the document file names, one
	 *            name per line
	 * @param noiseWordsFile
	 *            Name of file that has a list of noise words, one noise word per
	 *            line
	 * @throws FileNotFoundException
	 *             If there is a problem locating any of the input files on disk
	 */
	public void makeIndex(String docsFile, String noiseWordsFile) throws FileNotFoundException {
		// load noise words to hash table
		Scanner sc = new Scanner(new File(noiseWordsFile));
		while (sc.hasNext()) {
			String word = sc.next();
			noiseWords.add(word);
		}

		// index all keywords
		sc = new Scanner(new File(docsFile));
		while (sc.hasNext()) {
			String docFile = sc.next();
			HashMap<String, Occurrence> kws = loadKeywordsFromDocument(docFile);
			mergeKeywords(kws);
		}
		sc.close();
	}

	/**
	 * Search result for "kw1 or kw2". A document is in the result set if kw1 or kw2
	 * occurs in that document. Result set is arranged in descending order of
	 * document frequencies. (Note that a matching document will only appear once in
	 * the result.) Ties in frequency values are broken in favor of the first
	 * keyword. (That is, if kw1 is in doc1 with frequency f1, and kw2 is in doc2
	 * also with the same frequency f1, then doc1 will take precedence over doc2 in
	 * the result. The result set is limited to 5 entries. If there are no matches
	 * at all, result is null.
	 * 
	 * @param kw1
	 *            First keyword
	 * @param kw1
	 *            Second keyword
	 * @return List of documents in which either kw1 or kw2 occurs, arranged in
	 *         descending order of frequencies. The result size is limited to 5
	 *         documents. If there are no matches, returns null.
	 */
	public ArrayList<String> top5search(String kw1, String kw2) {

		ArrayList<String> top5 = new ArrayList<String>();

		ArrayList<Occurrence> first = keywordsIndex.get(kw1);
		ArrayList<Occurrence> second = keywordsIndex.get(kw2);

		if (first == null && second == null) {

			return null;
		}

		int fCount = 0;
		int sCount = 0;

		while ((first != null && first.size() > fCount) && (second != null && second.size() > sCount)
				&& top5.size() < 5) {

			if (first.get(fCount).frequency > second.get(sCount).frequency) {

				if (!top5.contains(first.get(fCount).document)) {

					top5.add((first.get(fCount).document));

				}
				fCount++;

			}

			else if (second.get(sCount).frequency > first.get(fCount).frequency) {

				if (!top5.contains(second.get(sCount).document)) {

					top5.add((second.get(sCount).document));

				}
				sCount++;
			}

			else {

				if (!top5.contains(first.get(fCount).document)) {

					top5.add((first.get(fCount).document));

				}
				fCount++;
				sCount++;
			}

		}

		while ((first != null && first.size() > fCount) && second == null && (top5.size() < 5)) {

			if (!top5.contains(first.get(fCount).document)) {

				top5.add(first.get(fCount).document);

			}

			fCount++;

		}

		while (first == null && (second != null && second.size() > sCount) && (top5.size() < 5)) {

			if (!top5.contains(second.get(sCount).document)) {

				top5.add(second.get(sCount).document);

			}

			sCount++;

		}

		return top5;
	}

}

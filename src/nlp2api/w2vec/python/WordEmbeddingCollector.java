
/******
 * 
 * @author MasudRahman
 * Word Embedding collector
 * 
 */

package nlp2api.w2vec.python;

import java.util.ArrayList;
import java.util.HashMap;
import nlp2api.utility.ContentLoader;
import nlp2api.config.StaticData;

public class WordEmbeddingCollector {

	String embeddingFile;
	HashMap<String, ArrayList<Double>> masterEmbeddingMap;

	public WordEmbeddingCollector() {
		this.embeddingFile = StaticData.HOME_DIR + "/nlp2api-word2vec/embeddings.txt";
		this.masterEmbeddingMap = new HashMap<String, ArrayList<Double>>();
	}

	public HashMap<String, ArrayList<Double>> getMasterEmbeddingList() {
		ArrayList<String> fileLines = ContentLoader.getAllTokensSC(this.embeddingFile);
		int entries=0;
		for (String fileLine : fileLines) {
			String[] parts = fileLine.split("\\s+");
			String key = parts[0].trim();
			ArrayList<Double> tempDim = new ArrayList<>();
			for (int i = 1; i < parts.length; i++) {
				double score = Double.parseDouble(parts[i].trim());
				tempDim.add(score);
			}
			this.masterEmbeddingMap.put(key, tempDim);
			entries++;
		}
		
		System.out.println("Items loaded:"+entries);
		
		return this.masterEmbeddingMap;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		WordEmbeddingCollector wecoll=new WordEmbeddingCollector();
		System.out.println(wecoll.getMasterEmbeddingList().entrySet().size());
	}
}


/**********
 * @author MasudRahman
 * This class determines the semantic proximity between the query keyword and a candidate API class.
 */

package nlp2api.code.search.bda.scorecalc;

import java.util.ArrayList;
import java.util.HashMap;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import nlp2api.w2vec.python.WordEmbeddingCollector;
import nlp2api.data.analytics.WordProximityDetector;
import edu.stanford.nlp.util.ArrayUtils;

public class APIKeywordProximityCalc {

	ArrayList<String> candidateAPIKeys;
	ArrayList<String> queryTerms;
	HashMap<String, Double> proximityScoreMap;
	HashMap<String, ArrayList<Double>> vectorMap;
	boolean vectorProvided = false;
	public static HashMap<String, ArrayList<Double>> masterEmbeddingMap = new HashMap<>();


	public APIKeywordProximityCalc(ArrayList<String> queryKeywords,
			ArrayList<String> candidateAPIkeys,
			HashMap<String, ArrayList<Double>> vectorMap) {
		this.queryTerms = queryKeywords;
		this.candidateAPIKeys = candidateAPIkeys;
		this.proximityScoreMap = new HashMap<>();
		this.vectorMap = vectorMap;
		this.vectorProvided = true;
	}
	
	public HashMap<String, Double> calculateQAProximityLocal() {
		ArrayList<String> wordList = new ArrayList<>();
		wordList.addAll(this.queryTerms);
		wordList.addAll(this.candidateAPIKeys);

		// get the master embeddings
		if (masterEmbeddingMap.isEmpty()) {
			if (!vectorProvided) {
				masterEmbeddingMap = new WordEmbeddingCollector()
						.getMasterEmbeddingList();
			} else {
				masterEmbeddingMap = vectorMap;
			}
		}

		for (String apiKey : this.candidateAPIKeys) {
			ArrayList<Double> proxies = new ArrayList<>();
			for (String qterm : this.queryTerms) {
				double proximity = new WordProximityDetector(apiKey, qterm,
						masterEmbeddingMap).determineProximity();
				proxies.add(proximity);
			}
			DescriptiveStatistics desStat = new DescriptiveStatistics(
					ArrayUtils.asPrimitiveDoubleArray(proxies));
			double maxProximity = desStat.getMax();
			this.proximityScoreMap.put(apiKey, maxProximity);
		}
		return this.proximityScoreMap;
	}


	public static void main(String[] args) {
		// TODO Auto-generated method stub
	}
}

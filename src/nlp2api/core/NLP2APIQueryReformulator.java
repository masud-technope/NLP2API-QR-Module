
/******
 * @author MasudRahman
 * The actual class that reformulates a given NL query into relevant API classes.
 * 
 */

package nlp2api.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import nlp2api.config.StaticData;
import nlp2api.text.normalizer.TextNormalizer;
import nlp2api.utility.ItemSorter;
import nlp2api.utility.MiscUtility;
import nlp2api.w2vec.python.WordEmbeddingCollector;
import nlp2api.code.search.bda.CandidateManager;
import nlp2api.code.search.bda.scorecalc.APIKeywordProximityCalc;
import nlp2api.code.search.bda.scorecalc.BordaScoreCalc;

public class NLP2APIQueryReformulator {

	int caseNo;
	String initialQuery;
	int TOPK;
	String scoreKey;

	// load master embedding once
	static HashMap<String, ArrayList<Double>> masterList = new WordEmbeddingCollector().getMasterEmbeddingList();

	public NLP2APIQueryReformulator(int caseNo, String initialQuery, int TOPK, String scoreKey) {
		this.caseNo = caseNo;
		this.initialQuery = initialQuery;// initialQuery.toLowerCase();
		this.TOPK = TOPK;
		this.scoreKey = scoreKey;
	}

	protected String getQueryKeywords(String searchQuery) {
		return new TextNormalizer(searchQuery).normalizeText();
	}

	protected String getTopKItems(ArrayList<String> apiNames, int K) {
		String temp = new String();
		for (int i = 0; i < K; i++) {
			if (i < apiNames.size()) {
				temp += apiNames.get(i) + "\t";
			}
		}
		return temp.trim();
	}

	protected HashMap<String, Double> extractTopKAPINames(HashMap<String, Double> scoreMap) {
		List<Map.Entry<String, Double>> sorted = ItemSorter.sortHashMapDouble(scoreMap);
		HashMap<String, Double> suggestion = new HashMap<String, Double>();
		for (Map.Entry<String, Double> entry : sorted) {
			suggestion.put(entry.getKey(), entry.getValue());
			if (suggestion.size() == TOPK)
				break;
		}
		return suggestion;
	}

	protected HashMap<String, Double> combinedExtractTopKAPI(HashMap<String, Double> bscoreMap,
			HashMap<String, Double> pscoreMap) {
		HashMap<String, Double> combined = new HashMap<>();

		switch (scoreKey) {
		case "borda":
			for (String key : bscoreMap.keySet()) {
				combined.put(key, bscoreMap.get(key));
			}
			break;
		case "w2vec":
			for (String key : pscoreMap.keySet()) {
				if (combined.containsKey(key)) {
					double updated = combined.get(key) + pscoreMap.get(key);
					combined.put(key, updated);
				} else {
					combined.put(key, pscoreMap.get(key));
				}
			}
			break;
		case "both":
			for (String key : bscoreMap.keySet()) {
				double bscore = bscoreMap.get(key) * StaticData.Borda_Weight;
				combined.put(key, bscore);
			}
			for (String key : pscoreMap.keySet()) {
				if (combined.containsKey(key)) {
					double bscore = combined.get(key);
					double pscore = pscoreMap.get(key) * StaticData.w2vec_Weight;
					combined.put(key, bscore + pscore);
				} else {
					double score = pscoreMap.get(key) * StaticData.w2vec_Weight;
					combined.put(key, score);
				}
			}
			break;
		default:
			break;
		}

		return extractTopKAPINames(combined);
	}

	protected HashMap<String, Double> normalizedScores(HashMap<String, Double> scoreMap) {
		double maxScore = 0;
		for (String key : scoreMap.keySet()) {
			double myScore = scoreMap.get(key);
			if (myScore > maxScore) {
				maxScore = myScore;
			}
		}
		for (String key : scoreMap.keySet()) {
			double myScore = scoreMap.get(key);
			myScore = myScore / maxScore;
			scoreMap.put(key, myScore);
		}
		return scoreMap;
	}

	public HashMap<String, Double> provideRelevantAPIs() {
		// String keywords = getQueryKeywords();
		CandidateManager candidateManager = new CandidateManager(caseNo, initialQuery, StaticData.PRF_SIZE);
		HashMap<String, ArrayList<String>> candidateMap = candidateManager.collectQRCandidates(true);

		BordaScoreCalc borda = new BordaScoreCalc(candidateMap);
		HashMap<String, Double> bscoreMap = borda.calculateBordaScore();

		// normalize the scores
		bscoreMap = normalizedScores(bscoreMap);

		String normalized = getQueryKeywords(initialQuery);
		APIKeywordProximityCalc akpCalc = new APIKeywordProximityCalc(MiscUtility.str2List(normalized),
				new ArrayList<String>(bscoreMap.keySet()), masterList);

		HashMap<String, Double> akpScoreMap = akpCalc.calculateQAProximityLocal();
		// .calculateKeywordAPIProximity();

		return combinedExtractTopKAPI(bscoreMap, akpScoreMap);
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int caseNo = 31;
		String searchQuery = "How do I execute Http Get request?";
		searchQuery = new TextNormalizer(searchQuery).normalizeTextLight();
		int TOPK = 10;
		System.out.println(new NLP2APIQueryReformulator(caseNo, searchQuery, TOPK, "both").provideRelevantAPIs());

	}
}

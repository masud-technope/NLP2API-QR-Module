/*****
 * 
 * 
 * @author MasudRahman
 * It collects candidate API classes from relevant Stack Overflow threads for each given query 
 */

package nlp2api.code.search.bda;

import java.util.ArrayList;
import java.util.HashMap;
import nlp2api.utility.ContentLoader;
import nlp2api.utility.MiscUtility;
import nlp2api.config.StaticData;

public class CandidateManager {

	String initialQuery;
	int TOPK;
	int caseNo;

	public CandidateManager(int caseNo, String initialQuery, int TOPK) {
		this.initialQuery = initialQuery;
		this.TOPK = TOPK;
		this.caseNo = caseNo;
	}

	public HashMap<String, ArrayList<String>> collectQRCandidates(boolean local) {
		String candidateFile = StaticData.HOME_DIR + "/nlp2api-candidate/" + caseNo + ".txt";
		ArrayList<String> cLines = ContentLoader.getAllLinesOptList(candidateFile);
		HashMap<String, ArrayList<String>> candidateMap = new HashMap<String, ArrayList<String>>();
		for (String cLine : cLines) {
			String[] parts = cLine.split(":");
			String candidateSourceKey = parts[0].trim();
			ArrayList<String> candidateList = new ArrayList<String>();
			candidateList = MiscUtility.str2List(parts[1].trim());
			candidateMap.put(candidateSourceKey, candidateList);
		}
		return candidateMap;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int caseNo = 2;
		String query = "How do I compress or zip a directory recursively?";
		System.out.println(new CandidateManager(caseNo, query, 10).collectQRCandidates(true));

	}
}

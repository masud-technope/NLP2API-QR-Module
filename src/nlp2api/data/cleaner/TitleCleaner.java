
/***********
 * SO corpus utility class
 */

package nlp2api.data.cleaner;

import java.io.File;
import nlp2api.config.StaticData;
import nlp2api.text.normalizer.TextNormalizer;
import nlp2api.utility.ContentLoader;
import nlp2api.utility.ContentWriter;

public class TitleCleaner {

	String titleFolder;
	String titleFolderNorm;

	public TitleCleaner() {
		this.titleFolder = StaticData.EXP_HOME + "/dataset/question";
		this.titleFolderNorm = StaticData.EXP_HOME + "/dataset/question-norm";
	}

	protected void cleanTheTitle() {
		File[] files = new File(this.titleFolder).listFiles();
		for (File f : files) {
			String content = ContentLoader.loadFileContent(f.getAbsolutePath());
			String normalized = new TextNormalizer(content).normalizeText();
			String outFile = this.titleFolderNorm + "/" + f.getName();
			ContentWriter.writeContent(outFile, normalized);
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new TitleCleaner().cleanTheTitle();
	}
}

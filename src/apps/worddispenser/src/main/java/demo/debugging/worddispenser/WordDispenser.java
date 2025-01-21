package demo.debugging.worddispenser;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MarkerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class WordDispenser {
	
	public static final String WORDSLIST_FILENAME = "WORDSLISTFILE";

	protected static final String LOG_MARKER = "WORDS DISPENSER";
	protected static final Logger LOGGER = LoggerFactory.getLogger(WordDispenser.class);
	
	private List<String> words = new ArrayList<>();
	
	public WordDispenser(@Value("${word.list.filepath:#{null}}") String wordsListFile) {
		if (wordsListFile == null || wordsListFile.isEmpty()) {
			return;
		}
		try {
			List<String> wordsList = Files.readAllLines(new File(wordsListFile).toPath(), Charset.defaultCharset());
			setList(wordsList);
			LOGGER.info(MarkerFactory.getMarker(LOG_MARKER), "Loaded words list from file '{}'", wordsListFile);
		} catch (IOException ex) {
			LOGGER.error(MarkerFactory.getMarker(LOG_MARKER), "Could not read words list from file '{}'", wordsListFile, ex);
			ex.printStackTrace();
		}
	}
	
	public void setList(List<String> words) {
		synchronized(this) {
			this.words.clear();
			this.words.addAll(words);
		}
	}
	
	public String getWord(int index) {
		synchronized(this) {
			if (words.isEmpty()) {
				throw new IllegalStateException("List of words not yet set.");
			}
			if (index < 0 || index >= words.size()) {
				throw new IllegalArgumentException("Index is not within range.");
			}
			return words.get(index);
		}
	}
	
	public boolean isReady() {
		return !words.isEmpty();
	}
}

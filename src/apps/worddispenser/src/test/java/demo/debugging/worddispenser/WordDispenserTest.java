package demo.debugging.worddispenser;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class WordDispenserTest {
	
	@InjectMocks
	private WordDispenser wordDispenser;
	
	@Test
	void storesCopyOfListOfWords() {
		List<String> newWords = Arrays.asList("first", "second", "third");
		wordDispenser.setList(newWords);
		for (int index = 0; index < newWords.size(); index++) {
			assertThat(wordDispenser.getWord(index)).isEqualTo(newWords.get(index));
		}
	}

	@Test
	void barfsIllegalStateIfListNotSet() {
		assertThrows(IllegalStateException.class, () -> wordDispenser.getWord(1));
	}

	@Test
	void bargsIllegalArgumentIfIndexOutOfBounds() {
		List<String> newWords = Arrays.asList("first", "second", "third");
		wordDispenser.setList(newWords);
		assertThrows(IllegalArgumentException.class, () -> wordDispenser.getWord(-1));
		assertThrows(IllegalArgumentException.class, () -> wordDispenser.getWord(14));
	}
	
	@Test
	void loadsInitialWordlistFromFile() {
		String fileName = null;
		List<String> expected = new ArrayList<>();
		WordDispenser wordDispenser2 = null;
		try {
			fileName = Files.createTempFile(null, null, new FileAttribute<?>[0]).toString();
			FileWriter fileWriter = new FileWriter(fileName);
		    PrintWriter printWriter = new PrintWriter(fileWriter);
		    for (int index = 0; index < 3; index++) {
		    	printWriter.printf("word-%d\n", index);
		    	expected.add("word-" + index);
		    }
		    printWriter.close();
		    wordDispenser2 = new WordDispenser(fileName);
		} catch (IOException e) {
			fail(e);
		} finally {
			if (fileName != null) {
				try {
					Files.delete(new File(fileName).toPath());
				} catch (IOException e) {
					System.out.println("Failed to remove temporary file '" + fileName + "'"); 
					e.printStackTrace();
				}
			}
		}
		@SuppressWarnings("unchecked")
		List<String> actual = (List<String>) ReflectionTestUtils.getField(wordDispenser2,  "words");
		assertThat(actual).hasSameElementsAs(expected);
	}

	@Test
	void isNotReadyWhenWordsNotSet() {
		assertThat(wordDispenser.isReady()).isFalse();
	}

	@Test
	void isReadyWhenWordsSet() {
		List<String> newWords = Arrays.asList("first", "second", "third");
		wordDispenser.setList(newWords);
		assertThat(wordDispenser.isReady()).isTrue();
	}	
}

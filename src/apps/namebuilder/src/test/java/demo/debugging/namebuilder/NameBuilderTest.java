package demo.debugging.namebuilder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import demo.debugging.numberpair.api.NumbersPairsApi;
import demo.debugging.numberpair.model.PairResponseBodyWrapper;
import demo.debugging.worddispenser.api.WordsDefaultApi;
import demo.debugging.worddispenser.model.WordResponseBody;
import demo.debugging.worddispenser.model.WordResponseBodyWrapper;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class NameBuilderTest {

	@Mock
	private NumbersPairsApi pairsApi;
	
	@Mock
	private WordsDefaultApi adjectivesApi;
	
	@Mock
	private WordsDefaultApi nounsApi;
	
	private NameBuilder nameBuilder;

	private static final String TEST_ADJECTIVE = "adjective";
	private static final String TEST_NOUN = "noun";

	@BeforeEach
	void setupNameBuilder() {
		nameBuilder = new NameBuilder(pairsApi, nounsApi, adjectivesApi);
	}
	
	@Test
	void returnsNameFromServiceResponses() {
		
		when(pairsApi.pairGet()).thenReturn(new PairResponseBodyWrapper().one(1).two(1));
		when(adjectivesApi.wordIndexGet(1)).thenReturn(new WordResponseBodyWrapper().word(TEST_ADJECTIVE));
		when(nounsApi.wordIndexGet(1)).thenReturn(new WordResponseBodyWrapper().word(TEST_NOUN));

		assertThat(nameBuilder.getName()).isEqualTo(TEST_ADJECTIVE + "_" + TEST_NOUN);
	}

	@Test
	void convertsReturnValueToLowerCase() {
		when(pairsApi.pairGet()).thenReturn(new PairResponseBodyWrapper().one(1).two(1));
		when(adjectivesApi.wordIndexGet(1)).thenReturn(new WordResponseBodyWrapper().word(TEST_ADJECTIVE.toUpperCase()));
		when(nounsApi.wordIndexGet(1)).thenReturn(new WordResponseBodyWrapper().word(TEST_NOUN.toUpperCase()));

		assertThat(nameBuilder.getName()).isEqualTo((TEST_ADJECTIVE + "_" + TEST_NOUN).toLowerCase());
	}
	
	@Test
	void relaysExceptionFromPairsApi() {
		Exception expected = new RuntimeException("test-generated exception from pairs API");
		when(pairsApi.pairGet()).thenThrow(expected);
		when(adjectivesApi.wordIndexGet(1)).thenReturn(new WordResponseBodyWrapper().word(TEST_ADJECTIVE));
		when(nounsApi.wordIndexGet(1)).thenReturn(new WordResponseBodyWrapper().word(TEST_NOUN));
		
		Exception actual = assertThrows(RuntimeException.class, nameBuilder::getName);
		assertThat(actual).isEqualTo(expected);
	}

	@Test
	void relaysExceptionFromAdjectivesApi() {
		Exception expected = (Exception) new RuntimeException("test-generated exception from adjectives API");
		when(pairsApi.pairGet()).thenReturn(new PairResponseBodyWrapper().one(1).two(1));
		when(adjectivesApi.wordIndexGet(1)).thenThrow(expected);
		when(nounsApi.wordIndexGet(1)).thenReturn(new WordResponseBodyWrapper().word(TEST_NOUN));
		
		Exception actual = assertThrows(RuntimeException.class, nameBuilder::getName);
		assertThat(actual).isEqualTo(expected);
	}

	@Test
	void relaysExceptionFromNounsApi() {
		Exception expected = (Exception) new RuntimeException("test-generated exception from nouns API");
		when(pairsApi.pairGet()).thenReturn(new PairResponseBodyWrapper().one(1).two(1));
		when(adjectivesApi.wordIndexGet(1)).thenReturn(new WordResponseBodyWrapper().word(TEST_ADJECTIVE));
		when(nounsApi.wordIndexGet(1)).thenThrow(expected);
		
		Exception actual = assertThrows(RuntimeException.class, nameBuilder::getName);
		assertThat(actual).isEqualTo(expected);
	}
	
	@Test
	void failsIfReturnedIndexesAreNull() {
		when(pairsApi.pairGet()).thenReturn(new PairResponseBodyWrapper().one(null).two(null));
		
		assertThrows(RuntimeException.class, nameBuilder::getName);
	}
	
	@Test
	void failsIfReturnedAdjectiveIsNull() {
		when(pairsApi.pairGet()).thenReturn(new PairResponseBodyWrapper().one(1).two(1));
		when(adjectivesApi.wordIndexGet(1)).thenReturn(new WordResponseBodyWrapper().word(null));
		when(nounsApi.wordIndexGet(1)).thenReturn(new WordResponseBodyWrapper().word(TEST_NOUN));
		
		Exception actual = assertThrows(RuntimeException.class, nameBuilder::getName);
		assertThat(actual.getMessage()).containsSequence("adjective").containsSequence("null");
	}
	
	@Test
	void failsIfReturnedNounIsNull() {
		when(pairsApi.pairGet()).thenReturn(new PairResponseBodyWrapper().one(1).two(1));
		when(adjectivesApi.wordIndexGet(1)).thenReturn(new WordResponseBodyWrapper().word(TEST_ADJECTIVE));
		when(nounsApi.wordIndexGet(1)).thenReturn(new WordResponseBodyWrapper().word(null));
		
		Exception actual = assertThrows(RuntimeException.class, nameBuilder::getName);
		assertThat(actual.getMessage()).containsSequence("noun").containsSequence("null");
	}
	
	/*
	 *  
	 * throws exception in case service calls return null values */
	
}

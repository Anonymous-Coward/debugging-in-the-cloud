package demo.debugging.namebuilder;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import demo.debugging.numberpair.api.NumbersPairsApi;
import demo.debugging.numberpair.model.PairResponseBodyWrapper;
import demo.debugging.worddispenser.api.WordsDefaultApi;

@Service
public class NameBuilder {

	private final NumbersPairsApi numbersPairsApi;
	public NameBuilder(
			NumbersPairsApi numbersPairsApi, 
			@Qualifier("nounsApi") WordsDefaultApi nounsApi, 
			@Qualifier("adjectivesApi") WordsDefaultApi adjectivesApi) {
		super();
		this.numbersPairsApi = numbersPairsApi;
		this.nounsApi = nounsApi;
		this.adjectivesApi = adjectivesApi;
	}

	private final WordsDefaultApi nounsApi;
	private final WordsDefaultApi adjectivesApi;
	
	public String getName() {
			PairResponseBodyWrapper pairResponse = numbersPairsApi.pairGet();
			assertNotNull(pairResponse.getOne(), "adjective index");
			assertNotNull(pairResponse.getTwo(), "noun index");
			String adjective = adjectivesApi.wordIndexGet(pairResponse.getOne()).getWord();
			assertNotNull(adjective, "adjective");
			String noun = nounsApi.wordIndexGet(pairResponse.getTwo()).getWord();
			assertNotNull(noun, "noun");
			return (adjective + "_" + noun).toLowerCase();
	}
	
	private void assertNotNull(Object value, String name) {
		if (value == null) {
			throw new NullPointerException(name + " was null.");
		}
	}
}

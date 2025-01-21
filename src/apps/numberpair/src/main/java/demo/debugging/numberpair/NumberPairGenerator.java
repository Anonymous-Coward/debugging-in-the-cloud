package demo.debugging.numberpair;

import java.util.Date;
import java.util.Random;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MarkerFactory;

@Service
public class NumberPairGenerator {
	private int maxOne;
	private int maxTwo;

	protected static final String LOG_MARKER = "NUMBER PAIRS";
	protected static final Logger LOGGER = LoggerFactory.getLogger(NumberPairGenerator.class);
	
	public final record NumberPair(int one, int two) {}
	
	Random random = new Random(new Date().getTime());
	
	public NumberPairGenerator(
			@Value("${number.pairs.maxOne:0}") int maxOne,
			@Value("${number.pairs.maxTwo:0}") int maxTwo) {
		LOGGER.info(MarkerFactory.getMarker(LOG_MARKER), "Constructing number pair generator with arguments {}, {}", maxOne, maxTwo);
		if (maxOne > 0) { 
			this.maxOne = maxOne;
			LOGGER.debug(MarkerFactory.getMarker(LOG_MARKER), "Set max limit for first number to {} from configuration", maxOne);
		}
		if (maxTwo > 0) { 
			this.maxTwo = maxTwo;
			LOGGER.debug(MarkerFactory.getMarker(LOG_MARKER), "Set max limit for second number to {} from configuration", maxOne );
		}
	}
	
	public int getMaxOne() {
		return maxOne;
	}
	public void setMaxOne(int maxOne) {
		if (maxOne < 1) {
			throw new IllegalArgumentException("Limit for first random number must be strictly positive and integer.");
		}
		this.maxOne = maxOne;
	}
	public int getMaxTwo() {
		return maxTwo;
	}
	public void setMaxTwo(int maxTwo) {
		if (maxTwo < 1) {
			throw new IllegalArgumentException("Limit for second random number must be strictly positive and integer.");
		}
		this.maxTwo = maxTwo;
	}
	
	public NumberPair generate() {
		return new NumberPair(
				Math.abs(random.nextInt() % maxOne), 
				Math.abs(random.nextInt() % maxTwo));
	}

	public boolean isReady() {
		return maxOne > 0 && maxTwo > 0;
	}
}

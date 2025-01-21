package demo.debugging.numberpair;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Date;
import java.util.Random;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

class NumberPairGeneratorTest {

	Random random = new Random(new Date().getTime());
	
	@Test
	void cannotSetNegativeLimits() {
		var generator = new NumberPairGenerator(1, 1);
		assertThrows(IllegalArgumentException.class, () -> generator.setMaxOne(-1));
		assertThrows(IllegalArgumentException.class, () -> generator.setMaxTwo(-1));
	}

	@Test
	void doesNotSetInvalidLimitsFromConstructor() {
		var generator = new NumberPairGenerator(-1, -1);
		assertThat(generator.getMaxOne()).isZero();
		assertThat(generator.getMaxTwo()).isZero();
	}
	
	@RepeatedTest(1000)
	void generatedNumbersAreInRange() {
		var maxOne = 1000 + Math.abs(random.nextInt() % 2000);
		var maxTwo = 1000 + Math.abs(random.nextInt() % 2000);
		var generator = new NumberPairGenerator(maxOne, maxTwo);
		var pair = generator.generate();
		assertThat(pair.one()).isNotNegative().isLessThan(maxOne);
		assertThat(pair.two()).isNotNegative().isLessThan(maxTwo);
	}
	
	@RepeatedTest(1000)
	void generatedNumbersAreInRangeAfterRangeChange() {
		var maxOne = 1000 + Math.abs(random.nextInt() % 2000);
		var maxTwo = 1000 + Math.abs(random.nextInt() % 2000);
		var generator = new NumberPairGenerator(maxOne, maxTwo);
		
		var pair = generator.generate();
		
		assertThat(pair.one()).isNotNegative().isLessThan(maxOne);
		assertThat(pair.two()).isNotNegative().isLessThan(maxTwo);

		maxOne = maxOne - Math.abs(random.nextInt() % 1000);
		maxTwo = maxTwo - Math.abs(random.nextInt() % 1000);
		generator.setMaxOne(maxOne);
		generator.setMaxTwo(maxTwo);
		
		pair = generator.generate();
		
		assertThat(pair.one()).isNotNegative().isLessThan(maxOne);
		assertThat(pair.two()).isNotNegative().isLessThan(maxTwo);
	}

	@Test
	void isNotReadyWhenLimitsNotSet() {
		var generator = new NumberPairGenerator(0, 0);
		assertThat(generator.isReady()).isFalse();
		
		generator = new NumberPairGenerator(14, 0);
		assertThat(generator.isReady()).isFalse();
		
		generator = new NumberPairGenerator(0, 17);
		assertThat(generator.isReady()).isFalse();		
	}

	@Test
	void isReadyWhenWordsSet() {
		var generator = new NumberPairGenerator(14, 17);
		assertThat(generator.isReady()).isTrue();
	}	
}

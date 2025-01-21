package demo.debugging.worddispenser;

import static demo.debugging.worddispenser.WordDispenserController.STATUS_TAG;
import static demo.debugging.worddispenser.WordDispenserController.STATUS_VALUE_STARTING;
import static demo.debugging.worddispenser.WordDispenserController.STATUS_VALUE_UP;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.NativeWebRequest;

import com.fasterxml.jackson.databind.JsonNode;

import demo.debugging.worddispenser.model.ErrorResponseBody;
import demo.debugging.worddispenser.model.ListResponseBodyWrapper;
import demo.debugging.worddispenser.model.WordResponseBody;
import demo.debugging.worddispenser.model.WordResponseBodyWrapper;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class WordDispenserControllerTest {

	@Mock
	private WordDispenser wordDispenser;
	
	@Mock
	private NativeWebRequest nativeWebRequest;
	
	@InjectMocks
	private WordDispenserController wordDispenserController;
		
	@Test
	void forwardsSetListToService() {
		List<String> words = Arrays.asList("one", "two", "three");
		
		wordDispenserController.listPut(words);
		
		verify(wordDispenser).setList(words);
	}

	@Test
	void forwardsGetWordToService() {
		final int wordIndex = 14;
		final String word = "abc";
		when(wordDispenser.getWord(wordIndex)).thenReturn(word);
		
		ResponseEntity<WordResponseBodyWrapper> callResult = wordDispenserController.wordIndexGet(wordIndex);
		
		verify(wordDispenser).getWord(wordIndex);
		
		assertThat(callResult.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(((WordResponseBody) callResult.getBody()).getWord()).isEqualTo(word);
	}

	@Test
	void translatesIllegaStateExceptionForWordTo500InternalServerError() {
		final int wordIndex = 42;
		String errorMessage = "test-generated illegal state exception";
		doThrow(new IllegalStateException(errorMessage)).when(wordDispenser).getWord(anyInt());
		
		ResponseEntity<WordResponseBodyWrapper> callResult = wordDispenserController.wordIndexGet(wordIndex);
		
		assertThat(callResult.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
		assertThat(((ErrorResponseBody) callResult.getBody()).getMessage()).isEqualTo(errorMessage);
	}

	@Test
	void translatesIllegaArgumentExceptionForWordTo400BadRequest() {
		final int wordIndex = 42;
		String errorMessage = "test-generated illegal argument exception";
		doThrow(new IllegalArgumentException(errorMessage)).when(wordDispenser).getWord(anyInt());
		
		ResponseEntity<WordResponseBodyWrapper> callResult = wordDispenserController.wordIndexGet(wordIndex);
		
		assertThat(callResult.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		assertThat(((ErrorResponseBody) callResult.getBody()).getMessage()).isEqualTo(errorMessage);
	}

	@SuppressWarnings("unchecked")
	@Test
	void translatesExceptionForListInto400BadRequest() {
		String errorMessage = "test-generated generic runtime exception";
		doThrow(new RuntimeException(errorMessage)).when(wordDispenser).setList(any(List.class));
		
		ResponseEntity<ListResponseBodyWrapper> callResult = wordDispenserController.listPut(Arrays.asList());
		
		assertThat(callResult.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		assertThat(((ErrorResponseBody) callResult.getBody()).getMessage()).isEqualTo(errorMessage);
	}
	
	@Test
	void providesHealthCheckEndpoint() {
		when(wordDispenser.isReady()).thenReturn(false);
		ResponseEntity<JsonNode> healthStatus = wordDispenserController.healthStatus();
		assertThat(healthStatus.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(healthStatus.getBody().get(STATUS_TAG).asText()).isEqualTo(STATUS_VALUE_STARTING);
		
		when(wordDispenser.isReady()).thenReturn(true);
		healthStatus = wordDispenserController.healthStatus();
		assertThat(healthStatus.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(healthStatus.getBody().get(STATUS_TAG).asText()).isEqualTo(STATUS_VALUE_UP);
	}
}

package demo.debugging.numberpair;

import static demo.debugging.numberpair.NumberPairController.STATUS_TAG;
import static demo.debugging.numberpair.NumberPairController.STATUS_VALUE_STARTING;
import static demo.debugging.numberpair.NumberPairController.STATUS_VALUE_UP;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

import demo.debugging.numberpair.NumberPairGenerator.NumberPair;
import demo.debugging.numberpair.model.EmptyResponseBody;
import demo.debugging.numberpair.model.ErrorResponseBody;
import demo.debugging.numberpair.model.PairResponseBody;
import demo.debugging.numberpair.model.RangeRequestBody;
import demo.debugging.numberpair.model.RangesResponseBodyWrapper;



@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class NumberPairControllerTest {

	@Mock
	private NumberPairGenerator numberPairGenerator;
	
	@Mock
	private NativeWebRequest nativeWebRequest; 
	
	@InjectMocks
	private NumberPairController numberPairController;
	
	@Test
	void forwardsRangeSetToService() {
		final int maxOne = 100;
		final int maxTwo = 200;
		RangeRequestBody request = new RangeRequestBody(maxOne, maxTwo);
		
		ResponseEntity<RangesResponseBodyWrapper> rangeResponse = numberPairController.rangesPut(request);
		
		assertThat(rangeResponse.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
		assertThat(rangeResponse.getBody()).isInstanceOfAny(EmptyResponseBody.class);
		verify(numberPairGenerator).setMaxOne(maxOne);
		verify(numberPairGenerator).setMaxTwo(maxTwo);
	}

	@Test
	void delegatesPairGenerationToService() {
		final int one = 100;
		final int two = 200;
		when(numberPairGenerator.generate()).thenReturn(new NumberPair(one, two));
		
		ResponseEntity<?> pairResponse = numberPairController.pairGet();
		
		verify(numberPairGenerator).generate();
		assertThat(pairResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(((PairResponseBody) pairResponse.getBody()).getOne()).isEqualTo(one);
		assertThat(((PairResponseBody) pairResponse.getBody()).getTwo()).isEqualTo(two);
	}
	
	@Test
	void forwardsIllegalArgumentsExceptionErrorFromRangeSet() {
		final String exceptionMessage = "test error from generator";
		doThrow(new IllegalArgumentException(exceptionMessage)).when(numberPairGenerator).setMaxOne(anyInt());
		doThrow(new IllegalArgumentException(exceptionMessage)).when(numberPairGenerator).setMaxTwo(anyInt());
		
		RangeRequestBody request = new RangeRequestBody(0, 0);
		ResponseEntity<RangesResponseBodyWrapper> rangeResponse = numberPairController.rangesPut(request);
		
		assertThat(rangeResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		assertThat(((ErrorResponseBody) rangeResponse.getBody()).getMessage()).isEqualTo(exceptionMessage);
	}
	
	@Test
	void forwardsGenericExceptionErrorRangeSet() {
		final String exceptionMessage = "test error from generator";
		doThrow(new RuntimeException(exceptionMessage)).when(numberPairGenerator).setMaxOne(anyInt());
		doThrow(new RuntimeException(exceptionMessage)).when(numberPairGenerator).setMaxTwo(anyInt());
		
		RangeRequestBody request = new RangeRequestBody(0, 0);
		ResponseEntity<RangesResponseBodyWrapper> rangeResponse = numberPairController.rangesPut(request);
		
		assertThat(rangeResponse.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
		assertThat(((ErrorResponseBody) rangeResponse.getBody()).getMessage()).isEqualTo(exceptionMessage);
	}
	
	@Test
	void forwardsExceptionFromGetPair() {
		final String exceptionMessage = "test error from generator";
		doThrow(new RuntimeException(exceptionMessage)).when(numberPairGenerator).generate();
		
		ResponseEntity<?> pairResponse = numberPairController.pairGet();
		
		assertThat(pairResponse.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
		assertThat(((ErrorResponseBody) pairResponse.getBody()).getMessage()).isEqualTo(exceptionMessage);
	}

	@Test
	void providesHealthCheckEndpoint() {
		when(numberPairGenerator.isReady()).thenReturn(false);
		ResponseEntity<JsonNode> healthStatus = numberPairController.healthStatus();
		assertThat(healthStatus.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(healthStatus.getBody().get(STATUS_TAG).asText()).isEqualTo(STATUS_VALUE_STARTING);
		
		when(numberPairGenerator.isReady()).thenReturn(true);
		healthStatus = numberPairController.healthStatus();
		assertThat(healthStatus.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(healthStatus.getBody().get(STATUS_TAG).asText()).isEqualTo(STATUS_VALUE_UP);
	}
}

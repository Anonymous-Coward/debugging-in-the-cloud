package demo.debugging.namebuilder;

import static demo.debugging.namebuilder.NameBuilderController.STATUS_TAG;
import static demo.debugging.namebuilder.NameBuilderController.STATUS_VALUE_UP;
import static org.assertj.core.api.Assertions.assertThat;
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
import org.springframework.web.client.RestClientException;
import org.springframework.web.context.request.NativeWebRequest;

import com.fasterxml.jackson.databind.JsonNode;

import demo.debugging.namebuilder.model.ErrorResponseBody;
import demo.debugging.namebuilder.model.NameResponseBody;
import demo.debugging.namebuilder.model.NameResponseBodyWrapper;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class NameBuilderControllerTest {

	@Mock
	private NameBuilder nameBuilder;
	
	@Mock
	private NativeWebRequest nativeWebRequest;

	@InjectMocks
	private NameBuilderController nameBuilderController;
	
	private static final String TEST_NAME = "adjective_noun";
	
	@Test
	void returnsValueFromServiceAs200OK() {
		when(nameBuilder.getName()).thenReturn(TEST_NAME);
		
		ResponseEntity<NameResponseBodyWrapper> callResult = nameBuilderController.nameGet();
		
		assertThat(callResult.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(((NameResponseBody) callResult.getBody()).getName()).isEqualTo(TEST_NAME);
	}
	
	@Test
	void convertsClientExceptionInto502Response() {
		RestClientException restClientException = new RestClientException("test-generated client exception");
		when(nameBuilder.getName()).thenThrow(restClientException);
		
		ResponseEntity<NameResponseBodyWrapper> callResult = nameBuilderController.nameGet();
		
		assertThat(callResult.getStatusCode()).isEqualTo(HttpStatus.BAD_GATEWAY);
		assertThat(((ErrorResponseBody) callResult.getBody()).getMessage()).isEqualTo(restClientException.getMessage());
	}

	@Test
	void convertsGenericExceptionInto500Response() {
		RuntimeException restClientException = new RuntimeException("test-generated generic exception");
		when(nameBuilder.getName()).thenThrow(restClientException);
		
		ResponseEntity<NameResponseBodyWrapper> callResult = nameBuilderController.nameGet();
		
		assertThat(callResult.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
		assertThat(((ErrorResponseBody) callResult.getBody()).getMessage()).isEqualTo(restClientException.getMessage());
	}

	@Test
	void providesHealthCheckEndpoint() {
		ResponseEntity<JsonNode> healthStatus = nameBuilderController.healthStatus();
		assertThat(healthStatus.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(healthStatus.getBody().get(STATUS_TAG).asText()).isEqualTo(STATUS_VALUE_UP);
	}
}

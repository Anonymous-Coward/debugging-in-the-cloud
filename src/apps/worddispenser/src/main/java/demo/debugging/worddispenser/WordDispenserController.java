package demo.debugging.worddispenser;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.context.request.NativeWebRequest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import demo.debugging.worddispenser.api.ListApi;
import demo.debugging.worddispenser.api.WordApi;
import demo.debugging.worddispenser.model.EmptyResponseBody;
import demo.debugging.worddispenser.model.ErrorResponseBody;
import demo.debugging.worddispenser.model.ListResponseBodyWrapper;
import demo.debugging.worddispenser.model.WordResponseBody;
import demo.debugging.worddispenser.model.WordResponseBodyWrapper;
import jakarta.annotation.Nonnull;
import jakarta.validation.Valid;

@Controller
public class WordDispenserController implements ListApi, WordApi {

	public static final String STATUS_VALUE_STARTING = "STARTING";
	public static final String STATUS_VALUE_UP = "UP";
	public static final String STATUS_TAG = "status";

	// NativeWebRequest is a wrapper around the actual request
	// Spring will update the wrapped request inside the wrapper for each request
	@Nonnull
	private NativeWebRequest nativeWebRequest;

	@Nonnull
	private WordDispenser wordDispenser;
	
	public WordDispenserController(
			@Nonnull NativeWebRequest nativeWebRequest,
			@Nonnull WordDispenser wordDispenser) {
		this.nativeWebRequest = nativeWebRequest;
		this.wordDispenser = wordDispenser;
	}
	
	@Override
	public Optional<NativeWebRequest> getRequest() {
		return Optional.of(nativeWebRequest);
	}

	@Override
	public ResponseEntity<WordResponseBodyWrapper> wordIndexGet(Integer index) {
		try {
			WordResponseBody responseBody = new WordResponseBody();
			responseBody.setWord(wordDispenser.getWord(index));
			return ResponseEntity.ok(responseBody);
		} catch(IllegalStateException ex) {
			ErrorResponseBody responseBody = new ErrorResponseBody();
			responseBody.setMessage(ex.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseBody);
		} catch(IllegalArgumentException ex) {
			ErrorResponseBody responseBody = new ErrorResponseBody();
			responseBody.setMessage(ex.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseBody);
		}
	}

	@Override
	public ResponseEntity<ListResponseBodyWrapper> listPut(@Valid List<String> requestBody) {
		try {
			wordDispenser.setList(requestBody);
			EmptyResponseBody responseBody = new EmptyResponseBody();
			return ResponseEntity.status(HttpStatus.ACCEPTED).body(responseBody);
		} catch (Exception ex) {
			ErrorResponseBody responseBody = new ErrorResponseBody();
			responseBody.setMessage(ex.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseBody);
		}
		
	}
	
	@GetMapping(path = "/health")
	public ResponseEntity<JsonNode> healthStatus() {
		ObjectNode status = JsonNodeFactory.instance.objectNode();
		String statusValue = wordDispenser.isReady() ? STATUS_VALUE_UP : STATUS_VALUE_STARTING;
		status.put(STATUS_TAG, statusValue);
		return ResponseEntity.ok(status);
	}
}

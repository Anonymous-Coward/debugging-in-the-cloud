package demo.debugging.numberpair;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.NativeWebRequest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import demo.debugging.numberpair.NumberPairGenerator.NumberPair;
import demo.debugging.numberpair.api.PairApi;
import demo.debugging.numberpair.api.RangesApi;
import demo.debugging.numberpair.model.EmptyResponseBody;
import demo.debugging.numberpair.model.ErrorResponseBody;
import demo.debugging.numberpair.model.PairResponseBody;
import demo.debugging.numberpair.model.PairResponseBodyWrapper;
import demo.debugging.numberpair.model.RangeRequestBody;
import demo.debugging.numberpair.model.RangesResponseBodyWrapper;
import jakarta.annotation.Nonnull;
import jakarta.validation.Valid;

@Controller
@RequestMapping(path = "/")
public class NumberPairController implements PairApi, RangesApi {
	
	public static final String STATUS_VALUE_STARTING = "STARTING";
	public static final String STATUS_VALUE_UP = "UP";
	public static final String STATUS_TAG = "status";

	// NativeWebRequest is a wrapper around the actual request
	// Spring will update the wrapped request inside the wrapper for each request
	@Nonnull
	private NativeWebRequest nativeWebRequest;
	
	@Nonnull
	private NumberPairGenerator numberPairGenerator;

	public NumberPairController(
			@Nonnull NativeWebRequest nativeWebRequest,
			@Nonnull NumberPairGenerator numberPairGenerator) {
        this.nativeWebRequest = nativeWebRequest;
        this.numberPairGenerator = numberPairGenerator;
    }

	@Override
	public Optional<NativeWebRequest> getRequest() {
		return Optional.of(nativeWebRequest);
	}
	
	@Override
	public ResponseEntity<RangesResponseBodyWrapper> rangesPut(@Valid RangeRequestBody rangesPutRequest) {
		try {
			numberPairGenerator.setMaxOne(rangesPutRequest.getMaxOne());
			numberPairGenerator.setMaxTwo(rangesPutRequest.getMaxTwo());
			return ResponseEntity.status(HttpStatus.ACCEPTED).body(new EmptyResponseBody());
		} catch(IllegalArgumentException ex) {
			ErrorResponseBody errorResponseBody = new ErrorResponseBody();
			errorResponseBody.setMessage(ex.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponseBody);
		} catch(Exception ex) {
			ErrorResponseBody errorResponseBody = new ErrorResponseBody();
			errorResponseBody.setMessage(ex.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponseBody);
		}
	}

	@Override
	public ResponseEntity<PairResponseBodyWrapper> pairGet() {
		try {
			NumberPair pair = numberPairGenerator.generate();
			return ResponseEntity.ok(new PairResponseBody(pair.one(), pair.two()));
		} catch(Exception ex) {
			ErrorResponseBody errorResponseBody = new ErrorResponseBody();
			errorResponseBody.setMessage(ex.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponseBody);
		}
	}

	@GetMapping(path = "/health")
	public ResponseEntity<JsonNode> healthStatus() {
		ObjectNode status = JsonNodeFactory.instance.objectNode();
		String statusValue = numberPairGenerator.isReady() ? STATUS_VALUE_UP : STATUS_VALUE_STARTING;
		status.put(STATUS_TAG, statusValue);
		return ResponseEntity.ok(status);
	}
}

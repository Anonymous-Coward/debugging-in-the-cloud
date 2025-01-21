package demo.debugging.namebuilder;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestClientException;
import org.springframework.web.context.request.NativeWebRequest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import demo.debugging.namebuilder.api.NameApi;
import demo.debugging.namebuilder.model.ErrorResponseBody;
import demo.debugging.namebuilder.model.NameResponseBody;
import demo.debugging.namebuilder.model.NameResponseBodyWrapper;
import jakarta.annotation.Nonnull;

@Controller
@RequestMapping(path = "/")
public class NameBuilderController implements NameApi {

	public static final String STATUS_TAG = "status";
	public static final String STATUS_VALUE_UP = "UP";

	@Nonnull
	private NativeWebRequest nativeWebRequest;

	@Nonnull
	private NameBuilder nameBuilder;
	
	public NameBuilderController(NativeWebRequest nativeWebRequest, NameBuilder nameBuilder) {
		super();
		this.nativeWebRequest = nativeWebRequest;
		this.nameBuilder = nameBuilder;
	}

	@Override
	public Optional<NativeWebRequest> getRequest() {
		return Optional.of(nativeWebRequest);
	}

	@Override
	public ResponseEntity<NameResponseBodyWrapper> nameGet() {
		try {
			return ResponseEntity.ok(new NameResponseBody().name(nameBuilder.getName()));
		} catch (RestClientException rcex) {
			ErrorResponseBody responseBody = new ErrorResponseBody();
			responseBody.setMessage(rcex.getMessage());
			rcex.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(responseBody);
		} catch (Exception ex) {
			ErrorResponseBody responseBody = new ErrorResponseBody();
			responseBody.setMessage(ex.getMessage());
			ex.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseBody);
		}
	}

	// the name generator is stateless, therefore always ready and healthy
	@GetMapping(path = "/health")
	public ResponseEntity<JsonNode> healthStatus() {
		ObjectNode status = JsonNodeFactory.instance.objectNode();
		status.put(STATUS_TAG, STATUS_VALUE_UP);
		return ResponseEntity.ok(status);
	}	
}

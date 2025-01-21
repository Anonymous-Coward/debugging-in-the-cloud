package demo.debugging.namebuilder;

import java.net.Socket;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509ExtendedTrustManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MarkerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import demo.debugging.numberpair.api.NumbersPairsApi;
import demo.debugging.worddispenser.api.WordsDefaultApi;
import jakarta.annotation.PostConstruct;

@SpringBootApplication
public class NameBuilderApplication {

	protected static final String LOG_MARKER = "NUMBER PAIRS";
	protected static final Logger LOGGER = LoggerFactory.getLogger(NameBuilderApplication.class);

	@Bean
	WordsDefaultApi nounsApi(@Value("${api.basepaths.nouns}") String basePath) {
		return wordsApi(basePath);
	}

	@Bean
	WordsDefaultApi adjectivesApi(@Value("${api.basepaths.adjectives}") String basePath) {
		return wordsApi(basePath);
	}

	@Bean
	NumbersPairsApi numbersPairsApi(@Value("${api.basepaths.numberpairs}") String basePath) {
		NumbersPairsApi api = new NumbersPairsApi();
		api.getApiClient().setBasePath(basePath);
		return api;
	}
	
	@PostConstruct
	void disableTlsCertificateValidation() throws NoSuchAlgorithmException, KeyManagementException {
		LOGGER.debug(MarkerFactory.getMarker(LOG_MARKER), "Injecting naive trust manager.");
		TrustManager[] trustAllCerts = new TrustManager[] {
			    new X509ExtendedTrustManager() {
					@Override
					public void checkClientTrusted(X509Certificate[] chain, String authType) // NOSONAR
							throws CertificateException {
					}
					@Override
					public void checkServerTrusted(X509Certificate[] chain, String authType) // NOSONAR
							throws CertificateException {
					}
					@Override
					public X509Certificate[] getAcceptedIssuers() {
						return new X509Certificate[0];
					}
					@Override
					public void checkClientTrusted(X509Certificate[] chain, String authType, Socket socket) // NOSONAR
							throws CertificateException {
					}
					@Override
					public void checkServerTrusted(X509Certificate[] chain, String authType, Socket socket) // NOSONAR
							throws CertificateException {
					}
					@Override
					public void checkClientTrusted(X509Certificate[] chain, String authType, SSLEngine engine) // NOSONAR
							throws CertificateException {
					}
					@Override
					public void checkServerTrusted(X509Certificate[] chain, String authType, SSLEngine engine) // NOSONAR
							throws CertificateException {
					}
			    }
			};
		LOGGER.debug(MarkerFactory.getMarker(LOG_MARKER), "Manipulating SSL context.");
	    SSLContext sslContext = SSLContext.getInstance("TLS");
	    LOGGER.debug(MarkerFactory.getMarker(LOG_MARKER), "Class of sslContext: {}", sslContext.getClass().getCanonicalName());
	    sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
	    LOGGER.debug(MarkerFactory.getMarker(LOG_MARKER), "Protocol served by context: {}", sslContext.getProtocol());
	    
	    HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
	    HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true); // NOSONAR not validating the hostname is the whole point
	}
	
	public static void main(String[] args) {
		SpringApplication.run(NameBuilderApplication.class, args);
	}
	
	private WordsDefaultApi wordsApi(String basePath) {
		WordsDefaultApi api = new WordsDefaultApi();
		api.getApiClient().setBasePath(basePath);
		return api;
	}
}

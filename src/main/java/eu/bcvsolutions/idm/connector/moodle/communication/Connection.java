package eu.bcvsolutions.idm.connector.moodle.communication;

import java.io.IOException;
import java.util.Map;

import org.identityconnectors.common.logging.Log;
import org.identityconnectors.framework.common.exceptions.ConnectionFailedException;
import org.identityconnectors.framework.common.exceptions.ConnectorException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import eu.bcvsolutions.idm.connector.moodle.MoodleConfiguration;
import eu.bcvsolutions.idm.connector.moodle.model.Error;

/**
 * @author Roman Kucera
 */
public class Connection {

	private static final Log LOG = Log.getLog(Connection.class);

	/**
	 * Wrapped method for GET call to end system
	 *
	 * @param url
	 * @return
	 */
	public HttpResponse<String> get(String url) {
		try {
			HttpResponse<String> response = Unirest.get(url)
					.header("content-type", "application/json")
					.asString();
			return response;
		} catch (UnirestException e) {
			throw new ConnectionFailedException("Connection failed", e);
		}
	}

	/**
	 * Wrapped method for POST call to end system
	 *
	 * @param url
	 * @param parameters
	 * @return
	 */
	public HttpResponse<String> post(String url, Map<String, Object> parameters) {
		try {
			HttpResponse<String> response = Unirest.post(url)
					.header("Content-Type", "application/x-www-form-urlencoded")
					.fields(parameters)
					.asString();
			return response;
		} catch (UnirestException e) {
			throw new ConnectionFailedException("Connection failed", e);
		}
	}

	/**
	 * Wrapped method for PUT call to end system
	 *
	 * @param url
	 * @param body
	 * @return
	 */
	public HttpResponse<String> put(String url, Object body) {
		try {
			HttpResponse<String> response = Unirest.put(url)
					.header("content-type", "application/json")
					.body(body)
					.asString();
			return response;
		} catch (UnirestException e) {
			throw new ConnectionFailedException("Connection failed", e);
		}
	}

	/**
	 * Wrapped method for DELETE call to end system
	 *
	 * @param url
	 * @return
	 */
	public HttpResponse<String> delete(String url) {
		try {
			HttpResponse<String> response = Unirest.delete(url)
					.header("content-type", "application/json")
					.asString();
			return response;
		} catch (UnirestException e) {
			throw new ConnectionFailedException("Connection failed", e);
		}
	}

	public ConnectorException handleError(HttpResponse<String> response, String operation) {
		if (response != null) {
			try {
				ObjectMapper jsonObjectMapper = new ObjectMapper();

				Error error = jsonObjectMapper.readValue(response.getBody().toString(), Error.class);
				LOG.error("Operation {0} failed, error code: {1}, exception: {2}, message: {3}", operation, error.getErrorcode(), error.getException(), error.getMessage());
				return new ConnectorException("Operation " + operation + " failed, error code: " + error.getErrorcode() + ",exception: " + error.getException() + ", message: " + error.getMessage());
			} catch (IOException ex) {
				LOG.error("Can not parse error response for operation {0} " + ex, operation);
				return new ConnectorException("Can not parse error response for operation " + operation);
			}
		} else {
			LOG.error("Response is null can't parse error message for operation {0}", operation);
			return new ConnectorException("Response is null can't parse error message for operation " + operation);
		}
	}

}

package eu.bcvsolutions.idm.connector.moodle.model;

/**
 * @author Roman Kucera
 */
public class Error {
	private String exception;
	private String errorcode;
	private String message;
	private String debuginfo;

	public String getException() {
		return exception;
	}

	public void setException(String exception) {
		this.exception = exception;
	}

	public String getErrorcode() {
		return errorcode;
	}

	public void setErrorcode(String errorcode) {
		this.errorcode = errorcode;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getDebuginfo() {
		return debuginfo;
	}

	public void setDebuginfo(String debuginfo) {
		this.debuginfo = debuginfo;
	}
}

package eu.bcvsolutions.idm.connector.moodle.model;

/**
 * @author Roman Kucera
 * Model class for attribute Preference which is in user detail
 */
public class Preference {
	private String name;
	private String value;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}

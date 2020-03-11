package eu.bcvsolutions.idm.connector.moodle.model;

/**
 * @author Roman Kucera
 */
public class ResponseGroup {
	private int id;
	private String name;
	private String idnumber;
	private String description;
	private int descriptionformat;
	private Boolean visible;
	private String theme;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIdnumber() {
		return idnumber;
	}

	public void setIdnumber(String idnumber) {
		this.idnumber = idnumber;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getDescriptionformat() {
		return descriptionformat;
	}

	public void setDescriptionformat(int descriptionformat) {
		this.descriptionformat = descriptionformat;
	}

	public Boolean getVisible() {
		return visible;
	}

	public void setVisible(Boolean visible) {
		this.visible = visible;
	}

	public String getTheme() {
		return theme;
	}

	public void setTheme(String theme) {
		this.theme = theme;
	}
}

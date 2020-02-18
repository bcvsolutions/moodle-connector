package eu.bcvsolutions.idm.connector.moodle.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author Roman Kucera
 */
public class ResponseUser {
	private int id;
	private String username;
	private String firstname;
	private String lastname;
	private String fullname;
	private String email;
	private String department;
	private int firstaccess;
	private int lastaccess;
	private String auth;
	private String suspended;
	private String confirmed;
	private String lang;
	private String theme;
	private String timezone;
	private int mailformat;
	private String description;
	private int descriptionformat;
	private String city;
	private String country;
	private String profileimageurlsmall;
	private String profileimageurl;
	private String idnumber;
	private String phone1;
	private String phone2;
	private String institution;
	private String profileurl;
	private String identity;
	@JsonIgnore
	private Preference[] preferences;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getFullname() {
		return fullname;
	}

	public void setFullname(String fullname) {
		this.fullname = fullname;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public int getFirstaccess() {
		return firstaccess;
	}

	public void setFirstaccess(int firstaccess) {
		this.firstaccess = firstaccess;
	}

	public int getLastaccess() {
		return lastaccess;
	}

	public void setLastaccess(int lastaccess) {
		this.lastaccess = lastaccess;
	}

	public String getAuth() {
		return auth;
	}

	public void setAuth(String auth) {
		this.auth = auth;
	}

	public String getSuspended() {
		return suspended;
	}

	public void setSuspended(String suspended) {
		this.suspended = suspended;
	}

	public String getConfirmed() {
		return confirmed;
	}

	public void setConfirmed(String confirmed) {
		this.confirmed = confirmed;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public String getTheme() {
		return theme;
	}

	public void setTheme(String theme) {
		this.theme = theme;
	}

	public String getTimezone() {
		return timezone;
	}

	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}

	public int getMailformat() {
		return mailformat;
	}

	public void setMailformat(int mailformat) {
		this.mailformat = mailformat;
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

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getProfileimageurlsmall() {
		return profileimageurlsmall;
	}

	public void setProfileimageurlsmall(String profileimageurlsmall) {
		this.profileimageurlsmall = profileimageurlsmall;
	}

	public String getProfileimageurl() {
		return profileimageurl;
	}

	public void setProfileimageurl(String profileimageurl) {
		this.profileimageurl = profileimageurl;
	}

	public String getIdnumber() {
		return idnumber;
	}

	public void setIdnumber(String idnumber) {
		this.idnumber = idnumber;
	}

	public String getPhone1() {
		return phone1;
	}

	public void setPhone1(String phone1) {
		this.phone1 = phone1;
	}

	public String getPhone2() {
		return phone2;
	}

	public void setPhone2(String phone2) {
		this.phone2 = phone2;
	}

	public String getInstitution() {
		return institution;
	}

	public void setInstitution(String institution) {
		this.institution = institution;
	}

	public String getProfileurl() {
		return profileurl;
	}

	public void setProfileurl(String profileurl) {
		this.profileurl = profileurl;
	}

	public String getIdentity() {
		return identity;
	}

	public void setIdentity(String identity) {
		this.identity = identity;
	}

	public Preference[] getPreferences() {
		return preferences;
	}

	public void setPreferences(Preference[] preferences) {
		this.preferences = preferences;
	}
}

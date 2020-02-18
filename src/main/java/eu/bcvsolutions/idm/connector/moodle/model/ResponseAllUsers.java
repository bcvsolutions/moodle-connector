package eu.bcvsolutions.idm.connector.moodle.model;

import java.util.List;

/**
 * @author Roman Kucera
 */
public class ResponseAllUsers {
	private List<ResponseUser> users;
	private int count;

	public List<ResponseUser> getUsers() {
		return users;
	}

	public void setUsers(List<ResponseUser> users) {
		this.users = users;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
}

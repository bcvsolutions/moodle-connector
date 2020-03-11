package eu.bcvsolutions.idm.connector.moodle.model;

import java.util.List;

/**
 * @author Roman Kucera
 */
public class ResponseGroupUsers {
	private int cohortid;
	private List<Integer> userids;

	public int getCohortid() {
		return cohortid;
	}

	public void setCohortid(int cohortid) {
		this.cohortid = cohortid;
	}

	public List<Integer> getUserids() {
		return userids;
	}

	public void setUserids(List<Integer> userids) {
		this.userids = userids;
	}
}

package eu.bcvsolutions.idm.connector.moodle.model;

import java.util.List;

/**
 * @author Roman Kucera
 * Model class for response of all groups from API
 */
public class ResponseAllGroups {
	private List<ResponseGroup> cohorts;

	public List<ResponseGroup> getCohorts() {
		return cohorts;
	}

	public void setCohorts(List<ResponseGroup> cohorts) {
		this.cohorts = cohorts;
	}
}

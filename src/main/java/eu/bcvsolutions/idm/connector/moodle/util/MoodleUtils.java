package eu.bcvsolutions.idm.connector.moodle.util;

import java.net.URISyntaxException;

import org.apache.http.client.utils.URIBuilder;
import org.identityconnectors.common.security.GuardedString;
import org.identityconnectors.framework.common.objects.AttributeBuilder;
import org.identityconnectors.framework.common.objects.ConnectorObjectBuilder;
import org.identityconnectors.framework.common.objects.ObjectClass;
import org.identityconnectors.framework.common.objects.ResultsHandler;
import org.identityconnectors.framework.common.objects.Uid;

import eu.bcvsolutions.idm.connector.moodle.MoodleConfiguration;
import eu.bcvsolutions.idm.connector.moodle.enmus.AttrNameEnum;
import eu.bcvsolutions.idm.connector.moodle.model.ResponseUser;

/**
 * @author Roman Kucera
 */
public class MoodleUtils {

	/**
	 * Get password as plain string
	 *
	 * @param password
	 * @return
	 */
	public String getPassword(GuardedString password) {
		GuardedStringAccessor accessor = new GuardedStringAccessor();
		password.access(accessor);
		char[] result = accessor.getArray();
		return new String(result);
	}

	public URIBuilder buildBaseUrl(MoodleConfiguration configuration) throws URISyntaxException {
		URIBuilder uriBuilder = new URIBuilder(configuration.getEndpoint() + "?wstoken=" + getPassword(configuration.getToken()));
		return uriBuilder;
	}

	public void handleUser(ObjectClass objectClass, ResultsHandler handler, ResponseUser user) {
		ConnectorObjectBuilder builder = new ConnectorObjectBuilder();
		builder.setUid(new Uid(String.valueOf(user.getId())));
		builder.setName(user.getUsername());
		builder.setObjectClass(objectClass);
		builder.addAttribute(AttributeBuilder.build(AttrNameEnum.firstname.toString(), user.getFirstname()));
		builder.addAttribute(AttributeBuilder.build(AttrNameEnum.lastname.toString(), user.getLastname()));
		builder.addAttribute(AttributeBuilder.build(AttrNameEnum.lastname.toString(), user.getLastname()));
		builder.addAttribute(AttributeBuilder.build(AttrNameEnum.email.toString(), user.getEmail()));
		builder.addAttribute(AttributeBuilder.build(AttrNameEnum.username.toString(), user.getUsername()));
		handler.handle(builder.build());
	}
}

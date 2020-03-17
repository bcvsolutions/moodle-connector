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
import eu.bcvsolutions.idm.connector.moodle.enmus.GroupAttrNameEnum;
import eu.bcvsolutions.idm.connector.moodle.enmus.UserAttrNameEnum;
import eu.bcvsolutions.idm.connector.moodle.model.ResponseGroup;
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

	/**
	 * get basic URL with token already as part of it
	 * @param configuration
	 * @return
	 * @throws URISyntaxException
	 */
	public URIBuilder buildBaseUrl(MoodleConfiguration configuration) throws URISyntaxException {
		return new URIBuilder(configuration.getEndpoint() + "?wstoken=" + getPassword(configuration.getToken()));
	}

	public String getUrlWithSecuredToken(String url) {
		return url.substring(0, url.indexOf("=") + 1) + "****" + url.substring(url.indexOf("&"));
	}

	/**
	 * Transform single user into ResultHandler
	 * @param objectClass
	 * @param handler
	 * @param user
	 */
	public void handleUser(ObjectClass objectClass, ResultsHandler handler, ResponseUser user) {
		ConnectorObjectBuilder builder = new ConnectorObjectBuilder();
		builder.setUid(new Uid(String.valueOf(user.getId())));
		builder.setName(user.getUsername());
		builder.setObjectClass(objectClass);
		builder.addAttribute(AttributeBuilder.build(UserAttrNameEnum.firstname.toString(), user.getFirstname()));
		builder.addAttribute(AttributeBuilder.build(UserAttrNameEnum.lastname.toString(), user.getLastname()));
		builder.addAttribute(AttributeBuilder.build(UserAttrNameEnum.email.toString(), user.getEmail()));
		builder.addAttribute(AttributeBuilder.build(UserAttrNameEnum.username.toString(), user.getUsername()));
		builder.addAttribute(AttributeBuilder.build(UserAttrNameEnum.city.toString(), user.getCity()));
		builder.addAttribute(AttributeBuilder.build(UserAttrNameEnum.country.toString(), user.getCountry()));
		builder.addAttribute(AttributeBuilder.build(UserAttrNameEnum.department.toString(), user.getDepartment()));
		builder.addAttribute(AttributeBuilder.build(UserAttrNameEnum.idnumber.toString(), user.getIdnumber()));
		builder.addAttribute(AttributeBuilder.build(UserAttrNameEnum.timezone.toString(), user.getTimezone()));

		if (user.getRoles() != null) {
			builder.addAttribute(AttributeBuilder.build(UserAttrNameEnum.roles.toString(), user.getRoles()));
		}

		handler.handle(builder.build());
	}

	/**
	 * Transform single group into ResultHandler
	 * @param objectClass
	 * @param handler
	 * @param group
	 */
	public void handleGroup(ObjectClass objectClass, ResultsHandler handler, ResponseGroup group) {
		ConnectorObjectBuilder builder = new ConnectorObjectBuilder();
		builder.setUid(new Uid(String.valueOf(group.getId())));
		builder.setName(String.valueOf(group.getId()));
		builder.setObjectClass(objectClass);
		builder.addAttribute(AttributeBuilder.build(GroupAttrNameEnum.id.toString(), group.getId()));
		builder.addAttribute(AttributeBuilder.build(GroupAttrNameEnum.name.toString(), group.getName()));
		builder.addAttribute(AttributeBuilder.build(GroupAttrNameEnum.description.toString(), group.getDescription()));
		builder.addAttribute(AttributeBuilder.build(GroupAttrNameEnum.descriptionformat.toString(), group.getDescriptionformat()));
		builder.addAttribute(AttributeBuilder.build(GroupAttrNameEnum.visible.toString(), group.getVisible()));
		builder.addAttribute(AttributeBuilder.build(GroupAttrNameEnum.theme.toString(), group.getTheme()));
		handler.handle(builder.build());
	}
}

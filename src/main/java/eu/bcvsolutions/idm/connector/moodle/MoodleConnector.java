package eu.bcvsolutions.idm.connector.moodle;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Set;

import org.identityconnectors.common.logging.Log;
import org.identityconnectors.common.security.GuardedString;
import org.identityconnectors.framework.common.exceptions.ConnectionFailedException;
import org.identityconnectors.framework.common.exceptions.ConnectorException;
import org.identityconnectors.framework.common.objects.Attribute;
import org.identityconnectors.framework.common.objects.AttributeInfoBuilder;
import org.identityconnectors.framework.common.objects.ObjectClass;
import org.identityconnectors.framework.common.objects.ObjectClassInfoBuilder;
import org.identityconnectors.framework.common.objects.OperationOptions;
import org.identityconnectors.framework.common.objects.ResultsHandler;
import org.identityconnectors.framework.common.objects.Schema;
import org.identityconnectors.framework.common.objects.SchemaBuilder;
import org.identityconnectors.framework.common.objects.Uid;
import org.identityconnectors.framework.common.objects.filter.AbstractFilterTranslator;
import org.identityconnectors.framework.common.objects.filter.EqualsFilter;
import org.identityconnectors.framework.common.objects.filter.FilterTranslator;
import org.identityconnectors.framework.spi.Configuration;
import org.identityconnectors.framework.spi.Connector;
import org.identityconnectors.framework.spi.ConnectorClass;
import org.identityconnectors.framework.spi.operations.CreateOp;
import org.identityconnectors.framework.spi.operations.DeleteOp;
import org.identityconnectors.framework.spi.operations.SchemaOp;
import org.identityconnectors.framework.spi.operations.SearchOp;
import org.identityconnectors.framework.spi.operations.TestOp;
import org.identityconnectors.framework.spi.operations.UpdateOp;

import eu.bcvsolutions.idm.connector.moodle.enmus.AttrNameEnum;
import eu.bcvsolutions.idm.connector.moodle.model.ResponseUser;
import eu.bcvsolutions.idm.connector.moodle.operation.CreateUser;
import eu.bcvsolutions.idm.connector.moodle.operation.DeleteUser;
import eu.bcvsolutions.idm.connector.moodle.operation.GetUser;
import eu.bcvsolutions.idm.connector.moodle.util.MoodleUtils;

/**
 * Moodle connector main class
 */
@ConnectorClass(configurationClass = MoodleConfiguration.class, displayNameKey = "moodle.connector.display")
public class MoodleConnector implements Connector,
		CreateOp, UpdateOp, DeleteOp,
		SchemaOp, TestOp, SearchOp<String> {

	private static final Log LOG = Log.getLog(MoodleConnector.class);

	private MoodleConfiguration configuration;

	@Override
	public MoodleConfiguration getConfiguration() {
		return configuration;
	}

	@Override
	public void init(final Configuration configuration) {
		this.configuration = (MoodleConfiguration) configuration;
		LOG.ok("Connector {0} successfully inited", getClass().getName());
	}

	@Override
	public void dispose() {
		// dispose of any resources the this connector uses.
	}

	@Override
	public Uid create(
			final ObjectClass objectClass,
			final Set<Attribute> createAttributes,
			final OperationOptions options) {
		if (objectClass.is(ObjectClass.ACCOUNT_NAME)) {
			CreateUser createUser = new CreateUser(configuration);
			try {
				ResponseUser user = createUser.createUser(createAttributes);
				return new Uid(String.valueOf(user.getId()));
			} catch (URISyntaxException e) {
				throw new ConnectorException("Error during preparing request URL:", e);
			}
		}
		throw new ConnectorException("Unsupported operation create for object class " + objectClass + "");
	}

	@Override
	public Uid update(
			final ObjectClass objectClass,
			final Uid uid,
			final Set<Attribute> replaceAttributes,
			final OperationOptions options) {

		return uid;
	}

	@Override
	public void delete(
			final ObjectClass objectClass,
			final Uid uid,
			final OperationOptions options) {
		if (objectClass.is(ObjectClass.ACCOUNT_NAME)) {
			DeleteUser deleteUser = new DeleteUser(configuration);
			try {
				deleteUser.deleteUser(uid.getUidValue());
				return;
			} catch (URISyntaxException e) {
				throw new ConnectorException("Error during preparing request URL:", e);
			}
		}
		throw new ConnectorException("Unsupported operation create for object class " + objectClass + "");
	}

	@Override
	public Schema schema() {
		// Schema for users
		ObjectClassInfoBuilder accountObjectClassBuilder = new ObjectClassInfoBuilder();
		accountObjectClassBuilder.setType(ObjectClass.ACCOUNT_NAME);
		accountObjectClassBuilder.addAttributeInfo(AttributeInfoBuilder.build(AttrNameEnum.username.toString(), String.class));
		accountObjectClassBuilder.addAttributeInfo(AttributeInfoBuilder.build("__PASSWORD__", GuardedString.class));
		accountObjectClassBuilder.addAttributeInfo(AttributeInfoBuilder.build(AttrNameEnum.firstname.toString(), String.class));
		accountObjectClassBuilder.addAttributeInfo(AttributeInfoBuilder.build(AttrNameEnum.lastname.toString(), String.class));
		accountObjectClassBuilder.addAttributeInfo(AttributeInfoBuilder.build(AttrNameEnum.email.toString(), String.class));
		accountObjectClassBuilder.addAttributeInfo(AttributeInfoBuilder.build(AttrNameEnum.idnumber.toString(), String.class));
		accountObjectClassBuilder.addAttributeInfo(AttributeInfoBuilder.build(AttrNameEnum.department.toString(), String.class));
		accountObjectClassBuilder.addAttributeInfo(AttributeInfoBuilder.build(AttrNameEnum.city.toString(), String.class));
		accountObjectClassBuilder.addAttributeInfo(AttributeInfoBuilder.build(AttrNameEnum.country.toString(), String.class));
		accountObjectClassBuilder.addAttributeInfo(AttributeInfoBuilder.build(AttrNameEnum.timezone.toString(), String.class));

		SchemaBuilder schemaBuilder = new SchemaBuilder(MoodleConnector.class);
		schemaBuilder.defineObjectClass(accountObjectClassBuilder.build());
		return schemaBuilder.build();
	}

	@Override
	public void test() {
		GetUser getUser = new GetUser(configuration);
		try {
			ResponseUser userByUsername = getUser.getUserByField(configuration.getUser(), AttrNameEnum.username.toString());
			if (userByUsername == null) {
				throw new ConnectionFailedException("Error during request, user not found, but connection probably working");
			}
		} catch (URISyntaxException e) {
			throw new ConnectorException("Error during preparing request URL:", e);
		}
	}

	@Override
	public FilterTranslator<String> createFilterTranslator(
			final ObjectClass objectClass,
			final OperationOptions options) {

		if (objectClass.is(ObjectClass.ACCOUNT_NAME) || objectClass.is(ObjectClass.GROUP_NAME)) {
			return new AbstractFilterTranslator<String>() {
				@Override
				protected String createEqualsExpression(EqualsFilter filter, boolean not) {
					if (not) {
						throw new UnsupportedOperationException("This type of equals expression is not allow for now.");
					}

					Attribute attr = filter.getAttribute();

					if (attr == null || !attr.is(Uid.NAME)) {
						throw new IllegalArgumentException("Attribute is null or not UID attribute.");
					}

					return ((Uid) attr).getUidValue();
				}
			};
		}
		return null;
	}

	@Override
	public void executeQuery(
			final ObjectClass objectClass,
			final String query,
			final ResultsHandler handler,
			final OperationOptions options) {
		// search one
		GetUser getUser = new GetUser(configuration);
		MoodleUtils moodleUtils = new MoodleUtils();
		if (query != null) {
			if (objectClass.is(ObjectClass.ACCOUNT_NAME)) {
				try {
					ResponseUser userByUsername = getUser.getUserByField(query, AttrNameEnum.id.toString());
					if (userByUsername != null) {
						moodleUtils.handleUser(objectClass, handler, userByUsername);
					}
				} catch (URISyntaxException e) {
					throw new ConnectorException("Error during preparing request URL:", e);
				}
			}
		} else {
			// search all
			try {
				List<ResponseUser> users = getUser.getUsers();
				users.forEach(responseUser -> moodleUtils.handleUser(objectClass, handler, responseUser));
			} catch (URISyntaxException e) {
				throw new ConnectorException("Error during preparing request URL:", e);
			}
		}
	}
}

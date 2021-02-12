package is.rebbi.wo.definitions;

import java.util.List;

public interface ProvidesEntityViewDefinitions {

	/**
	 * @return A list of EntityViewDefinitions this specifies.
	 */
	public List<EntityDefinition> entityViewDefinitions();

	/**
	 * @return The priority of this definition. Higher numbers override lower numbers.
	 */
	public default int priority() {
		return 0;
	}

	/**
	 * FIXME: This definitely does not belong here.
	 */
	public void defineRoutes();
}
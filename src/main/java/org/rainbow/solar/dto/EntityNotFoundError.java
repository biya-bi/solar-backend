/**
 * 
 */
package org.rainbow.solar.dto;

/**
 * @author biya-bi
 *
 */
public class EntityNotFoundError<T> extends SolarError {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2352375076198947283L;
	private T id;

	protected EntityNotFoundError() {
	}

	public EntityNotFoundError(int code, String message, T id) {
		super(code, message);
		this.id = id;
	}

	public T getId() {
		return id;
	}

}

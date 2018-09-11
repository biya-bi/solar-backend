/**
 * 
 */
package org.rainbow.solar.service;

/**
 * @author biya-bi
 *
 */
public interface Service<E, I> {
	E create(E e);

	E update(E e);

	void delete(E e);

	E getById(I id);

	boolean exists(I id);

	long count();
}

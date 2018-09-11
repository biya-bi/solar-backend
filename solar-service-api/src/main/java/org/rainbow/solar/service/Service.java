/**
 * 
 */
package org.rainbow.solar.service;

/**
 * @author biya-bi
 *
 */
public interface Service<E, I> {
	void create(E e);

	void update(E e);

	void delete(E e);

	E getById(I id);

	boolean exists(I id);

	long count();
}

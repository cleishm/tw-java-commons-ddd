package com.thoughtworks.ddd.hibernate;

import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.NonUniqueResultException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;

import com.thoughtworks.ddd.repository.NonUniqueObjectSelectedException;
import com.thoughtworks.ddd.repository.NullObjectAddedException;
import com.thoughtworks.ddd.specification.OrderComparator;
import com.thoughtworks.ddd.specification.Specification;

@SuppressWarnings("unchecked")
public abstract class HibernateRepository<T> {
	private final SessionFactory factory;
    private final Class<T> persistantClass;

    public HibernateRepository(final SessionFactory factory) {
        this.factory = factory;
        persistantClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    public void add(final T entity) throws NullObjectAddedException {
    	if (entity == null) {
    		throw new NullObjectAddedException();
    	}
        getCurrentSession().save(entity);
    }
    
    public void add(final Collection<T> entities) throws NullObjectAddedException {
    	for (final T entity : entities) {
    		add(entity);
    	}
    }

    public Set<T> selectAll() {
        return new HashSet<T>(getCurrentSession().createCriteria(persistantClass).list());
    }

    public Set<T> selectAll(final OrderComparator<T> comparator) {
        final Criteria criteria = getCurrentSession().createCriteria(persistantClass);
        comparator.populateCriteria(criteria);
        return new LinkedHashSet<T>(criteria.list());
    }

    public Set<T> selectSatisfying(final Specification<T> specification) {
        final Criteria criteria = getCurrentSession().createCriteria(persistantClass);
        specification.populateCriteria(criteria);
        final List<T> matchedObjects = criteria.list();
        return new LinkedHashSet<T>(matchedObjects);
    }

    public Set<T> selectSatisfying(final Specification<T> specification, final OrderComparator<T> comparator) {
        final Criteria criteria = getCurrentSession().createCriteria(persistantClass);
        specification.populateCriteria(criteria);
        comparator.populateCriteria(criteria);
        final List<T> matchedObjects = criteria.list();
        return new LinkedHashSet<T>(matchedObjects);
    }

    public int countSatisfying(final Specification<T> specification) {
        final Criteria criteria = getCurrentSession().createCriteria(persistantClass);
        criteria.setProjection(Projections.rowCount());
        specification.populateCriteria(criteria);
        final List result = criteria.list();
        return (Integer) result.get(0);
    }

    public T selectUnique(final Specification<T> specification) throws NonUniqueObjectSelectedException {
        final Criteria criteria = getCurrentSession().createCriteria(persistantClass);
        specification.populateCriteria(criteria);
        final T matchedObject;
        try {
            matchedObject = (T) criteria.uniqueResult();
        } catch (final NonUniqueResultException e) {
            throw new NonUniqueObjectSelectedException(e);
        }
        return matchedObject;
    }

    protected Session getCurrentSession() {
        return factory.getCurrentSession();
    }

}

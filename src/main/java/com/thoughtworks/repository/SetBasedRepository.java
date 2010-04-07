package com.thoughtworks.repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.thoughtworks.specification.Specification;

public class SetBasedRepository<T> {
    private final Set<T> objectSet;

    public SetBasedRepository() {
        objectSet = new HashSet<T>();
    }
    
    public SetBasedRepository(final Collection<T> entities) {
        objectSet = new HashSet<T>(entities);
    }

    public void add(final T entity) throws NullObjectAddedException {
        if (entity == null) {
            throw new NullObjectAddedException();
        }
        objectSet.add(entity);
    }
    
    public void add(final Collection<T> entities) throws NullObjectAddedException {
    	if (entities == null) {
    		throw new IllegalArgumentException();
    	}
    	for (final T entity: entities) {
    		if (entity == null) {
    			throw new NullObjectAddedException();
    		}
    	}
    	objectSet.addAll(entities);
    }
    
    public Set<T> selectAll() {
    	return new HashSet<T>(objectSet);
    }
    
    public Set<T> selectAll(final Comparator<T> comparator) {
    	final List<T> result = new ArrayList<T>(objectSet);
    	Collections.sort(result, comparator);
    	return new LinkedHashSet<T>(result);
    }

    public Set<T> selectSatisfying(final Specification<T> specification) {
        return selectSatisfyingIntoCollection(specification, new HashSet<T>());
    }

    public Set<T> selectSatisfying(final Specification<T> specification, final Comparator<T> comparator) {
        final List<T> result = selectSatisfyingIntoCollection(specification, new ArrayList<T>());
        Collections.sort(result, comparator);
        return new LinkedHashSet<T>(result);
    }

    public T selectUnique(final Specification<T> specification) throws NonUniqueObjectSelectedException {
        final List<T> results = selectSatisfyingIntoCollection(specification, new ArrayList<T>());
        if (results.size() == 1) {
            return results.get(0);
        } else if (!results.isEmpty()) {
            throw new NonUniqueObjectSelectedException();
        }
        return null;
    }

    private <C extends Collection<T>> C selectSatisfyingIntoCollection(final Specification<T> specification,
            final C target) {
        for (T object : objectSet) {
            if (specification.isSatisfiedBy(object)) {
                target.add(object);
            }
        }
        return target;
    }

}

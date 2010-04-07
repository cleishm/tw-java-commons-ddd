package com.thoughtworks.specification;

import org.hibernate.Criteria;

public class MatchAllSpecification<T> implements Specification<T> {

    public boolean isSatisfiedBy(final T object) {
        return true;
    }

    public void populateCriteria(final Criteria criteria) {
        throw new UnsupportedOperationException("not implemented");
    }

}

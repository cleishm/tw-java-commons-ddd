package com.thoughtworks.specification;

import org.hibernate.Criteria;

public interface Specification<T> {

    boolean isSatisfiedBy(T object);

    void populateCriteria(Criteria criteria);

}

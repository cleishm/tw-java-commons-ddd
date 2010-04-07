package com.thoughtworks.specification;

import java.util.Comparator;

import org.hibernate.Criteria;

public interface OrderComparator<T> extends Comparator<T> {
    void populateCriteria(Criteria criteria);
}

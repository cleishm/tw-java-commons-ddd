package com.thoughtworks.specification;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.Criteria;

public class AndSpecification<T> implements Specification<T> {
    private final List<Specification<T>> subSpecifications;

    public <S extends Specification<T>> AndSpecification(final Collection<S> specifications) {
        subSpecifications = new ArrayList<Specification<T>>(specifications);
    }

    public AndSpecification(final Specification<T>... specifications) {
        subSpecifications = Arrays.asList(specifications);
    }

    public boolean isSatisfiedBy(final T object) {
        for (final Specification<T> specification : subSpecifications) {
            if (!specification.isSatisfiedBy(object)) {
                return false;
            }
        }
        return true;
    }

    public void populateCriteria(final Criteria criteria) {
        for (final Specification<T> specification : subSpecifications) {
            specification.populateCriteria(criteria);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(final Object obj) {
        if (!(obj instanceof AndSpecification)) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        final AndSpecification other = (AndSpecification) obj;
        return CollectionUtils.isEqualCollection(subSpecifications, other.subSpecifications);
    }

    @Override
    public int hashCode() {
        final HashCodeBuilder hashCodeBuilder = new HashCodeBuilder();
        for (final Specification<T> specification : subSpecifications) {
            hashCodeBuilder.append(specification);
        }
        return hashCodeBuilder.toHashCode();
    }
}

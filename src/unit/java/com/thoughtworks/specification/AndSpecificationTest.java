package com.thoughtworks.specification;

import static junit.framework.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.junit.Test;

@SuppressWarnings("unchecked")
public class AndSpecificationTest {

    @Test
    public void isSatisifiedByShouldInvokeIsSatisfiedByOnAllContainedSpecificationsWhenObjectMatches() throws Exception {
        final Object object = new Object();
        final Set<Specification<Object>> specifications = new HashSet<Specification<Object>>();

        for (int i = 10; i > 0; --i) {
            final Specification<Object> mockSpecification = mock(Specification.class);
            when(mockSpecification.isSatisfiedBy(same(object))).thenReturn(true);
            specifications.add(mockSpecification);
        }

        final AndSpecification<Object> composite = new AndSpecification<Object>(specifications);
        assertTrue(composite.isSatisfiedBy(object));

        for (final Specification<Object> specification : specifications) {
            verify(specification).isSatisfiedBy(same(object));
        }
    }

    @Test
    public void isSatisifiedByShouldInvokeIsSatisfiedByOnAllContainedSpecificationsUntilFailure() throws Exception {
        final Object object = new Object();
        final List<Specification<Object>> specifications = new ArrayList<Specification<Object>>();

        for (int i = 10; i > 0; --i) {
            final Specification<Object> mockSpecification = mock(Specification.class);
            when(mockSpecification.isSatisfiedBy(same(object))).thenReturn(true);
            specifications.add(mockSpecification);
        }

        when(specifications.get(5).isSatisfiedBy(same(object))).thenReturn(false);

        final AndSpecification<Object> composite = new AndSpecification<Object>(specifications);
        assertFalse(composite.isSatisfiedBy(object));

        int i = 0;
        for (final Specification<Object> specification : specifications) {
            if (i++ <= 5) {
                verify(specification).isSatisfiedBy(same(object));
            } else {
                verifyZeroInteractions(specification);
            }
        }
    }

    @Test
    public void populateCriteriaShouldInvokePopulateCriteriaOnAllContainedSpecifications() throws Exception {
        final Set<Specification<Object>> specifications = new HashSet<Specification<Object>>();

        for (int i = 10; i > 0; --i) {
            final Specification<Object> mockSpecification = mock(Specification.class);
            specifications.add(mockSpecification);
        }

        final Criteria mockCriteria = mock(Criteria.class);
        final AndSpecification<Object> composite = new AndSpecification<Object>(specifications);
        composite.populateCriteria(mockCriteria);

        for (final Specification<Object> specification : specifications) {
            verify(specification).populateCriteria(same(mockCriteria));
        }
    }

    @Test
    public void shouldReturnEqualWhenAllContainedConstraintsAreAlsoEqual() throws Exception {
        final Specification specification1 = mock(Specification.class);
        final Specification specification2 = mock(Specification.class);
        final Specification specification3 = mock(Specification.class);

        final AndSpecification<Object> compositeSpecification = new AndSpecification<Object>(Arrays.asList(
                specification1, specification2, specification3));
        final AndSpecification<Object> compositeSpecification2 = new AndSpecification<Object>(specification1,
                specification2, specification3);
        assertEquals(compositeSpecification, compositeSpecification2);
    }

    @Test
    public void shouldNotReturnEqualsWhenAllContainedConstraintsAreNotEqual() throws Exception {
        final Specification specification1 = mock(Specification.class);
        final Specification specification2 = mock(Specification.class);
        final Specification specification3 = mock(Specification.class);
        final Specification specification4 = mock(Specification.class);

        final AndSpecification<Object> compositeSpecification = new AndSpecification<Object>(Arrays.asList(
                specification1, specification2, specification3));
        final AndSpecification<Object> compositeSpecification2 = new AndSpecification<Object>(Arrays.asList(
                specification1, specification2, specification4));
        assertFalse(compositeSpecification.equals(compositeSpecification2));
    }

    @Test
    public void shouldNotReturnEqualsWhenContainedCollectionsSizeIsDifferent() throws Exception {
        final Specification specification1 = mock(Specification.class);
        final Specification specification2 = mock(Specification.class);
        final Specification specification3 = mock(Specification.class);

        final AndSpecification<Object> compositeSpecification = new AndSpecification<Object>(Arrays.asList(
                specification1, specification2, specification3));
        final AndSpecification<Object> compositeSpecification2 = new AndSpecification<Object>(Arrays
                .asList(specification1));
        assertFalse(compositeSpecification.equals(compositeSpecification2));
    }

    @Test
    public void shouldReturnSameHashCodeForEqualContainedSpecifications() throws Exception {
        final Specification specification1 = mock(Specification.class);
        final Specification specification2 = mock(Specification.class);

        final AndSpecification<Object> compositeSpecification = new AndSpecification<Object>(specification1,
                specification2);
        final AndSpecification<Object> compositeSpecification2 = new AndSpecification<Object>(Arrays.asList(
                specification1, specification2));
        assertEquals(compositeSpecification.hashCode(), compositeSpecification2.hashCode());
    }

    @Test
    public void bothConstructorsShouldResultInSameSpecificationWhenArgumentsEqual() throws Exception {
        final Specification specification1 = mock(Specification.class);
        final Specification specification2 = mock(Specification.class);
        final Specification specification3 = mock(Specification.class);
        final Specification specification4 = mock(Specification.class);

        final Collection<Specification<Object>> specifications = new HashSet<Specification<Object>>();
        specifications.add(specification1);
        specifications.add(specification2);
        specifications.add(specification3);
        specifications.add(specification4);

        final AndSpecification<Object> compositeSpecification1 = new AndSpecification<Object>(specifications);
        final AndSpecification<Object> compositeSpecification2 = new AndSpecification<Object>(specification1,
                specification2, specification3, specification4);
        assertEquals(compositeSpecification1, compositeSpecification2);
    }

    @Test
    public void bothConstructorsShouldResultInDifferentSpecificationWhenArgumentsNotEqual() throws Exception {
        final Specification specification1 = mock(Specification.class);
        final Specification specification2 = mock(Specification.class);
        final Specification specification3 = mock(Specification.class);
        final Specification specification4 = mock(Specification.class);

        final Collection<Specification<Object>> specifications = new HashSet<Specification<Object>>();
        specifications.add(specification1);
        specifications.add(specification2);
        specifications.add(specification4);

        final AndSpecification<Object> compositeSpecification1 = new AndSpecification<Object>(specifications);
        final AndSpecification<Object> compositeSpecification2 = new AndSpecification<Object>(specification1,
                specification2, specification3);
        assertFalse(compositeSpecification1.equals(compositeSpecification2));
        assertFalse(compositeSpecification2.equals(compositeSpecification1));
    }

}

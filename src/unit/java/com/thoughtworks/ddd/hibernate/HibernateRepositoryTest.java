package com.thoughtworks.ddd.hibernate;

import static junit.framework.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.NonUniqueResultException;
import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;
import org.hibernate.criterion.RowCountProjection;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;

import com.thoughtworks.ddd.hibernate.HibernateRepository;
import com.thoughtworks.ddd.repository.NonUniqueObjectSelectedException;
import com.thoughtworks.ddd.repository.NullObjectAddedException;
import com.thoughtworks.ddd.specification.OrderComparator;
import com.thoughtworks.ddd.specification.Specification;

public class HibernateRepositoryTest {
    private Session mockSession;
    private Criteria mockCriteria;
    private Specification<MyEntity> mockSpecification;
    private OrderComparator<MyEntity> mockComparator;
    private HibernateRepository<MyEntity> repository;

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() throws Exception {
        final SessionFactory mockFactory = mock(SessionFactory.class);
        mockSession = mock(Session.class);
        mockCriteria = mock(Criteria.class);
        mockSpecification = mock(Specification.class);
        mockComparator = mock(OrderComparator.class);
        when(mockFactory.getCurrentSession()).thenReturn(mockSession);
        when(mockSession.createCriteria(eq(MyEntity.class))).thenReturn(mockCriteria);

        repository = new HibernateRepository<MyEntity>(mockFactory) { };
    }

    @Test
    public void addShouldAddObjectToSession() throws Exception {
        final MyEntity entity = new MyEntity();
        repository.add(entity);
        verify(mockSession).save(same(entity));
    }
    
    @Test
    public void addShouldAddAllObjectsToSession() throws Exception {
    	final Set<MyEntity> entities = new HashSet<MyEntity>();
        final MyEntity entity1 = new MyEntity();
		entities.add(entity1);
		final MyEntity entity2 = new MyEntity();
        entities.add(entity2);
        repository.add(entities);
        verify(mockSession).save(same(entity1));
        verify(mockSession).save(same(entity2));
    }
    
    @Test(expected = NullObjectAddedException.class)
    public void shouldNotAddEntitiesWhenAnyEntityIsNull() throws Exception {
        final Set<MyEntity> entities = new HashSet<MyEntity>();
        entities.add(new MyEntity());
        entities.add(null);
        entities.add(new MyEntity());
        try {
        	repository.add(entities);
        } catch (NullObjectAddedException e) {
        	assertTrue(repository.selectAll().isEmpty());
        	throw e;
        }
    }
    
    @Test(expected = NullObjectAddedException.class)
    public void shouldNotAddEntityWhenEntityIsNull() throws Exception {
        repository.add((MyEntity)null);
    }

    @Test
    public void selectAllShouldReturnAllResultsFromBasicCriteria() throws Exception {
        final MyEntity expectedObject = new MyEntity();
        when(mockCriteria.list()).thenReturn(Collections.singletonList(expectedObject));

        final Set<MyEntity> result = repository.selectAll();
        assertEquals(Collections.singleton(expectedObject), result);
    }

    @Test
    public void selectAllShouldUseComparatorFromSpecification() throws Exception {
        final MyEntity expectedObject = new MyEntity();
        when(mockCriteria.list()).thenReturn(Collections.singletonList(expectedObject));

        final Set<MyEntity> result = repository.selectAll(mockComparator);
        assertEquals(Collections.singleton(expectedObject), result);

        final InOrder order = inOrder(mockComparator, mockCriteria);
        order.verify(mockComparator).populateCriteria(same(mockCriteria));
        order.verify(mockCriteria).list();
    }

    @Test
    public void selectSatisfyingShouldUseCriteriaFromSpecification() throws Exception {
        final MyEntity expectedObject = new MyEntity();
        when(mockCriteria.list()).thenReturn(Collections.singletonList(expectedObject));

        final Set<MyEntity> result = repository.selectSatisfying(mockSpecification);
        assertEquals(Collections.singleton(expectedObject), result);

        final InOrder order = inOrder(mockSpecification, mockCriteria);
        order.verify(mockSpecification).populateCriteria(same(mockCriteria));
        order.verify(mockCriteria).list();
    }

    @Test
    public void selectSatisfyingShouldUseCriteriaAndComparatorFromSpecification() throws Exception {
        final MyEntity expectedObject = new MyEntity();
        when(mockCriteria.list()).thenReturn(Collections.singletonList(expectedObject));

        final Set<MyEntity> result = repository.selectSatisfying(mockSpecification, mockComparator);
        assertEquals(Collections.singleton(expectedObject), result);

        final InOrder order = inOrder(mockSpecification, mockComparator, mockCriteria);
        order.verify(mockSpecification).populateCriteria(same(mockCriteria));
        order.verify(mockComparator).populateCriteria(same(mockCriteria));
        order.verify(mockCriteria).list();
    }

    @Test
    public void countSatisfyingShouldUseCriteriaFromSpecification() throws Exception {
        final int expectedCount = 10;
        when(mockCriteria.list()).thenReturn(Collections.singletonList(expectedCount));

        assertEquals(expectedCount, repository.countSatisfying(mockSpecification));

        final InOrder order = inOrder(mockSpecification, mockCriteria);
        order.verify(mockCriteria).setProjection(isA(RowCountProjection.class));
        order.verify(mockSpecification).populateCriteria(same(mockCriteria));
        order.verify(mockCriteria).list();
    }

    @Test
    public void selectUniqueShouldUseCriteriaFromSpecificationAndReturnSingleResult() throws Exception {
        final MyEntity expectedObject = new MyEntity();
        when(mockCriteria.uniqueResult()).thenReturn(expectedObject);

        final MyEntity result = repository.selectUnique(mockSpecification);
        assertSame(expectedObject, result);

        final InOrder order = inOrder(mockSpecification, mockCriteria);
        order.verify(mockSpecification).populateCriteria(same(mockCriteria));
        order.verify(mockCriteria).uniqueResult();
    }

    @Test
    public void selectUniqueShouldReturnNullIfNoResultsMatchSpecification() throws Exception {
        when(mockCriteria.uniqueResult()).thenReturn(null);

        final MyEntity result = repository.selectUnique(mockSpecification);
        assertNull(result);

        final InOrder order = inOrder(mockSpecification, mockCriteria);
        order.verify(mockSpecification).populateCriteria(same(mockCriteria));
        order.verify(mockCriteria).uniqueResult();
    }

    @Test
    public void selectUniqueShouldThrowExceptionIfMultipeResultsMatchSpecification() throws Exception {
        final List<MyEntity> results = new ArrayList<MyEntity>();
        results.add(new MyEntity());
        results.add(new MyEntity());
        final NonUniqueResultException exception = new NonUniqueResultException(10);
        when(mockCriteria.uniqueResult()).thenThrow(exception);

        try {
            repository.selectUnique(mockSpecification);
        } catch (final NonUniqueObjectSelectedException e) {
            assertSame(exception, e.getCause());
        }

        final InOrder order = inOrder(mockSpecification, mockCriteria);
        order.verify(mockSpecification).populateCriteria(same(mockCriteria));
        order.verify(mockCriteria).uniqueResult();
    }

    private static final class MyEntity {
    }

}

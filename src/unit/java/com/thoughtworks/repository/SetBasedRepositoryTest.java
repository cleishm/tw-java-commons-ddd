package com.thoughtworks.repository;

import static junit.framework.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.thoughtworks.specification.MatchAllSpecification;
import com.thoughtworks.specification.Specification;

@SuppressWarnings("unchecked")
public class SetBasedRepositoryTest {
    private static final Set<MyEntity> EMPTY_ENTITY_SET = Collections.emptySet();

    private MyEntity entity;
    private Specification<MyEntity> mockSpecification;

    @Before
    public void setup() {
        entity = new MyEntity();
        mockSpecification = mock(Specification.class);
    }
    
    @Test
    public void selectAllShouldReturnAllResultsFromBasicCriteria() throws Exception {
        final Set<MyEntity> entities = Collections.singleton(entity);
        final SetBasedRepository<MyEntity> repository = new SetBasedRepository<MyEntity>(entities);
        final Set<MyEntity> selectedEntities = repository.selectAll();
		assertNotSame(entities, selectedEntities);
        assertEquals(entities, selectedEntities);
    }

    @Test
    public void selectAllShouldUseComparatorFromSpecification() throws Exception {
        final MyEntity entity1 = new MyEntity();
        final MyEntity entity2 = new MyEntity();

        final Set<MyEntity> entities = new HashSet<MyEntity>();
        entities.add(entity1);
        entities.add(entity2);

        final Comparator<MyEntity> comparator = new Comparator<MyEntity>() {
            public int compare(final MyEntity e1, final MyEntity e2) {
                return (e1.equals(entity1)) ? 1 : -1;
            }
        };

        final SetBasedRepository<MyEntity> repository = new SetBasedRepository<MyEntity>(entities);
        final Set<MyEntity> selectedEntities = repository.selectAll(comparator);
        assertNotSame(entities, selectedEntities);
        assertEquals(Arrays.asList(entity2, entity1), new ArrayList<MyEntity>(selectedEntities));
    }

    @Test
    public void shouldReturnNoEntitiesFromSelectSatisfyingWhenRepositoryIsEmpty() {
        final SetBasedRepository<MyEntity> repository = new SetBasedRepository<MyEntity>(EMPTY_ENTITY_SET);
        assertTrue(repository.selectSatisfying(mockSpecification).isEmpty());
    }

    @Test
    public void shouldReturnAllEntitiesWhenSpecificationMatchesAllEntities() {
        final Set<MyEntity> entities = Collections.singleton(entity);
        final SetBasedRepository<MyEntity> repository = new SetBasedRepository<MyEntity>(entities);
        when(mockSpecification.isSatisfiedBy(same(entity))).thenReturn(true);
        assertEquals(entities, repository.selectSatisfying(mockSpecification));
    }

    @Test
    public void shouldReturnNoEntitiesWhenSpecificationMatchesNoEntities() {
        final Set<MyEntity> entities = Collections.singleton(entity);
        final SetBasedRepository<MyEntity> repository = new SetBasedRepository<MyEntity>(entities);
        when(mockSpecification.isSatisfiedBy(same(entity))).thenReturn(false);
        assertTrue(repository.selectSatisfying(mockSpecification).isEmpty());
    }

    @Test
    public void shouldReturnOrderedSetContainingMatchedEntitiesWhenSelectSatisfyingUsingSpecifiedComparator() {
        final MyEntity entity1 = new MyEntity();
        final MyEntity entity2 = new MyEntity();

        final Set<MyEntity> entities = new HashSet<MyEntity>();
        entities.add(entity1);
        entities.add(entity2);

        final Specification<MyEntity> mockSpecification = mock(Specification.class);
        when(mockSpecification.isSatisfiedBy(same(entity1))).thenReturn(true);
        when(mockSpecification.isSatisfiedBy(same(entity2))).thenReturn(true);

        final Comparator<MyEntity> comparator = new Comparator<MyEntity>() {
            public int compare(final MyEntity e1, final MyEntity e2) {
                return (e1.equals(entity1)) ? 1 : -1;
            }
        };

        final SetBasedRepository<MyEntity> repository = new SetBasedRepository<MyEntity>(entities);
        final Set<MyEntity> selectedEntities = repository.selectSatisfying(mockSpecification, comparator);
        assertEquals(Arrays.asList(entity2, entity1), new ArrayList<MyEntity>(selectedEntities));
    }

    @Test(expected = NonUniqueObjectSelectedException.class)
    public void shouldFailWhenNonUniqueResultDuringSelectUnique() throws Exception {
        final Set<MyEntity> entities = new HashSet<MyEntity>();
        entities.add(new MyEntity());
        entities.add(new MyEntity());
        final SetBasedRepository<MyEntity> repository = new SetBasedRepository<MyEntity>(entities);
        repository.selectUnique(new MatchAllSpecification<MyEntity>());
    }

    @Test
    public void shouldReturnUniqueResultFromSelectUnique() throws Exception {
        final MyEntity entity1 = new MyEntity();
        final MyEntity entity2 = new MyEntity();

        final Set<MyEntity> entities = new HashSet<MyEntity>();
        entities.add(entity1);
        entities.add(entity2);
        final SetBasedRepository<MyEntity> repository = new SetBasedRepository<MyEntity>(entities);
        final Specification<MyEntity> specification = mock(Specification.class);
        when(specification.isSatisfiedBy(same(entity1))).thenReturn(false);
        when(specification.isSatisfiedBy(same(entity2))).thenReturn(true);
        final MyEntity result = repository.selectUnique(specification);
        assertEquals(entity2, result);
    }

    @Test
    public void shouldReturnNullFromSelectUniqueIfNoMatches() throws Exception {
        final Set<MyEntity> entities = new HashSet<MyEntity>();
        entities.add(new MyEntity());
        entities.add(new MyEntity());
        final SetBasedRepository<MyEntity> repository = new SetBasedRepository<MyEntity>(entities);
        final Specification<MyEntity> specification = mock(Specification.class);
        when(specification.isSatisfiedBy(isA(MyEntity.class))).thenReturn(false);
        final MyEntity result = repository.selectUnique(specification);
        assertNull(result);
    }

    @Test
    public void shouldAddEntityToSet() throws Exception {
        final SetBasedRepository<MyEntity> repository = new SetBasedRepository<MyEntity>();
        repository.add(entity);
        when(mockSpecification.isSatisfiedBy(same(entity))).thenReturn(true);
        final MyEntity repositoryEntity = repository.selectUnique(mockSpecification);
        assertSame(repositoryEntity, entity);
    }
    
    @Test
    public void shouldAddAllEntitiesToSet() throws Exception {
        final Set<MyEntity> entities = new HashSet<MyEntity>();
        entities.add(new MyEntity());
        entities.add(new MyEntity());
        final SetBasedRepository<MyEntity> repository = new SetBasedRepository<MyEntity>();
        repository.add(entities);
        final Set<MyEntity> repositoryEntities = repository.selectAll();
        assertEquals(repositoryEntities, entities);
    }

    @Test(expected = NullObjectAddedException.class)
    public void shouldNotAddEntityWhenEntityIsNull() throws Exception {
        final SetBasedRepository<MyEntity> repository = new SetBasedRepository<MyEntity>(new HashSet());
        repository.add((MyEntity)null);
    }
    
    @Test(expected = NullObjectAddedException.class)
    public void shouldNotAddEntitiesWhenAnyEntityIsNull() throws Exception {
        final Set<MyEntity> entities = new HashSet<MyEntity>();
        entities.add(new MyEntity());
        entities.add(null);
        entities.add(new MyEntity());
        final SetBasedRepository<MyEntity> repository = new SetBasedRepository<MyEntity>();
        try {
        	repository.add(entities);
        } catch (NullObjectAddedException e) {
        	assertTrue(repository.selectAll().isEmpty());
        	throw e;
        }
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAddEntitiesWhenAnyCollectionIsNull() throws Exception {
        final SetBasedRepository<MyEntity> repository = new SetBasedRepository<MyEntity>();
        repository.add((Collection<MyEntity>)null);
    }

    private static final class MyEntity {
    }

}

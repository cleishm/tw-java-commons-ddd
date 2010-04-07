package com.thoughtworks.ddd.specification;

import static junit.framework.Assert.*;

import org.junit.Test;

import com.thoughtworks.ddd.specification.MatchAllSpecification;

public class MatchAllSpecificationTest {

    @Test
    public void shouldBeSatisfiedByAnyObject() {
        assertTrue(new MatchAllSpecification<Object>().isSatisfiedBy(new Object()));
    }

}

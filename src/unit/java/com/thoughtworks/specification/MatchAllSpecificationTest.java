package com.thoughtworks.specification;

import static junit.framework.Assert.*;

import org.junit.Test;

public class MatchAllSpecificationTest {

    @Test
    public void shouldBeSatisfiedByAnyObject() {
        assertTrue(new MatchAllSpecification<Object>().isSatisfiedBy(new Object()));
    }

}

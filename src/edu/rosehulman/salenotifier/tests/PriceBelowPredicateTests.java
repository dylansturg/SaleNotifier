package edu.rosehulman.salenotifier.tests;

import org.junit.Test;

import edu.rosehulman.salenotifier.models.Item;
import edu.rosehulman.salenotifier.notifications.PriceBelowPredicate;

import junit.framework.Assert;
import junit.framework.TestCase;

public class PriceBelowPredicateTests extends TestCase {

	@Test
	public void testNullItemShouldThrowException() {
		PriceBelowPredicate predicate = new PriceBelowPredicate();
		try {
			predicate.evaluate(null, 0);
			Assert.fail("Should have generated an exception");
		} catch (IllegalArgumentException expected) {

		}
	}

	@Test
	public void testItemWithoutPricesShouldBeFalse() {
		PriceBelowPredicate predicate = new PriceBelowPredicate();
		boolean result = predicate.evaluate(new Item(), 0);
		assertFalse(result);
	}
}

package edu.rosehulman.salenotifier.tests;

import static org.junit.Assert.*;
import junit.framework.TestCase;

import org.junit.Test;

import edu.rosehulman.salenotifier.models.Item;
import edu.rosehulman.salenotifier.models.ItemPrice;
import edu.rosehulman.salenotifier.notifications.PriceAbovePredicate;

public class PriceAbovePredicateTests extends TestCase {

	@Test
	public void testNullItemThrowsException() {
		PriceAbovePredicate predicate = new PriceAbovePredicate();
		try {
			predicate.evaluate(null, 0);
			fail("Should have received IllegalArgumentException");
		} catch (IllegalArgumentException expected) {

		}
	}

	@Test
	public void testItemWithNoPrices() {
		PriceAbovePredicate predicate = new PriceAbovePredicate();
		Item test = new Item();
		boolean result = predicate.evaluate(test, 0);
		assertFalse(result);
	}

	@Test
	public void testItemWithSinglePriceAbove() {
		PriceAbovePredicate predicate = new PriceAbovePredicate();
		Item test = new Item();
		test.addPrice(createPrice(20.5, 99));
		boolean result = predicate.evaluate(test, 20);
		assertTrue(result);
	}

	private ItemPrice createPrice(double price, long sellerId) {
		ItemPrice result = new ItemPrice();
		result.setPrice(price);
		result.setSellerId(sellerId);
		return result;
	}
}

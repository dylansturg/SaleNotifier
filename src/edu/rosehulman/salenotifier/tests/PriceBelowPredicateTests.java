package edu.rosehulman.salenotifier.tests;

import org.junit.Test;

import edu.rosehulman.salenotifier.models.Item;
import edu.rosehulman.salenotifier.models.ItemPrice;
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

	@Test
	public void testItemWithPriceEqual() {
		PriceBelowPredicate predicate = new PriceBelowPredicate();
		Item test = new Item();
		test.addPrice(new ItemPrice(75.0, null));

		boolean result = predicate.evaluate(test, 75);
		assertFalse(result);
	}

	@Test
	public void testItemWithPriceBelow() {
		PriceBelowPredicate predicate = new PriceBelowPredicate();
		Item test = new Item();
		test.addPrice(createPrice(45.0, 9));

		boolean result = predicate.evaluate(test, 46);
		assertTrue(result);
	}

	@Test
	public void testItemWithTwoPricesAbove() {
		PriceBelowPredicate predicate = new PriceBelowPredicate();
		Item test = new Item();
		test.addPrice(createPrice(45.0, 4));
		test.addPrice(createPrice(75.0, 99));

		boolean result = predicate.evaluate(test, 40);
		assertFalse(result);
	}

	@Test
	public void testItemWithManyPricesAbove() {
		PriceBelowPredicate predicate = new PriceBelowPredicate();
		Item test = new Item();

		double threshold = 10;
		for (int i = 0; i < 30; i++) {
			ItemPrice testPrice = new ItemPrice(i + 1, threshold + i * 3, null);
			testPrice.setSellerId(i + 1);
			test.addPrice(testPrice);
		}

		boolean result = predicate.evaluate(test, threshold);
		assertFalse(result);
	}

	@Test
	public void testItemWithManyPricesOneBelow() {
		PriceBelowPredicate predicate = new PriceBelowPredicate();
		Item test = new Item();

		double threshold = 10;
		for (int i = 0; i < 30; i++) {
			ItemPrice testPrice = new ItemPrice(threshold + i * 3, null);
			testPrice.setSellerId(i);
			test.addPrice(testPrice);
		}

		test.addPrice(createPrice(3, 404));

		boolean result = predicate.evaluate(test, threshold);
		assertTrue(result);
	}

	private ItemPrice createPrice(double price, long sellerId) {
		ItemPrice result = new ItemPrice();
		result.setPrice(price);
		result.setSellerId(sellerId);
		return result;
	}
}

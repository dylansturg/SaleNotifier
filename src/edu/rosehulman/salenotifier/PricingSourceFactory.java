package edu.rosehulman.salenotifier;

import java.util.ArrayList;
import java.util.List;

import edu.rosehulman.salenotifier.ItemSearchTask.IPartialSearchResultsCallback;
import edu.rosehulman.salenotifier.amazon.AmazonPricingSource;
import edu.rosehulman.salenotifier.db.Enumerable;
import edu.rosehulman.salenotifier.db.Enumerable.IPredicate;
import edu.rosehulman.salenotifier.db.SaleNotifierSQLHelper;
import edu.rosehulman.salenotifier.ebay.EbayPricingSource;
import edu.rosehulman.salenotifier.settings.Setting;
import edu.rosehulman.salenotifier.settings.SettingsManager;

public class PricingSourceFactory {
	public static final String[] AVAILABLE_PRICE_SOURCES = { "AMAZON", "EBAY",
			"SEMANTICS3" };

	public PricingSourceFactory() {
	}

	public static List<IPricingSource> getValidPriceSources() {
		if (!SaleNotifierSQLHelper.isInit()) {
			throw new IllegalStateException(
					"Failed to initialize the SQL data source before attemping to get IPricinSources");
		}

		Enumerable<Setting<?>> appSettings = SettingsManager.getManager()
				.getAppSettings();

		@SuppressWarnings({ "unchecked", "rawtypes" })
		Enumerable<Setting<Boolean>> datasourceSettings = (Enumerable) appSettings
				.where(new IPredicate<Setting<?>>() {
					@Override
					public boolean match(Setting<?> element) {
						return element.getName().contains(
								Setting.DATA_SOURCE_PREFIX);
					}
				});

		List<IPricingSource> sources = new ArrayList<IPricingSource>();
		for (final String source : AVAILABLE_PRICE_SOURCES) {
			Setting<Boolean> sourceSetting = datasourceSettings
					.firstOrDefault(new IPredicate<Setting<Boolean>>() {
						@Override
						public boolean match(Setting<Boolean> element) {
							return element.getName().equalsIgnoreCase(
									String.format(
											Setting.DATA_SOURCE_NAME_FORMAT,
											source));
						}
					});

			if (sourceSetting != null && !sourceSetting.getValue()) {
				continue;
			}
			IPricingSource priceSource = resolveSource(source);
			if (priceSource != null) {
				sources.add(priceSource);
			}
		}

		return sources;
	}

	private static IPricingSource resolveSource(String name) {
		if (name.equalsIgnoreCase("amazon")) {
			return new AmazonPricingSource();
		} else if (name.equalsIgnoreCase("semantics3")) {
			return new Semantics3PriceSource();
		} else if (name.equalsIgnoreCase("ebay")) {
			return new EbayPricingSource();
		}

		return null;
	}
}

package edu.rosehulman.salenotifier.amazon;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.FormattedHeader;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import edu.rosehulman.salenotifier.TrackedItemsActivity;

import android.util.Log;
import android.util.Xml;

public class AmazonResponse {

	private List<AmazonItem> results;

	public AmazonResponse(String xmlResponse, String operation)
			throws XmlPullParserException, IOException {
		AmazonResultsParser parser = new AmazonResultsParser(operation);
		results = parser.parse(xmlResponse);
	}

	public List<AmazonItem> getParsedResults() {
		return results;
	}

	class AmazonResultsParser {
		private final String NS = null;

		private String operation;

		public AmazonResultsParser(String op) {
			operation = op;
		}

		public List<AmazonItem> parse(String xml)
				throws XmlPullParserException, IOException {
			XmlPullParser parser = Xml.newPullParser();
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			parser.setInput(new StringReader(xml));
			parser.nextTag();
			return readOperationResult(parser);
		}

		private List<AmazonItem> readOperationResult(XmlPullParser parser)
				throws XmlPullParserException, IOException {
			List<AmazonItem> results = new ArrayList<AmazonItem>();

			parser.require(XmlPullParser.START_TAG, NS, operation + "Response");
			while (parser.next() != XmlPullParser.END_TAG) {
				if (parser.getEventType() != XmlPullParser.START_TAG) {
					continue;
				}
				String name = parser.getName();
				// Starts by looking for the entry tag
				if (name.equals("Items")) {
					results.addAll(readItems(parser));
				} else {
					skip(parser);
				}
			}

			return results;
		}

		private void skip(XmlPullParser parser) throws XmlPullParserException,
				IOException {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				throw new IllegalStateException();
			}
			int depth = 1;
			while (depth != 0) {
				switch (parser.next()) {
				case XmlPullParser.END_TAG:
					String name = parser.getName();
					depth--;
					break;
				case XmlPullParser.START_TAG:
					String name2 = parser.getName();
					depth++;
					break;
				}
			}
		}

		private List<AmazonItem> readItems(XmlPullParser parser)
				throws XmlPullParserException, IOException {
			List<AmazonItem> results = new ArrayList<AmazonItem>();

			parser.require(XmlPullParser.START_TAG, NS, "Items");
			while (parser.next() != XmlPullParser.END_TAG) {
				if (parser.getEventType() != XmlPullParser.START_TAG) {
					continue;
				}
				String name = parser.getName();
				// Starts by looking for the entry tag
				if (name.equals("Item")) {
					AmazonItem item = readItem(parser);
					if (item != null) {
						results.add(item);
					}
				} else {
					skip(parser);
				}
			}
			parser.require(XmlPullParser.END_TAG, NS, "Items");
			return results;
		}

		private AmazonItem readItem(XmlPullParser parser)
				throws XmlPullParserException, IOException {

			AmazonItem item = new AmazonItem();

			while (parser.next() != XmlPullParser.END_TAG) {
				if (parser.getEventType() != XmlPullParser.START_TAG) {
					continue;
				}
				String name = parser.getName();

				if (name.equals("ASIN")) {
					item.ASIN = readText(parser, "ASIN");
				} else if (name.equals("DetailPageURL")) {
					item.DetailsUrl = readText(parser, "DetailPageURL");
				} else if (name.equals("SmallImage")) {
					item.ImageUrls.put("Small",
							readImageUrl(parser, "SmallImage"));
				} else if (name.equals("MediumImage")) {
					item.ImageUrls.put("Medium",
							readImageUrl(parser, "MediumImage"));
				} else if (name.equals("LargeImage")) {
					item.ImageUrls.put("Large",
							readImageUrl(parser, "LargeImage"));
				} else if (name.equals("ItemAttributes")) {
					readItemAttributes(parser, item);
				} else if (name.equals("OfferSummary")) {
					double price = readOfferSummary(parser);
					if (price > 0) {
						if (item.price > price || item.price <= 0) {
							item.price = price;
						}
					}
				} else {
					skip(parser);
				}
			}

			if (item.UPCs.size() == 0 && item.EANs.size() == 0) {
				return null; // useless item
			}

			return item;
		}

		private void readItemAttributes(XmlPullParser parser, AmazonItem item)
				throws XmlPullParserException, IOException {
			parser.require(XmlPullParser.START_TAG, NS, "ItemAttributes");
			while (parser.next() != XmlPullParser.END_TAG) {
				if (parser.getEventType() != XmlPullParser.START_TAG) {
					continue;
				}
				String name = parser.getName();
				// Starts by looking for the entry tag
				if (name.equals("ListPrice")) {
					double price = readPrice(parser, "ListPrice");
					if (price > 0) {
						if (item.price > price || item.price <= 0) {
							item.price = price;
						}
					}
				} else if (name.equals("EANList")) {
					item.EANs.addAll(readProductCodeList(parser, "EANList"));
				} else if (name.equals("EAN")) {
					item.EANs.add(readText(parser, "EAN"));
				} else if (name.equals("Title")) {
					item.title = readText(parser, "Title");
				} else if (name.equals("UPCList")) {
					item.EANs.addAll(readProductCodeList(parser, "UPCList"));
				} else if (name.equals("UPC")) {
					item.EANs.add(readText(parser, "UPC"));
				} else {
					skip(parser);
				}
			}
			parser.require(XmlPullParser.END_TAG, NS, "ItemAttributes");
		}

		private List<String> readProductCodeList(XmlPullParser parser,
				String type) throws XmlPullParserException, IOException {

			parser.require(XmlPullParser.START_TAG, NS, type);
			List<String> productCodes = new ArrayList<String>();
			while (parser.next() != XmlPullParser.END_TAG) {
				if (parser.getEventType() != XmlPullParser.START_TAG) {
					continue;
				}
				String name = parser.getName();
				// Starts by looking for the entry tag
				if (name.equals(type + "Element")) {
					productCodes.add(readText(parser, type + "Element"));
				} else {
					skip(parser);
				}
			}
			parser.require(XmlPullParser.END_TAG, NS, type);

			return productCodes;
		}

		private double readPrice(XmlPullParser parser, String container)
				throws XmlPullParserException, IOException {
			parser.require(XmlPullParser.START_TAG, NS, container);

			double price = -1;
			String currency = null;
			while (parser.next() != XmlPullParser.END_TAG) {
				if (parser.getEventType() != XmlPullParser.START_TAG) {
					continue;
				}
				String name = parser.getName();
				// Starts by looking for the entry tag
				if (name.equals("CurrencyCode")) {
					currency = readText(parser, "CurrencyCode");
				} else if (name.equals("Amount")) {
					String unformattedPrice = readText(parser, "Amount");
					if (unformattedPrice.length() > 2) {
						try {
							Double decimal = Double
									.parseDouble(unformattedPrice
											.substring(unformattedPrice
													.length() - 2));
							Double digits = Double
									.parseDouble(unformattedPrice.substring(0,
											unformattedPrice.length() - 2));

							price = digits + (decimal / 100);
						} catch (Exception e) {
							Log.d(TrackedItemsActivity.LOG_TAG,
									"AmazonResponse: Failed to parse Amount");
						}

					}

				} else {
					skip(parser);
				}
			}
			parser.require(XmlPullParser.END_TAG, NS, container);

			if (currency.equalsIgnoreCase("USD")) {
				return price;
			}
			return -1;
		}

		private String readImageUrl(XmlPullParser parser, String imageSize)
				throws XmlPullParserException, IOException {
			parser.require(XmlPullParser.START_TAG, NS, imageSize);
			String result = "";
			while (parser.next() != XmlPullParser.END_TAG) {
				if (parser.getEventType() != XmlPullParser.START_TAG) {
					continue;
				}
				String name = parser.getName();
				// Starts by looking for the entry tag
				if (name.equals("URL")) {
					result = readText(parser, "URL");
				} else {
					skip(parser);
				}
			}
			parser.require(XmlPullParser.END_TAG, NS, imageSize);
			return result;
		}

		private double readOfferSummary(XmlPullParser parser)
				throws XmlPullParserException, IOException {
			parser.require(XmlPullParser.START_TAG, NS, "OfferSummary");
			double result = -1;
			while (parser.next() != XmlPullParser.END_TAG) {
				if (parser.getEventType() != XmlPullParser.START_TAG) {
					continue;
				}
				String name = parser.getName();
				// Starts by looking for the entry tag
				if (name.equals("LowestNewPrice")) {
					result = readPrice(parser, "LowestNewPrice");
				} else {
					skip(parser);
				}
			}
			parser.require(XmlPullParser.END_TAG, NS, "OfferSummary");
			return result;
		}

		private String readText(XmlPullParser parser, String name)
				throws IOException, XmlPullParserException {
			parser.require(XmlPullParser.START_TAG, NS, name);
			String result = "";
			if (parser.next() == XmlPullParser.TEXT) {
				result = parser.getText();
				parser.nextTag();
			}
			parser.require(XmlPullParser.END_TAG, NS, name);
			return result;
		}
	}
}

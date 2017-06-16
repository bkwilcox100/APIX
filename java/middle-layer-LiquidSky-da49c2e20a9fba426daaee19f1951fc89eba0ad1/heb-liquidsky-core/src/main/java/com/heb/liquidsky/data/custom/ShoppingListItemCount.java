package com.heb.liquidsky.data.custom;

import java.util.List;
import java.util.Map;

import com.heb.liquidsky.endpoints.ShoppingListConstants;
import com.heb.liquidsky.data.DataItem;
import com.heb.liquidsky.data.DataStoreException;
import com.heb.liquidsky.trace.HebFlexTracer;
import com.heb.liquidsky.trace.HebTraceContext;

/**
 * This custom property gets the count of the products and freeform items for the shopping list
 * @author Scott McArthur
 */
public class ShoppingListItemCount extends AbstractCustomPropertyDescriptor {

	private static final HebFlexTracer TRACER = HebFlexTracer.getTracer(ShoppingListItemCount.class);
	private static final String ATTR_DEFAULT_VALUE = "defaultValue";

	public ShoppingListItemCount(Map<String, String> attributes) throws InstantiationException {
		super(attributes);
		if (attributes == null || !attributes.containsKey(ATTR_DEFAULT_VALUE)) {
			throw new InstantiationException("ShoppingListItemCount: must specify a " + ATTR_DEFAULT_VALUE + " attribute");
		}
	}

	@Override
	public String getPropertyValue(DataItem dataItem) throws DataStoreException {
		HebTraceContext context = TRACER.startSpan("getPropertyValue");
		try {
			int itemCount = 0;
			if (dataItem != null) {
				List<DataItem> productItems = dataItem.getList(ShoppingListConstants.PROPERTY_NAME_SHOPPING_LIST_PRODUCT_ITEMS);
				List<DataItem> freeformItems = dataItem.getList(ShoppingListConstants.PROPERTY_NAME_SHOPPING_LIST_FREEFORM_ITEMS);
				itemCount = (productItems != null ? productItems.size() : 0) + (freeformItems != null ? freeformItems.size() : 0);
			}
			return Integer.toString(itemCount);
		} finally {
			TRACER.endSpan(context);
		}
	}
}

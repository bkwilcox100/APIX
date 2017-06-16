package com.heb.liquidsky.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.heb.liquidsky.cache.HebMemoryStoreCache;
import com.heb.liquidsky.common.ValueContainer;
import com.heb.liquidsky.trace.Label;
import com.heb.liquidsky.trace.HebFlexTracer;
import com.heb.liquidsky.trace.HebTraceContext;

/**
 * Utility class for handling cache for DataStore items.
 */
public final class DataStoreCache {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(DataStoreCache.class.getName());
	private static final HebFlexTracer TRACER = HebFlexTracer.getTracer(DataStoreCache.class);
	private static final DataStoreCache INSTANCE = new DataStoreCache();

	private DataStoreCache() {
		// only allow access to this class via the singleton instance
	}

	private String cacheKey(String id, String dataTypeName) {
		return DataStore.class.getName() + "__" + dataTypeName.toLowerCase() + "__" + id.toLowerCase();
	}

	/**
	 * For properties linked to other data items, determine what items
	 * in the cache are dependencies to ensure that reverse lookups are
	 * kept in sync.
	 */
	protected List<String> calculateDependencies(DataItem dataItem) throws DataStoreException {
		List<String> cacheKeys = new ArrayList<>();
		if (dataItem == null) {
			return cacheKeys;
		}
		HebTraceContext context = TRACER.startSpan("calculateDependencies");
		try {
			for (DataItemPropertyDescriptor descriptor : dataItem.dataItemDescriptor().getPropertyDescriptors()) {
				if (descriptor.getItemType() != null) {
					// calling getItem() would perform unnecessary item initialization
					// and place the child item into the cache, so use a tricky
					// workaround to just get the id.
					String itemId = dataItem.getString(descriptor.getPropertyName());
					if (itemId == null) {
						continue;
					}
					String key = this.cacheKey(itemId, descriptor.getItemType());
					if (!cacheKeys.contains(key)) { 
						cacheKeys.add(key);
					}
				} else if (descriptor.getListItemType() != null) {
					// calling getList() would perform unnecessary item initialization
					// and place items into the cache, so use a tricky workaround to
					// just get the list of item ids.
					String[] itemIds = (String[]) dataItem.getObject(descriptor.getPropertyName());
					if (itemIds == null) {
						continue;
					}
					for (String itemId : itemIds) {
						String key = this.cacheKey(itemId, descriptor.getListItemType());
						if (!cacheKeys.contains(key)) { 
							cacheKeys.add(key);
						}
					}
				}
			}
		} finally {
			TRACER.annotateSpan(context, "item", dataItem.toString());
			TRACER.endSpan(context);
		}
		return cacheKeys;
	}

	protected void deleteAll(List<String> cacheKeys) {
		HebTraceContext context = TRACER.startSpan("delete");
		try {
			HebMemoryStoreCache.getInstance().deleteAll(cacheKeys);
		} finally {
			TRACER.annotateSpan(context, "# of items", Integer.toString(cacheKeys.size()));
			TRACER.endSpan(context);
		}
	}

	public static DataStoreCache getInstance() {
		return INSTANCE;
	}

	public ValueContainer<Serializable> read(String id, String dataTypeName) {
		HebTraceContext context = TRACER.startSpan("read");
		try {
			String cacheKey = this.cacheKey(id, dataTypeName);
			return HebMemoryStoreCache.getInstance().get(cacheKey);
		} finally {
			TRACER.annotateSpan(context, new Label("id", id), new Label("data-type", dataTypeName));
			TRACER.endSpan(context);
		}
	}

	public Map<String, Object> readAll(String[] ids, String dataTypeName) {
		Map<String, Object> result = null;
		if (ids != null && ids.length > 0) {
			HebTraceContext context = TRACER.startSpan("readAll");
			try {
				List<String> cacheKeys = new ArrayList<>();
				for (String id : ids) {
					cacheKeys.add(this.cacheKey(id, dataTypeName));
				}
				Map<String, Object> cachedResult = HebMemoryStoreCache.getInstance().getAll(cacheKeys);
				if (cachedResult != null && !cachedResult.isEmpty()) {
					for (String id : ids) {
						String cacheKey = this.cacheKey(id, dataTypeName);
						if (!cachedResult.containsKey(cacheKey)) {
							continue;
						}
						if (result == null) {
							result = new HashMap<>(ids.length);
						}
						result.put(id, cachedResult.get(cacheKey));
					}
				}
			} finally {
				TRACER.annotateSpan(context, new Label("# of items", Integer.toString(ids.length)), new Label("data-type", dataTypeName));
				TRACER.endSpan(context);
			}
		}
		return (result == null) ? Collections.<String, Object> emptyMap() : result;
	}

	public void update(String id, String dataType, DataItem dataItem) {
		HebTraceContext context = TRACER.startSpan("update");
		try {
			this.updateInternal(id, dataType, dataItem);
		} finally {
			TRACER.annotateSpan(context, new Label("id", id), new Label("data-type", dataType));
			TRACER.endSpan(context);
		}
	}

	private void updateInternal(String id, String dataType, DataItem cacheValue) {
		if (cacheValue instanceof MutableDataItem) {
			throw new IllegalArgumentException("Cannot cache a MutableDataItem");
		}
		String cacheKey = this.cacheKey(id, dataType);
		ValueContainer<Serializable> dataItemContainer = (cacheValue == null) ? ValueContainer.<Serializable> getEmptyValueContainer() : new ValueContainer<>((Serializable) cacheValue);
		HebMemoryStoreCache.getInstance().put(cacheKey, dataItemContainer);
	}
}

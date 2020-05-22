package com.nabiki.think.webgui;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.nabiki.think.crawler.yumi.data.QueryResult;
import com.nabiki.think.webgui.utils.Utils;
import com.nabiki.think.webgui.utils.YumiCatalog;
import com.nabiki.think.webgui.utils.YumiCategory;

public class DataAccess {
	private final ConcurrentHashMap<Integer, QueryResult> yumiRes = new ConcurrentHashMap<>();
	private YumiCategory category;
	private YumiCatalog catalog;
	
	public DataAccess() {
	}
	
	public ConcurrentHashMap<Integer, QueryResult> yumi() {
		synchronized(this) {
			return this.yumiRes;
		}
	}
	
	public void yumi(Map<Integer, QueryResult> m) {
		synchronized(this) {
			this.yumiRes.clear();
			this.yumiRes.putAll(m);
			// Set category.
			this.catalog = Utils.extractCatalog(m);
			this.category = new YumiCategory(this.catalog);
		}
	}
	
	public YumiCatalog catalog() {
		return this.catalog;
	}
	
	public YumiCategory category() {
		return this.category;
	}
}

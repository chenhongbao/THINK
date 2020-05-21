package com.nabiki.think.webgui;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.nabiki.think.crawler.yumi.data.QueryResult;

public class DataAccess {
	private final ConcurrentHashMap<Integer, QueryResult> yumiRes = new ConcurrentHashMap<>();
	
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
		}
	}
}

package com.nabiki.think.webgui;

import java.util.concurrent.ConcurrentHashMap;

import com.nabiki.think.crawler.yumi.data.QueryResult;

public class DataAccess {
	private final ConcurrentHashMap<Integer, QueryResult> yumiRes = new ConcurrentHashMap<>();
	
	public DataAccess() {
	}
	
	public ConcurrentHashMap<Integer, QueryResult> yumi() {
		return this.yumiRes;
	}
	
	public void yumi(ConcurrentHashMap<Integer, QueryResult> m) {
		this.yumiRes.clear();
		this.yumiRes.putAll(m);
	}
}

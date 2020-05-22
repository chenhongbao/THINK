package com.nabiki.think.webgui.utils;

import java.util.Map;

import com.nabiki.think.crawler.yumi.data.QueryResult;

public class Utils {
	public static YumiCatalog extractCatalog(Map<Integer, QueryResult> m) {
		var c = new YumiCatalog();
		for (var key : m.keySet())
			c.list.add(new ValuePair(key, m.get(key).name));
		
		return c;
	}
}

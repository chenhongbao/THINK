package com.nabiki.think.crawler.yumi.api;

import java.util.List;

public interface QueryResult {
	String unit();
	
	String name();
	
	String type();
	
	List<ValuePair> list();
}

package com.nabiki.think.crawler.yumi.data;

import java.util.LinkedList;
import java.util.List;

public class QueryResult {
	public String unit;
	public String name;
	public String type;
	public List<ValuePair> list = new LinkedList<>();
	
	public QueryResult() {}
}

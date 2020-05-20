package com.nabiki.think.crawler.yumi.impl;

import java.util.LinkedList;
import java.util.List;

import com.nabiki.think.crawler.yumi.api.QueryResult;
import com.nabiki.think.crawler.yumi.api.ValuePair;

public class QueryResultImpl implements QueryResult {
	public String unit;
	public String name;
	public String type;
	public List<ValuePairImpl> list = new LinkedList<>();
	
	public QueryResultImpl() {}

	@Override
	public String unit() {
		return this.unit;
	}

	@Override
	public String name() {
		return this.name;
	}

	@Override
	public String type() {
		return this.type;
	}

	@Override
	public List<ValuePair> list() {
		var ret = new LinkedList<ValuePair>();
		for (var r : this.list)
			ret.add(r);
		
		return ret;
	}

}

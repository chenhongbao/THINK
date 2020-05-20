package com.nabiki.think.crawler.yumi.impl;

import java.time.LocalDate;

import javax.json.bind.annotation.JsonbDateFormat;

import com.nabiki.think.crawler.yumi.api.ValuePair;

public class ValuePairImpl implements ValuePair {
	public String data;
	@JsonbDateFormat("yyyy-MM-dd")
	public LocalDate date;
	
	public ValuePairImpl() {}

	@Override
	public Double data() {
		return Double.valueOf(this.data);
	}

	@Override
	public LocalDate date() {
		return this.date;
	}

}

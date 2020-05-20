package com.nabiki.think.crawler.yumi.data;

import java.time.LocalDate;

import javax.json.bind.annotation.JsonbDateFormat;

public class ValuePair {
	public String data;
	@JsonbDateFormat("yyyy-MM-dd")
	public LocalDate date;
	
	public ValuePair() {}
}

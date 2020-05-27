package com.nabiki.think.webgui;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.nabiki.think.crawler.yumi.data.QueryResult;
import com.nabiki.think.webgui.utils.TodayNotice;
import com.nabiki.think.webgui.utils.Utils;
import com.nabiki.think.webgui.utils.YumiCatalog;
import com.nabiki.think.webgui.utils.YumiCategory;

public class DataAccess {
	private final ConcurrentHashMap<Integer, QueryResult> yumiRes = new ConcurrentHashMap<>();
	private YumiCategory category;
	private YumiCatalog catalog;
	private TodayNotice notice;
	private String updateTime;
	
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
		
		// Set category.
		this.catalog = Utils.extractCatalog(m);
		this.category = new YumiCategory(this.catalog);
		// Set notice.
		this.notice = new TodayNotice(this.yumiRes);
		// Set time stamp.
		this.updateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("更新于yyyy年M月d日H时m分s秒"));
	}
	
	public YumiCatalog catalog() {
		return this.catalog;
	}
	
	public YumiCategory category() {
		return this.category;
	}
	
	public TodayNotice notice() {
		return this.notice;
	}
	
	public String updateTime() {
		return this.updateTime;
	}
}

package com.nabiki.think.webgui;

import java.io.IOException;
import java.time.LocalTime;

import com.nabiki.think.crawler.yumi.Yumi;

public class CrawlerTask implements Runnable {
	private final Yumi yumi;
	private final DataAccess da;
	
	CrawlerTask(Yumi yumi, DataAccess da) {
		this.yumi = yumi;
		this.da = da;
	}

	@Override
	public void run() {
		// Don't fetch data at night.
		var now = LocalTime.now();
		if (23 <= now.getHour() || now.getHour() < 6)
			return;
		
		// Load old data for the first run.
		try {
			this.da.yumi(this.yumi.read());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// Log activity.
		System.out.println("Start fetching data from yumi.com.cn.");
		
		try {
			yumi.run();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// Log activity.
		System.out.println("End fetching data.");
		// Set new data into data access.
		this.da.yumi(this.yumi.lastQuery());
	}
}
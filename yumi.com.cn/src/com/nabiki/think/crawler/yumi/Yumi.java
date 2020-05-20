package com.nabiki.think.crawler.yumi;

import java.io.IOException;

public class Yumi {
	private final YumiClient client;
	private final QueryResultIO io;
	
	public Yumi() throws IOException {
		this.io = new QueryResultIO();
		this.client = new YumiClient(this.io);
	}
	
	public void run() throws IOException {
		for (var id : Utils.ids) {
			this.io.write(Utils.merge(client.query(id).result, this.io.read(id)));
		}
	}
}

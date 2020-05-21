package com.nabiki.think.crawler.yumi;

import java.io.IOException;
import java.nio.file.Path;

public class Yumi {
	private final YumiClient client;
	private final QueryResultIO io;
	
	public Yumi(Path dataRoot) throws IOException {
		this.io = new QueryResultIO(dataRoot);
		this.client = new YumiClient(this.io);
	}
	
	public void run() throws IOException {
		for (var id : Utils.ids) {
			this.io.write(Utils.merge(client.query(id).result, this.io.read(id)));
			
			// Wait some time so the server won't be busy.
			try {
				Thread.sleep((long)(Math.random() * 10000));
			} catch (InterruptedException e) {
			}
		}
	}
	
	public QueryResultIO io() {
		return this.io;
	}
}

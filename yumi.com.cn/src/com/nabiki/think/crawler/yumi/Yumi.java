package com.nabiki.think.crawler.yumi;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.nabiki.think.crawler.yumi.data.QueryResult;

public class Yumi {
	private final YumiClient client;
	private final QueryResultIO io;
	private final Path confPath;
	private final YumiErrorListener listener;
	private final Map<Integer, QueryResult> cacheResult = new ConcurrentHashMap<>();
	
	public Yumi(Path root, YumiErrorListener listener) throws IOException {
		this.listener = listener;
		this.confPath = Path.of(root.toAbsolutePath().toString(), "nabiki_config");
		this.io = new QueryResultIO(Path.of(root.toAbsolutePath().toString(), "nabiki_data"));
		this.client = new YumiClient(this.io);
	}
	
	public void run() throws IOException {
		var ids = Utils.queryYumiIds(config());
		for (var id : ids) {
			// Fetch new data.
			var rsp = client.query(id);
			if (rsp == null) {
				whenError(new Exception("Fail fetching data for query ID: " + id));
				continue;
			}
			
			// Merge two piece of data.
			var newRes = Utils.merge(rsp.result, this.io.read(id));
			if (newRes == null) {
				whenError(new Exception("Fail merging results for query ID: " + id));
				continue;
			}
			
			this.io.write(newRes);
			// Save result in cache.
			this.cacheResult.put(id, newRes);
			// Wait some time so the server won't be busy.
			try {
				Thread.sleep((long)(Math.random() * 10000));
			} catch (InterruptedException e) {
				whenError(e);
			}
		}
	}
	
	private void whenError(Exception e) {
		try {
			if (this.listener != null)
				this.listener.error(e);
		} catch (Exception ex) {
		}
	}
	
	public Map<Integer, QueryResult> read() throws IOException {
		var m = new HashMap<Integer, QueryResult>();
		for (var id : Utils.queryYumiIds(config())) {
			var r = this.io.read(id);
			m.put(id,  r);
		}
		return m;
	}
	
	public Map<Integer, QueryResult> lastQuery() {
		return this.cacheResult;
	}
	
	public QueryResultIO io() {
		return this.io;
	}
	
	private Path config() throws IOException {
		var path = Path.of(this.confPath.toAbsolutePath().toString(), "yumi.com.cn");
		// Create directories if not exist.
		if (!path.toFile().isDirectory())
			Files.createDirectories(path);
		// Create file if not exists.
		path = Path.of(path.toAbsolutePath().toString(), "query_id.txt");
		if (!path.toFile().exists() || !path.toFile().canRead()) {
			path.toFile().createNewFile();
			path.toFile().setReadable(true);
		}
			
		return path;
	}
}

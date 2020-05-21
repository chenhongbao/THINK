package com.nabiki.think.crawler.yumi;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import javax.json.bind.config.PropertyNamingStrategy;

import com.nabiki.think.crawler.yumi.data.QueryResult;

public class QueryResultIO {
	private final Path root;
	private final Jsonb json;

	public QueryResultIO(Path dataRoot) throws IOException {
		this.root = Path.of(dataRoot.toAbsolutePath().toString(), "yumi.com.cn");
		
		if (!this.root.toFile().isDirectory())
			Files.createDirectories(this.root);
		// Create jsonb.
		JsonbConfig config = new JsonbConfig().withPropertyNamingStrategy(PropertyNamingStrategy.IDENTITY)
				.withFormatting(true).withNullValues(true);
		this.json = JsonbBuilder.create(config);
		
	}

	public void write(QueryResult rs) throws IOException {
		try (FileOutputStream fos = new FileOutputStream(file(Integer.valueOf(rs.type)))) {
			var str = jsonb().toJson(rs);	
			fos.write(str.getBytes(Charset.forName("UTF-8")));
		} catch (NumberFormatException e) {
			throw new IOException("Wrong query ID format, expected integer but found " + rs.type);
		}
	}
	
	public Jsonb jsonb() {
		return this.json;
	}
	
	public String raw(int queryId) throws IOException {
		var fi = new FileInputStream(file(queryId));
		return new String(fi.readAllBytes(), Charset.forName("UTF-8"));
	}

	public QueryResult read(int queryId) throws IOException {
		var str = raw(queryId);
		if (str.length() == 0) {
			var r =  new QueryResult();
			r.type = Integer.toString(queryId);
			return r;
		}
		else
			return jsonb().fromJson(str, QueryResult.class);
	}

	private File file(int queryId) throws IOException {
		var path = Path.of(this.root.toAbsolutePath().toString(), Integer.toString(queryId));
		var r = path.toFile();

		// Existent.
		if (!r.exists())
			r.createNewFile();
		// Readable.
		if (!r.canRead())
			r.setReadable(true);
		// Writable.
		if (!r.canWrite())
			r.setWritable(true);

		return r;
	}
}

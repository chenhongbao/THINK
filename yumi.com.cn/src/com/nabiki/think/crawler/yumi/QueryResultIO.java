package com.nabiki.think.crawler.yumi;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
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
	private final static Path root = Path.of(".", "data", "yumi.com.cn");
	private final Jsonb json;

	public QueryResultIO() throws IOException {
		if (!QueryResultIO.root.toFile().isDirectory())
			Files.createDirectories(QueryResultIO.root);
		// Create jsonb.
		JsonbConfig config = new JsonbConfig().withPropertyNamingStrategy(PropertyNamingStrategy.IDENTITY)
				.withFormatting(true).withNullValues(true);
		this.json = JsonbBuilder.create(config);
		
	}

	public void write(QueryResult rs) throws IOException {
		try (FileWriter fw = new FileWriter(file(Integer.valueOf(rs.type)))) {
			fw.write(jsonb().toJson(rs));
		} catch (NumberFormatException e) {
			throw new IOException("Wrong query ID format, expected integer but found " + rs.type);
		}
	}
	
	public Jsonb jsonb() {
		return this.json;
	}

	public QueryResult read(int queryId) throws IOException {
		var fi = new FileInputStream(file(queryId));
		var bytes = fi.readAllBytes();
		if (bytes.length == 0) {
			var r =  new QueryResult();
			r.type = Integer.toString(queryId);
			return r;
		}
		else
			return jsonb().fromJson(new String(bytes, Charset.forName("UTF-8")), QueryResult.class);
	}

	private File file(int queryId) throws IOException {
		var path = Path.of(QueryResultIO.root.toAbsolutePath().toString(), Integer.toString(queryId));
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

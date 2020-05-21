package com.nabiki.think.webgui;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import javax.json.bind.config.PropertyNamingStrategy;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import com.nabiki.think.crawler.yumi.Yumi;
import com.nabiki.think.crawler.yumi.data.QueryResult;

@WebListener
public class CrawlerInitializer implements ServletContextListener {
	// Get file with given name.
	// Create if file doesn't exist.
	private File ensureFile(String name) throws IOException {
		var file = Path.of(".", name).toFile();
		if (!file.exists())
			file.createNewFile();
		if (!file.canWrite())
			file.setWritable(true);
		
		return file;
	}
	
	private void setConsole() {	
		try {
			System.setOut(new TimePrintStream(new FileOutputStream(ensureFile("out.txt"), true)));
			System.setErr(new TimePrintStream(new FileOutputStream(ensureFile("err.txt"), true)));
		} catch (IOException e) {
		}
	}
	
	class CrawlerTask implements Runnable {
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
			setDa();
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
			setDa();
		}
		
		private void setDa() {
			var m = new ConcurrentHashMap<Integer, QueryResult>();
			try {
				var results = this.yumi.io().read();
				for (var key : results.keySet())
					m.put(key, results.get(key));
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			this.da.yumi(m);
		}
	}
	
	private Yumi yumi;
	private final DataAccess da;
	private final CrawlerTask task;
	private final ScheduledThreadPoolExecutor executor;
	
	public CrawlerInitializer() {
		try {
			if (this.yumi == null)
				this.yumi = new Yumi(Path.of(".", "data"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		this.da = new DataAccess();
		this.task = new CrawlerTask(this.yumi, this.da);
		this.executor = new ScheduledThreadPoolExecutor(20);
		
		// Set console file.
		setConsole();
	}

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		this.executor.scheduleAtFixedRate(this.task, 0, 1, TimeUnit.HOURS);
		
		// Register data object.
		var ctx = sce.getServletContext();
		ctx.setAttribute("DataAccess", this.da);
		
		// Common json builder.
		JsonbConfig config = new JsonbConfig()
				.withPropertyNamingStrategy(PropertyNamingStrategy.IDENTITY)
				.withNullValues(true);
		ctx.setAttribute("Jsonb", JsonbBuilder.create(config));
		
		System.out.println("Crawler task is scheduled.");
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		this.executor.remove(this.task);
		this.executor.shutdownNow();
		
		System.out.println("Crawler task is removed.");
	}

}

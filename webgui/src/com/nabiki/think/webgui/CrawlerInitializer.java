package com.nabiki.think.webgui;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import javax.json.bind.config.PropertyNamingStrategy;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import com.nabiki.think.crawler.yumi.*;
import com.nabiki.think.webgui.utils.TodayNotice;

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
			try {
				this.da.yumi(this.yumi.read());
				setAttributes();
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
			// Set servlet context.
			setAttributes();
		}
		
		private void setAttributes() {
			// Set last update time.
			var timeStamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("更新于yyyy年M月d日H时m分s秒"));
			context.setAttribute("UpdateTime", timeStamp);
			// Set notice.
			context.setAttribute("Notice", new TodayNotice(this.da.yumi()));
		}
	}
	
	private Yumi yumi;
	private final DataAccess da;
	private final CrawlerTask task;
	private final ScheduledThreadPoolExecutor executor;
	
	// Servlet context.
	private ServletContext context;
	
	public CrawlerInitializer() {
		try {
			if (this.yumi == null)
				this.yumi = new Yumi(Path.of(""), new YumiErrorListener() {

					@Override
					public void error(Exception e) {
						e.printStackTrace();
						
					}});
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
		this.context = sce.getServletContext();
		this.context.setAttribute("DataAccess", this.da);
		
		// Common json builder.
		JsonbConfig config = new JsonbConfig()
				.withPropertyNamingStrategy(PropertyNamingStrategy.IDENTITY)
				.withNullValues(true);
		this.context.setAttribute("Jsonb", JsonbBuilder.create(config));
		
		// Set global content.
		setContent(this.context);
		System.out.println("Crawler task is scheduled.");
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		this.executor.remove(this.task);
		this.executor.shutdownNow();
		
		System.out.println("Crawler task is removed.");
	}

	private void setContent(ServletContext ctx) {
		ctx.setAttribute("IndexTitle", "玉米市场行情");
	}
}

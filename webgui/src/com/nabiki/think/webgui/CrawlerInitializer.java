package com.nabiki.think.webgui;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalTime;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import javax.json.bind.config.PropertyNamingStrategy;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import com.nabiki.think.crawler.yumi.*;

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

	class CrawlerTask extends TimerTask {
		private Yumi yumi;
		
		CrawlerTask() {
		}

		private void initYumi() {
			try {
				this.yumi = new Yumi(Path.of(""), new YumiErrorListener() {
					@Override
					public void error(Exception e) {
						e.printStackTrace();
					}
				});
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void run() {						
			// Load old data for the first run.
			try {
				if (da.yumi().size() == 0) {
					// Init object.
					initYumi();
					da.yumi(yumi.read());
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

			// Don't fetch data at night.
			var now = LocalTime.now();
			if (23 <= now.getHour() || now.getHour() < 6) {
				return;
			}
			
			try {
				var future = CompletableFuture.runAsync(() -> {
					System.out.println("Start fetching data from yumi.com.cn.");
					try {
						yumi.run();
						System.out.println("End fetching data.");
					} catch (IOException e) {
						e.printStackTrace();
					}
				});
				future.get(30, TimeUnit.MINUTES);
				// Set new data into data access.
				da.yumi(this.yumi.lastQuery());
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}
	
	private final DataAccess da = new DataAccess();
	private final CrawlerTask task = new CrawlerTask();
	private final Timer timer = new Timer();

	// Servlet context.
	private ServletContext context;

	public CrawlerInitializer() {
		// Set console file.
		setConsole();
	}

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		this.timer.scheduleAtFixedRate(this.task, 0, TimeUnit.HOURS.toMillis(1));

		// Register data object.
		this.context = sce.getServletContext();
		this.context.setAttribute("DataAccess", this.da);

		// Common json builder.
		JsonbConfig config = new JsonbConfig().withPropertyNamingStrategy(PropertyNamingStrategy.IDENTITY)
				.withNullValues(true);
		this.context.setAttribute("Jsonb", JsonbBuilder.create(config));

		// Set global content.
		setContent(this.context);
		System.out.println("Crawler task is scheduled.");
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		this.timer.cancel();
		System.out.println("Crawler task is stopped.");
	}

	private void setContent(ServletContext ctx) {
		ctx.setAttribute("IndexTitle", "玉米市场行情");
	}
}

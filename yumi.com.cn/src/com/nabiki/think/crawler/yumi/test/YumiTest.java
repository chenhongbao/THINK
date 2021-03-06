package com.nabiki.think.crawler.yumi.test;

import java.io.IOException;
import java.nio.file.Path;

import com.nabiki.think.crawler.yumi.Yumi;
import com.nabiki.think.crawler.yumi.YumiErrorListener;

public class YumiTest {
	public static void main(String[] args) {
		try {
			new Yumi(Path.of(""), new YumiErrorListener() {

				@Override
				public void error(Exception e) {
					e.printStackTrace();
				}
				
			}).run();
		} catch (IOException e) {
			e.printStackTrace();
			e.getCause().printStackTrace();
		}
	}
}

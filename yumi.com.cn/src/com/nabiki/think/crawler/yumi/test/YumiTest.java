package com.nabiki.think.crawler.yumi.test;

import java.io.IOException;

import com.nabiki.think.crawler.yumi.Yumi;

public class YumiTest {
	public static void main(String[] args) {
		try {
			new Yumi().run();
		} catch (IOException e) {
			e.printStackTrace();
			e.getCause().printStackTrace();
		}
	}
}

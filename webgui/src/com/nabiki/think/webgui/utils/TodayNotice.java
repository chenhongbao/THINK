package com.nabiki.think.webgui.utils;

import java.util.HashMap;
import java.util.Map;

import com.nabiki.think.crawler.yumi.data.QueryResult;

public class TodayNotice {
	private Map<Integer, String> notices = new HashMap<>();
	private static int traceBack = 20;
	
	public TodayNotice(Map<Integer, QueryResult> m) {
		for (var key : m.keySet())
			process(key, m.get(key));
	}
	
	public boolean notice(int queryId) {
		return this.notices.containsKey(queryId);
	}
	
	private void process(int queryId, QueryResult r) {
		int size = Math.min(traceBack, r.list.size());
		double[] data = new double[size];
		int count = 0;
		for (int i = r.list.size() - size; i < r.list.size(); ++i, ++count) {
			try {
				data[count] = Double.parseDouble(r.list.get(i).data);
			} catch (NumberFormatException e) {
				data[count] = 0.0D;
			}
		}
		
		if (detect(data))
			this.notices.put(queryId, r.name);
	}
	
    /**
     * Detect the last element is the anomaly of the series.
     *
     * @param data data array
     * @return true if the last element is anomaly, false otherwise
     */
    private boolean detect(double[] data) {
        if (data == null || data.length < 3)
            return false;

        double maxDiff = 0.0;
        for (int i = 1; i < data.length - 1; ++i) {
            var cur = Math.abs(data[i] - data[i - 1]);
            maxDiff = Math.max(cur, maxDiff);
        }

        var lastDiff = Math.abs(data[data.length - 2] - data[data.length - 1]);
        if (lastDiff > maxDiff)
            return true;
        else
            return false;
    }
}

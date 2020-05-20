package com.nabiki.think.crawler.yumi;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import com.nabiki.think.crawler.yumi.data.QueryResult;
import com.nabiki.think.crawler.yumi.data.ValuePair;

public class Utils {
	// TODO Add query IDs.
	public static Set<Integer> ids;
	
	static {
		Utils.ids = new HashSet<>();
		
		ids.add(2075);
		ids.add(461);
	}
	
	public static boolean same(LocalDate d1, LocalDate d2) {
		return d1.getYear() == d2.getYear() && d1.getDayOfYear() == d2.getDayOfYear();
	}
	
	/**
	 * Merge two results. Return null if two results are of different types.
	 * 
	 * @param r1 result to merge
	 * @param r2 result to merge
	 * @return merged result
	 */
	public static QueryResult merge(QueryResult r1, QueryResult r2) {
		if (r1.type != null && r2.type != null && r1.type.compareTo(r2.type) != 0)
			return null;
		
		QueryResult r = new QueryResult();
		r.list.addAll(r1.list);
		
		// Merge r2 into r1.
		for (var c : r2.list) {
			boolean duplicated = false;
			
			for (var x : r.list) {
				if (Utils.same(c.date, x.date)) {
					duplicated = true;
					break;
				}
			}
			
			if (!duplicated)
				r.list.add(c);
		}
		
		// Sort list.
		Collections.sort(r.list, new Comparator<ValuePair>() {

			@Override
			public int compare(ValuePair o1, ValuePair o2) {
				return o1.date.isBefore(o2.date) ? -1 : (o1.date.isAfter(o2.date) ? 1 : 0);
			}});
		r.name = r1.name != null ? r1.name : r2.name;
		r.unit = r1.unit != null ? r1.unit : r2.unit;
		r.type = r1.type != null ? r1.type : r2.type;
		
		return r;
		
	}
}

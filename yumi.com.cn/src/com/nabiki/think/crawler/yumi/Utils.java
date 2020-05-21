package com.nabiki.think.crawler.yumi;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import com.nabiki.think.crawler.yumi.data.QueryResult;
import com.nabiki.think.crawler.yumi.data.ValuePair;

public class Utils {
	public static Set<Integer> ids;
	
	static {
		Utils.ids = new HashSet<>();
		// Average spot price.
		ids.add(2075);
		
		// Dutiable price of American corn.
		ids.add(461);
		
		// DCE corn.
		ids.add(2213);
		ids.add(2214);
		ids.add(2215);
		ids.add(2216);
		ids.add(2217);
		ids.add(2218);
		ids.add(2219);
		
		// Heilongjiang buy price.
		ids.add(224);
		ids.add(225);
		ids.add(226);
		ids.add(227);
		ids.add(228);
		ids.add(229);
		ids.add(230);
		ids.add(231);
		
		// Jilin buy price.
		ids.add(232);
		ids.add(233);
		ids.add(234);
		ids.add(235);
		ids.add(236);
		ids.add(237);
		ids.add(238);
		ids.add(239);
		ids.add(240);
		
		// Liaoning buy price.
		ids.add(241);
		ids.add(242);
		
		// Neimenggu buy price.
		ids.add(223);
		
		// Shandong buy price.
		ids.add(2078);
		ids.add(220);
		ids.add(514);
		ids.add(515);
		
		// Henan buy price.
		ids.add(210);
		ids.add(211);
		ids.add(212);
		ids.add(213);
		
		// Hebei buy price.
		ids.add(202);
		ids.add(203);
		ids.add(204);
		ids.add(205);
		ids.add(207);
		ids.add(208);
		ids.add(209);
		
		// Anhui buy price.
		ids.add(217);
		
		// Shanxi buy price.
		ids.add(197);
		ids.add(200);
		ids.add(201);
		ids.add(517);
		
		// Hanzhong buy price.
		ids.add(192);
		ids.add(194);
		
		// Xinjiang buy price.
		ids.add(184);
		ids.add(185);
		
		// Heilongjiang sell price.
		ids.add(165);
		ids.add(166);
		ids.add(167);
		ids.add(168);
		ids.add(169);
		ids.add(170);
		ids.add(171);
		ids.add(172);
		
		// Jilin sell price.
		ids.add(173);
		ids.add(174);
		ids.add(175);
		ids.add(176);
		ids.add(177);
		ids.add(178);
		ids.add(179);
		ids.add(180);
		ids.add(181);
		
		// Liaoning sell price.
		ids.add(182);
		ids.add(183);
		
		// Neimenggu sell price.
		ids.add(164);
		
		// On-port buy price.
		ids.add(160);
		ids.add(161);
		ids.add(162);
		ids.add(163);
		
		// On-port inventory price.
		ids.add(149);
		ids.add(150);
		ids.add(151);
		ids.add(152);
		
		// On-port trade price.	
		ids.add(153);
		ids.add(154);
		ids.add(155);
		ids.add(156);
		ids.add(157);
		ids.add(158);
		ids.add(159);
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

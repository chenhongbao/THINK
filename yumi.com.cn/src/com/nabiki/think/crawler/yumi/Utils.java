package com.nabiki.think.crawler.yumi;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.nabiki.think.crawler.yumi.data.QueryResult;
import com.nabiki.think.crawler.yumi.data.ValuePair;

public class Utils {
	public static Set<Integer> queryYumiIds(Path path) throws IOException {
		var file = path.toFile();
		if (!file.exists())
			file.createNewFile();
		// Accessibility.
		if (!file.canRead() || !file.canWrite()) {
			file.setReadable(true);
			file.setWritable(true);
		}

		var r = new HashSet<Integer>();
		// Process file line by line.
		String line;
		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			while ((line = reader.readLine()) != null) {
				var v = line.trim();
				// Skip empty line or comment.
				if (v.length() == 0 || v.startsWith("#") || v.startsWith("//"))
					continue;

				r.add(Integer.valueOf(v));
			}
		}

		return r;
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
		if (r1 != null && r2 == null)
			return r1;
		
		if (r1 == null && r2 != null)
			return r2;
		
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
			}
		});
		
		// Validate date.
		// Ensure all dates are in a continuous time line, day by day.
		validateDate(r.list);
		
		// Set meta data.
		r.name = r1.name != null ? r1.name : r2.name;
		r.unit = r1.unit != null ? r1.unit : r2.unit;
		r.type = r1.type != null ? r1.type : r2.type;

		return r;
	}
	
	// Input list must have been sorted by date.
    protected static void validateDate(List<ValuePair> sorted) {
        if (sorted.size() < 2)
            return;

        int index = sorted.size() - 1;
        while (--index >= 0) {
            var epoch0 = sorted.get(index).date.toEpochDay();
            var epoch1 = sorted.get(index + 1).date.toEpochDay();

            // Compare epoch day, back to front, find the diff larger than two weeks.
            // Remove all values before the index because they are not continuous.
            if (Math.abs(epoch1 - epoch0) > 14) {
                index += 1;
                break;
            }
        }

        if (index > 0)
            sorted.subList(0, index).clear();
    }
}

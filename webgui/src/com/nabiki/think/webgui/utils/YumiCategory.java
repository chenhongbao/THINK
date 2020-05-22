package com.nabiki.think.webgui.utils;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class YumiCategory {
	private Map<String, Collection<YumiItem>> category = new HashMap<>();
	
	public YumiCategory(YumiCatalog cat) {
		this.initCategory(cat);
	}
	
	public Collection<YumiItem> category(String categoryName) {
		return this.category.get(categoryName);
	}
	
	public Collection<String> categoryNames() {
		return this.category.keySet();
	}
	
	private void initCategory(YumiCatalog category) {
		for (var pair : category.list) {
			var i = pair.value.indexOf("-");
			var x = pair.value.lastIndexOf("-");
			
			String cat, display;
			// Extract category.
			if (i == -1) {
				cat = pair.value;
			} else if (i == 0) {
				cat = pair.value.substring(1);
			} else {
				cat = pair.value.substring(0, i);
			}
			
			// Extract display name.
			if (x == -1) {
				display = pair.value;
			} else if(x == pair.value.length() - 1) {
				display = pair.value.substring(i + 1, x);
			} else {
				display = pair.value.substring(x + 1);
			}
			
			addItem(pair.key, cat, display);
		}
	}
	
	private void addItem(int id, String cat, String display) {
		if (!this.category.containsKey(cat))
			this.category.put(cat, new LinkedList<YumiItem>());
		
		this.category.get(cat).add(new YumiItem(id, cat, display));
	}
}

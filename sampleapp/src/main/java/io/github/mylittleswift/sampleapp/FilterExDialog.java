package io.github.mylittleswift.sampleapp;

import android.os.Bundle;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FilterExDialog extends ExDialog {

	// Intent Parameter:
	// Explorer activity's title : getStringExtra("explorer_title");
	// URI : .getData().getPath();
	// Filtered Suffix :getStringExtra("filter_suffix");
	//
	//
	//
	
	private String filterSuffix = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		this.filterSuffix = getIntent().getStringExtra("filter_suffix");
		super.onCreate(savedInstanceState);
	}

	@Override
	protected List<Map<String, Object>> getData() {

		ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		File f = new File(mDir);
		File[] files = f.listFiles();
		Map<String, Object> map = null;
		if (!mDir.equals("/sdcard")) {

			map = new HashMap<String, Object>();
			map.put("title", "Back to ..\\");
			map.put("info", f.getParent());
			map.put("img", R.drawable.ex_folder);
			list.add(map);
		}

		for (File temp : files) {
			map = new HashMap<String, Object>();

			if (temp.isDirectory()) {

				map.put("img", R.drawable.ex_folder);
			} else {

				if (!temp.getName().toLowerCase().contains(this.filterSuffix)) {
					continue;
				}

				map.put("img", R.drawable.ex_doc);

			}
			map.put("title", temp.getName());
			map.put("info", temp.getPath());

			list.add(map);
		}
		return list;
	}
}

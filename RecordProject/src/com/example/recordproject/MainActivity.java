package com.example.recordproject;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.recordproject.widget.RecordView;
import com.example.recordproject.widget.interf.IRecordListener;

public class MainActivity extends Activity {

	private RecordView mRecordView;
	private int count = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initViews();
	}

	private void initViews() {
		mRecordView = (RecordView) findViewById(R.id.mRecordView);
		mRecordView.setRecordListener(new IRecordListener() {

			@Override
			public void stop() {
				Log.e("TAG", "-----停止-----");
			}

			@Override
			public void start() {
				Log.e("TAG", "-----开启-----");
			}

			@Override
			public void up() {
				Log.e("TAG", "-----向上切歌-----");
			}

			@Override
			public void down() {
				Log.e("TAG", "-----向下切歌-----");
			}

		});
	}
}

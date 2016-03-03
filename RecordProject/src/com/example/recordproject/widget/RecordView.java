package com.example.recordproject.widget;

import com.example.recordproject.widget.interf.IRecordListener;

import android.content.Context;
import android.util.AttributeSet;

public class RecordView extends BaseRecordView {

	public RecordView(Context context) {
		this(context, null);
	}

	public RecordView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public RecordView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public void setRecordListener(IRecordListener listener) {
		super.initListener(listener);
	}

	@Override
	public void start() {
		startPoleAnimation();
	}

	@Override
	public void stop() {
		stopPoleAnimation();
	}

}

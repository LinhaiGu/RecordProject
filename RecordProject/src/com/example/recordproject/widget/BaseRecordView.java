package com.example.recordproject.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.recordproject.R;
import com.example.recordproject.widget.interf.IRecordListener;

/**
 * 唱片
 * 
 * @author Linhai Gu
 * 
 */
public abstract class BaseRecordView extends LinearLayout {

	/**
	 * 上下文
	 */
	protected Context mContext;
	/**
	 * 碟片
	 */
	private FrameLayout fl_dvd;
	/**
	 * 碟片上面
	 */
	private FrameLayout fl_dvd2;
	/**
	 * 碟片动画
	 */
	private Animation mDVDAnimtion;
	/**
	 * 碟片播放动画的速度匀速
	 */
	private LinearInterpolator mDVDLin;

	/**
	 * 杆
	 */
	private ImageView iv_pole;
	/**
	 * 杆动画
	 */
	private Animation mPoleAnimtion;
	/**
	 * 杆播放动画的速度匀速
	 */
	private LinearInterpolator mPoleLin;
	/**
	 * 向上切歌
	 */
	private Animation mUpAnimation;
	/**
	 * 向下切歌
	 */
	private Animation mDownAnimation;

	/**
	 * 滑动的最小距离
	 */
	private int mTouchSlop;
	/**
	 * 浮层碟片
	 */
	private ImageView iv_center2;
	private ImageView iv_background2;
	private View mPole;
	/**
	 * 是否正在播放
	 */
	private boolean isStart = false;
	/**
	 * 杆是否在碟片上
	 */
	private boolean isPole = false;

	private IRecordListener mIRecordListener;

	protected abstract void setRecordListener(IRecordListener listener);

	protected abstract void start();

	protected abstract void stop();

	public BaseRecordView(Context context) {
		this(context, null);
	}

	public BaseRecordView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public BaseRecordView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.mContext = context;
		init();
		initAnimation();
		initEvent();
	}

	/**
	 * 初始化View
	 * 
	 * @param context
	 */
	private void init() {
		mTouchSlop = ViewConfiguration.get(mContext).getScaledTouchSlop();
		LayoutInflater.from(mContext).inflate(R.layout.record_layout, this);
		fl_dvd = (FrameLayout) findViewById(R.id.fl_dvd);// 碟片
		fl_dvd2 = (FrameLayout) findViewById(R.id.fl_dvd2);
		iv_background2 = (ImageView) findViewById(R.id.iv_background2);
		iv_center2 = (ImageView) findViewById(R.id.iv_center2);
		iv_pole = (ImageView) findViewById(R.id.iv_pole);// 杆
		mPole = findViewById(R.id.id_pole);
	}

	/**
	 * <pre>
	 * {@link #initDVDAnimation}初始化碟片动画
	 * {@link #initDVDUpAnimation}初始向上切歌的动画
	 *  {@link #initDVDDownAnimation}初始向下切歌的动画
	 * </pre>
	 */
	private void initAnimation() {
		initDVDAnimation();
		initDVDUpAnimation();
		initDVDDownAnimation();
	}

	/**
	 * 碟片的触摸事件
	 */
	private OnTouchListener mOnTouchListener = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			int oldY = (int) event.getX();
			int newY = (int) event.getY();
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				oldY = (int) event.getY();
				break;
			case MotionEvent.ACTION_MOVE:
				newY = (int) event.getY();
				stopDVDAnimation();
				break;
			case MotionEvent.ACTION_UP:
				if (isStart && oldY - newY > mTouchSlop && (newY < oldY)) {
					up();
				} else if (isStart && newY - oldY > mTouchSlop && (newY > oldY)) {
					down();
				} else if (isPole) {
					startDVDAnimation();
				}
				break;
			default:
				break;
			}
			return true;
		}
	};

	/**
	 * 初始化事件
	 */
	private void initEvent() {
		/**
		 * 碟片的触摸事件
		 */
		iv_background2.setOnTouchListener(mOnTouchListener);
		iv_center2.setOnTouchListener(mOnTouchListener);

		/**
		 * 杆触摸事件
		 */
		iv_pole.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					break;
				case MotionEvent.ACTION_MOVE:
					break;
				case MotionEvent.ACTION_UP:
					if (!isStart) {
						/*
						 * 当停止播放时，拨动杆，播放
						 */
						isPole = true;
						startPoleAnimation();
					}
					break;
				default:
					break;
				}
				return true;
			}
		});

		/**
		 * 虚拟位置的触摸
		 */
		mPole.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					break;
				case MotionEvent.ACTION_MOVE:
					break;
				case MotionEvent.ACTION_UP:
					if (isStart) {
						/*
						 * 当播放时，拨动杆，停止
						 */
						isPole = false;
						stopPoleAnimation();
					}
					break;
				default:
					break;
				}
				return true;
			}
		});
	}

	/**
	 * 碟片的播放动画
	 */
	private void initDVDAnimation() {
		mDVDLin = new LinearInterpolator();
		mDVDAnimtion = AnimationUtils.loadAnimation(mContext, R.anim.dvd_anim);
		mDVDAnimtion.setInterpolator(mDVDLin);
		mDVDAnimtion.setFillAfter(true);
		mDVDAnimtion.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {

			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}
		});
	}

	/**
	 * 碟片向上切歌动画
	 */
	private void initDVDUpAnimation() {
		mUpAnimation = AnimationUtils.loadAnimation(mContext, R.anim.up_anim);
		mUpAnimation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				mIRecordListener.up();
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				fl_dvd2.setVisibility(View.VISIBLE);
				startDVDAnimation();
			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}
		});
	}

	/**
	 * 碟片向下切歌动画
	 */
	private void initDVDDownAnimation() {
		mDownAnimation = AnimationUtils.loadAnimation(mContext,
				R.anim.down_anim);
		mDownAnimation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				mIRecordListener.down();
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				fl_dvd2.setVisibility(View.VISIBLE);
				startDVDAnimation();

			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}
		});
	}

	/**
	 * 杆子启动动画
	 */
	private void initPoleAnimation() {
		mPoleAnimtion = AnimationUtils
				.loadAnimation(mContext, R.anim.pole_anim);
		mPoleLin = new LinearInterpolator();
		mPoleAnimtion.setFillAfter(true);
		mPoleAnimtion.setInterpolator(mPoleLin);
		mPoleAnimtion.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				/*
				 * 杆子启动完毕，说明碟子应该要转动了
				 */
				isStart = true;
				isPole = true;
				startDVDAnimation();
				mIRecordListener.start();
			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}
		});

	}

	/**
	 * 杆复位动画
	 */
	private void initPoleStopAnimation() {
		mPoleAnimtion = AnimationUtils.loadAnimation(mContext,
				R.anim.pole_rest_anim);
		mPoleLin = new LinearInterpolator();
		mPoleAnimtion.setFillAfter(true);
		mPoleAnimtion.setInterpolator(mPoleLin);
		mPoleAnimtion.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				/*
				 * 杆子复位，说明已经停止播放或者播放完毕
				 */
				isPole = false;
				isStart = false;
				stopDVDAnimation();
				mIRecordListener.stop();
			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}
		});

	}

	/**
	 * 播放碟片动画
	 */
	private void startDVDAnimation() {
		fl_dvd.startAnimation(mDVDAnimtion);
	}

	/**
	 * 暂停碟片动画
	 */
	private void stopDVDAnimation() {
		fl_dvd.clearAnimation();
		// mDVDAnimtion.cancel();
	}

	/**
	 * 播放杆的动画
	 */
	protected void startPoleAnimation() {
		initPoleAnimation();
		// mPoleAnimtion.cancel();
		iv_pole.startAnimation(mPoleAnimtion);
	}

	/**
	 * 暂停杆的动画
	 */
	protected void stopPoleAnimation() {
		// iv_pole.clearAnimation();
		initPoleStopAnimation();
		if (mPoleAnimtion != null) {
			mPoleAnimtion.cancel();
		}
		iv_pole.startAnimation(mPoleAnimtion);
	}

	/**
	 * 向上切歌
	 */
	private void up() {
		fl_dvd2.startAnimation(mUpAnimation);
	}

	/**
	 * 向下切歌
	 */
	private void down() {
		fl_dvd2.startAnimation(mDownAnimation);
	}

	/**
	 * 监听
	 * 
	 * @param listener
	 */
	protected void initListener(IRecordListener listener) {
		this.mIRecordListener = listener;
	}
}

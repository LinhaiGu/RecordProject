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
 * ��Ƭ
 * 
 * @author Linhai Gu
 * 
 */
public abstract class BaseRecordView extends LinearLayout {

	/**
	 * ������
	 */
	protected Context mContext;
	/**
	 * ��Ƭ
	 */
	private FrameLayout fl_dvd;
	/**
	 * ��Ƭ����
	 */
	private FrameLayout fl_dvd2;
	/**
	 * ��Ƭ����
	 */
	private Animation mDVDAnimtion;
	/**
	 * ��Ƭ���Ŷ������ٶ�����
	 */
	private LinearInterpolator mDVDLin;

	/**
	 * ��
	 */
	private ImageView iv_pole;
	/**
	 * �˶���
	 */
	private Animation mPoleAnimtion;
	/**
	 * �˲��Ŷ������ٶ�����
	 */
	private LinearInterpolator mPoleLin;
	/**
	 * �����и�
	 */
	private Animation mUpAnimation;
	/**
	 * �����и�
	 */
	private Animation mDownAnimation;

	/**
	 * ��������С����
	 */
	private int mTouchSlop;
	/**
	 * �����Ƭ
	 */
	private ImageView iv_center2;
	private ImageView iv_background2;
	private View mPole;
	/**
	 * �Ƿ����ڲ���
	 */
	private boolean isStart = false;
	/**
	 * ���Ƿ��ڵ�Ƭ��
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
	 * ��ʼ��View
	 * 
	 * @param context
	 */
	private void init() {
		mTouchSlop = ViewConfiguration.get(mContext).getScaledTouchSlop();
		LayoutInflater.from(mContext).inflate(R.layout.record_layout, this);
		fl_dvd = (FrameLayout) findViewById(R.id.fl_dvd);// ��Ƭ
		fl_dvd2 = (FrameLayout) findViewById(R.id.fl_dvd2);
		iv_background2 = (ImageView) findViewById(R.id.iv_background2);
		iv_center2 = (ImageView) findViewById(R.id.iv_center2);
		iv_pole = (ImageView) findViewById(R.id.iv_pole);// ��
		mPole = findViewById(R.id.id_pole);
	}

	/**
	 * <pre>
	 * {@link #initDVDAnimation}��ʼ����Ƭ����
	 * {@link #initDVDUpAnimation}��ʼ�����и�Ķ���
	 *  {@link #initDVDDownAnimation}��ʼ�����и�Ķ���
	 * </pre>
	 */
	private void initAnimation() {
		initDVDAnimation();
		initDVDUpAnimation();
		initDVDDownAnimation();
	}

	/**
	 * ��Ƭ�Ĵ����¼�
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
	 * ��ʼ���¼�
	 */
	private void initEvent() {
		/**
		 * ��Ƭ�Ĵ����¼�
		 */
		iv_background2.setOnTouchListener(mOnTouchListener);
		iv_center2.setOnTouchListener(mOnTouchListener);

		/**
		 * �˴����¼�
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
						 * ��ֹͣ����ʱ�������ˣ�����
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
		 * ����λ�õĴ���
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
						 * ������ʱ�������ˣ�ֹͣ
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
	 * ��Ƭ�Ĳ��Ŷ���
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
	 * ��Ƭ�����и趯��
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
	 * ��Ƭ�����и趯��
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
	 * ������������
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
				 * ����������ϣ�˵������Ӧ��Ҫת����
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
	 * �˸�λ����
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
				 * ���Ӹ�λ��˵���Ѿ�ֹͣ���Ż��߲������
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
	 * ���ŵ�Ƭ����
	 */
	private void startDVDAnimation() {
		fl_dvd.startAnimation(mDVDAnimtion);
	}

	/**
	 * ��ͣ��Ƭ����
	 */
	private void stopDVDAnimation() {
		fl_dvd.clearAnimation();
		// mDVDAnimtion.cancel();
	}

	/**
	 * ���Ÿ˵Ķ���
	 */
	protected void startPoleAnimation() {
		initPoleAnimation();
		// mPoleAnimtion.cancel();
		iv_pole.startAnimation(mPoleAnimtion);
	}

	/**
	 * ��ͣ�˵Ķ���
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
	 * �����и�
	 */
	private void up() {
		fl_dvd2.startAnimation(mUpAnimation);
	}

	/**
	 * �����и�
	 */
	private void down() {
		fl_dvd2.startAnimation(mDownAnimation);
	}

	/**
	 * ����
	 * 
	 * @param listener
	 */
	protected void initListener(IRecordListener listener) {
		this.mIRecordListener = listener;
	}
}

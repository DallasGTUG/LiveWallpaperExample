package com.gtug.wallpaper;

import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

public class DemoService extends WallpaperService {
	// message handler, primary use is to update the screen
	private static final Handler mDemoHandler = new Handler();
	// make sure you give your shared preferences a unique name
	public static final String SHARED_PREFS = "com.gtug.dallas.wall";

	// custom log tag, used to filter output
	//private static final String LOG_TAG = "DemoLiveWallpaper";
	// quick flag to disable all debug output
	//private static boolean mDebugOutput = true;
	
	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public Engine onCreateEngine() {
		return new DigiFrameEngine();
	}
	
	class DigiFrameEngine extends Engine implements SharedPreferences.OnSharedPreferenceChangeListener {
		
		private SharedPreferences mSharedPreferences;
		
		// is the screen currently visible?
		private boolean mVisible;
		
		// singluar paint object, don't re-create this unnecessarily
		private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		
		// wallpaper dimensions, these span many screens
		private int mWallpaperWidth;
		private int mWallpaperHeight;
		// pixel dimensions of actual screen
		private int mScreenWidth;
		private int mScreenHeight;
		private int mOffsetX;
		
		// number of milliseconds in a minute
		//private final int MINUTE = 60000;
		
		// circle attributes
		private float mX;
		private float mY;
		private int mRadius = 50;
		private int mCircleColor = Color.MAGENTA;
		private int mBackgroundColor = Color.BLACK;
			
		// user preferences
		public final static String PREFERENCE_CIRCLE_COLOR 		= "preference_circle_color";
		public final static String PREFERENCE_CIRCLE_RADIUS 	= "preference_circle_radius";
		public final static String PREFERENCE_BACKGROUND_COLOR 	= "preference_background_color";
		// default values for user preferences
		public final static String DEFAULT_CIRCLE_COLOR 	= "50";
		public final static String DEFAULT_CIRCLE_RADIUS 	= "-65281";
		public final static String DEFAULT_BACKGROUND_COLOR = "-16777216";
		
		
		public DigiFrameEngine() {
			mSharedPreferences = getSharedPreferences(SHARED_PREFS, 0);
			mSharedPreferences.registerOnSharedPreferenceChangeListener(this);
			updatePreferences(mSharedPreferences);
		}
		
		public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
			updatePreferences(mSharedPreferences);
		}	
		
		@Override
		public void onCreate(SurfaceHolder surfaceHolder) {
			super.onCreate(surfaceHolder);
			setTouchEventsEnabled(true);
		}

		@Override
		public void onDestroy() {
			super.onDestroy();
			mDemoHandler.removeCallbacks(mDrawDigiFrame);
		}

		@Override
		public void onVisibilityChanged(boolean visible) {
			mVisible = visible;
			if (visible) {
				drawFrame();
			} else {
				mDemoHandler.removeCallbacks(mDrawDigiFrame);
			}
		}
		
		@Override
		public void onSurfaceCreated(SurfaceHolder holder) {
			super.onSurfaceCreated(holder);
		}

		@Override
		public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
			super.onSurfaceChanged(holder, format, width, height);
			mScreenWidth = width;
			mScreenHeight = height;
			mWallpaperWidth = getDesiredMinimumWidth();
			mWallpaperHeight = getDesiredMinimumHeight();
			
			drawFrame();
		}

		@Override
		public void onSurfaceDestroyed(SurfaceHolder holder) {
			super.onSurfaceDestroyed(holder);
			mVisible = false;
			mDemoHandler.removeCallbacks(mDrawDigiFrame);
		}

		@Override
		public void onOffsetsChanged(float xOffset, float yOffset, float xStep, float yStep, int xPixels, int yPixels) {
			mOffsetX = xPixels;
			drawFrame();
		}
		
		@Override
		public void onTouchEvent(MotionEvent event) {
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				mX = event.getX();
				mY = event.getY();
				drawFrame();
            }
            super.onTouchEvent(event);
		}
		
		private final Runnable mDrawDigiFrame = new Runnable() {
			public void run() {
				drawFrame();
			}
		};
		
		public void drawFrame() {
			final SurfaceHolder holder = getSurfaceHolder();
			Canvas c = null;
            try {
                c = holder.lockCanvas();
                if (c != null) {
                    draw(c);
                }
            } finally {
                if (c != null) holder.unlockCanvasAndPost(c);
            }

            // Reschedule the next redraw
            mDemoHandler.removeCallbacks(mDrawDigiFrame);
//            if (mVisible) {
//            	mDemoHandler.postDelayed(mDrawDigiFrame, MINUTE);
//            }
		}
		
		public void draw(Canvas canvas) {
			mPaint.setColor(mBackgroundColor);
			canvas.drawRect(0, 0, mWallpaperWidth, mWallpaperHeight, mPaint);
			
			mPaint.setColor(mCircleColor);
			canvas.drawCircle(mX, mY, mRadius, mPaint);
		}
		
		// update attrs based on user preferences
		public void updatePreferences(SharedPreferences prefs) {
			mCircleColor = Integer.parseInt(prefs.getString(PREFERENCE_CIRCLE_COLOR, DEFAULT_CIRCLE_COLOR));
			mRadius = Integer.parseInt(prefs.getString(PREFERENCE_CIRCLE_RADIUS, DEFAULT_CIRCLE_RADIUS));
			mBackgroundColor = Integer.parseInt(prefs.getString(PREFERENCE_BACKGROUND_COLOR, DEFAULT_BACKGROUND_COLOR));
		}
	}
}
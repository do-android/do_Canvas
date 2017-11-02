package doext.implement.canvas.bean;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import doext.implement.canvas.DoDrawBase;
import doext.implement.canvas.DoPoint;

public class DoBitmapBean extends DoDrawBase {

	private Bitmap mBitmap;
	private DoPoint mStartPoint;

	public DoBitmapBean(Bitmap bmp, DoPoint startPint) {
		this.mBitmap = bmp;
		this.mStartPoint = startPint;
	}

	@Override
	public void draw(Canvas canvas) {
		if (canvas == null || mBitmap == null || mBitmap.isRecycled()) {
			return;
		}
		mPaint.setColor(mColor);
		mPaint.setStrokeCap(mCap);
		mPaint.setStrokeWidth(mWidth);
		mPaint.setStyle(mStyle);

		canvas.drawBitmap(mBitmap, mStartPoint.x, mStartPoint.y, mPaint);
	}
}

package doext.implement.canvas.bean;

import doext.implement.canvas.DoDrawBase;
import doext.implement.canvas.DoPoint;
import android.graphics.Canvas;
import android.graphics.RectF;

public class DoArcBean extends DoDrawBase {

	private DoPoint mStartPoint;
	private DoPoint mEndPoint;
	private int mStartAngle;
	private int mSweepAngle;
	private boolean mIsUseCenter;

	public DoArcBean(DoPoint sp, DoPoint ep, int startAngle, int sweepAngle, boolean isUseCenter) {
		this.mStartPoint = sp;
		this.mEndPoint = ep;
		this.mStartAngle = startAngle;
		this.mSweepAngle = sweepAngle;
		this.mIsUseCenter = isUseCenter;
	}

	@Override
	public void draw(Canvas canvas) {
		if (canvas == null || mStartPoint == null || mEndPoint == null) {
			return;
		}
		mPaint.setColor(mColor);
		mPaint.setStrokeCap(mCap);
		mPaint.setStrokeWidth(mWidth);
		mPaint.setStyle(mStyle);

		RectF oval = new RectF(mStartPoint.x, mStartPoint.y, mEndPoint.x, mEndPoint.y);

		canvas.drawArc(oval, //弧线所使用的矩形区域大小   
				mStartAngle, //开始角度   
				mSweepAngle, //扫过的角度   
				mIsUseCenter, //是否使用中心   
				mPaint);
	}
}

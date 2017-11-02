package doext.implement.canvas.bean;

import doext.implement.canvas.DoDrawBase;
import doext.implement.canvas.DoPoint;
import android.graphics.Canvas;

public class DoLineBean extends DoDrawBase {

	private DoPoint mStartPoint;
	private DoPoint mEndPoint;

	public DoLineBean(DoPoint sp, DoPoint ep) {
		this.mStartPoint = sp;
		this.mEndPoint = ep;
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
		canvas.drawLine(mStartPoint.x, mStartPoint.y, mEndPoint.x, mEndPoint.y, mPaint);
	}
}

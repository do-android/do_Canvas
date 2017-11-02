package doext.implement.canvas.bean;

import doext.implement.canvas.DoDrawBase;
import doext.implement.canvas.DoPoint;
import android.graphics.Canvas;
import android.graphics.RectF;

public class DoRectBean extends DoDrawBase {

	private DoPoint mStartPoint;
	private DoPoint mEndPoint;

	public DoRectBean(DoPoint sp, DoPoint ep) {
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

		RectF oval = new RectF(mStartPoint.x, mStartPoint.y, mEndPoint.x, mEndPoint.y);
		canvas.drawRect(oval, mPaint);
	}
}

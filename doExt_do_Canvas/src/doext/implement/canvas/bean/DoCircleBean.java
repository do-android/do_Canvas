package doext.implement.canvas.bean;

import android.graphics.Canvas;
import doext.implement.canvas.DoDrawBase;
import doext.implement.canvas.DoPoint;

public class DoCircleBean extends DoDrawBase {

	private DoPoint mcenterPoint;
	private int mRadius;

	public DoCircleBean(DoPoint centerPoint, int radius) {
		this.mcenterPoint = centerPoint;
		this.mRadius = radius;
	}

	@Override
	public void draw(Canvas canvas) {
		if (canvas == null || mcenterPoint == null) {
			return;
		}
		mPaint.setColor(mColor);
		mPaint.setStrokeCap(mCap);
		mPaint.setStrokeWidth(mWidth);
		mPaint.setStyle(mStyle);
		canvas.drawCircle(mcenterPoint.x, mcenterPoint.y, mRadius, mPaint);
	}
}

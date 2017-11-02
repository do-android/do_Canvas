package doext.implement.canvas.bean;

import java.util.List;

import doext.implement.canvas.DoDrawBase;
import doext.implement.canvas.DoPoint;
import android.graphics.Canvas;

public class DoPointBean extends DoDrawBase {

	private float[] mPoints;

	public DoPointBean(List<DoPoint> points) {

		int _len = points.size();
		mPoints = new float[_len * 2];
		int j = 0;
		for (int i = 0; i < _len; i++) {
			DoPoint point = points.get(i);
			mPoints[j] = point.x;
			++j;
			mPoints[j] = point.y;
			++j;
		}

	}

	@Override
	public void draw(Canvas canvas) {
		if (canvas == null || mPoints == null || mPoints.length <= 0) {
			return;
		}
		mPaint.setColor(mColor);
		mPaint.setStrokeCap(mCap);
		mPaint.setStrokeWidth(mWidth);
		mPaint.setStyle(mStyle);
		canvas.drawPoints(mPoints, mPaint);
	}

}

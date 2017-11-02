package doext.implement.canvas;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;

public abstract class DoDrawBase {

	protected Paint mPaint;
	protected int mColor;
	protected int mWidth;
	protected Cap mCap;
	protected Style mStyle;

	public void setProperty(Paint paint, int color, int width, Cap cap, Style style) {
		this.mPaint = paint;
		this.mColor = color;
		this.mWidth = width;
		this.mCap = cap;
		this.mStyle = style;
	}

	public abstract void draw(Canvas canvas);
}

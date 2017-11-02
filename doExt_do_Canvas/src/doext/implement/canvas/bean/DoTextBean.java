package doext.implement.canvas.bean;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.graphics.Typeface;
import android.text.TextUtils;
import doext.implement.canvas.DoDrawBase;
import doext.implement.canvas.DoPoint;

public class DoTextBean extends DoDrawBase {

	private String mText;
	private DoPoint mPoint;
	private String mTypeface;
	private float mFontSize;
	private Align mTextAlign;
	private boolean mIsUnderline; //下划线
	private boolean mIsStrikethrough; //删除线
	private float mAngle; //字体旋转角度

	public DoTextBean(String text, DoPoint coord, String typeface, float fontSize, Align textAlign, boolean isUnderline, boolean isStrikethrough, float angle) {
		this.mPoint = coord;
		this.mText = text;
		this.mTypeface = typeface;
		this.mFontSize = fontSize;
		this.mTextAlign = textAlign;
		this.mIsUnderline = isUnderline;
		this.mIsStrikethrough = isStrikethrough;
		this.mAngle = angle;
	}

	@Override
	public void draw(Canvas canvas) {
		if (canvas == null || TextUtils.isEmpty(mText)) {
			return;
		}
		mPaint.setColor(mColor);
		mPaint.setStrokeCap(mCap);
		mPaint.setStrokeWidth(mWidth);
		mPaint.setStyle(Style.FILL);

		mPaint.setStrikeThruText(mIsStrikethrough); //删除线
		mPaint.setUnderlineText(mIsUnderline); //下划线

		mPaint.setTextAlign(mTextAlign);
		mPaint.setTextSize(mFontSize);
//		mPaint.setTypeface(mTypeface);

		if ("bold".equals(mTypeface)) { // 粗体
			setTypeface(Typeface.DEFAULT, Typeface.BOLD);
		} else if ("italic".equals(mTypeface)) { // 斜体
			setTypeface(Typeface.MONOSPACE, Typeface.ITALIC);
		} else if ("bold_italic".equals(mTypeface)) { // 粗斜体
			setTypeface(Typeface.MONOSPACE, Typeface.BOLD_ITALIC);
		} else { // normal
			setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
		}

		FontMetrics fm = mPaint.getFontMetrics();
		drawText(canvas, mText, mPoint.x, mPoint.y + Math.abs(fm.top), mPaint, mAngle);
		mPaint.setStyle(mStyle);
	}

	private void drawText(Canvas canvas, String text, float x, float y, Paint paint, float angle) {
		if (angle != 0) {
			canvas.rotate(angle, x, y);
		}
		canvas.drawText(text, x, y, paint);
		if (angle != 0) {
			canvas.rotate(-angle, x, y);
		}
	}

	public void setTypeface(Typeface tf, int style) {
		if (style > 0) {
			if (tf == null) {
				tf = Typeface.defaultFromStyle(style);
			} else {
				tf = Typeface.create(tf, style);
			}

			setTypeface(tf);
			// now compute what (if any) algorithmic styling is needed
			int typefaceStyle = tf != null ? tf.getStyle() : 0;
			int need = style & ~typefaceStyle;
			mPaint.setFakeBoldText((need & Typeface.BOLD) != 0);
			mPaint.setTextSkewX((need & Typeface.ITALIC) != 0 ? -0.25f : 0);
		} else {
			mPaint.setFakeBoldText(false);
			mPaint.setTextSkewX(0);
			setTypeface(tf);
		}
	}

	public void setTypeface(Typeface tf) {
		if (mPaint.getTypeface() != tf) {
			mPaint.setTypeface(tf);
		}
	}

}

package doext.implement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import core.helper.DoIOHelper;
import core.helper.DoImageLoadHelper;
import core.helper.DoJsonHelper;
import core.helper.DoScriptEngineHelper;
import core.helper.DoTextHelper;
import core.helper.DoUIModuleHelper;
import core.interfaces.DoIBitmap;
import core.interfaces.DoIScriptEngine;
import core.interfaces.DoIUIModuleView;
import core.object.DoInvokeResult;
import core.object.DoMultitonModule;
import core.object.DoUIModule;
import doext.define.do_Canvas_IMethod;
import doext.define.do_Canvas_MAbstract;
import doext.implement.canvas.DoDrawBase;
import doext.implement.canvas.DoPoint;
import doext.implement.canvas.bean.DoArcBean;
import doext.implement.canvas.bean.DoBitmapBean;
import doext.implement.canvas.bean.DoCircleBean;
import doext.implement.canvas.bean.DoLineBean;
import doext.implement.canvas.bean.DoOvalBean;
import doext.implement.canvas.bean.DoPointBean;
import doext.implement.canvas.bean.DoRectBean;
import doext.implement.canvas.bean.DoTextBean;

/**
 * 自定义扩展UIView组件实现类，此类必须继承相应VIEW类，并实现DoIUIModuleView,do_Canvas_IMethod接口；
 * #如何调用组件自定义事件？可以通过如下方法触发事件：
 * this.model.getEventCenter().fireEvent(_messageName, jsonResult);
 * 参数解释：@_messageName字符串事件名称，@jsonResult传递事件参数对象； 获取DoInvokeResult对象方式new
 * DoInvokeResult(this.model.getUniqueKey());
 */
public class do_Canvas_View extends View implements DoIUIModuleView, do_Canvas_IMethod {

	private Paint mPaint;
	private List<DoDrawBase> drawList;

	private double xZoom;
	private double yZoom;

	private int mStrokeColor = Color.BLACK;
	private int mStrokeWidth;
	private Cap mStrokeCap = Cap.BUTT;
	private Style mStyle = Style.FILL_AND_STROKE;

	/**
	 * 每个UIview都会引用一个具体的model实例；
	 */
	private do_Canvas_MAbstract model;

	public do_Canvas_View(Context context) {
		super(context);
	}

	/**
	 * 初始化加载view准备,_doUIModule是对应当前UIView的model实例
	 */
	@Override
	public void loadView(DoUIModule _doUIModule) throws Exception {
		this.model = (do_Canvas_MAbstract) _doUIModule;

		xZoom = this.model.getXZoom();
		yZoom = this.model.getYZoom();

		mPaint = new Paint();
		// 打开抗锯齿。抗锯齿是依赖于算法的，算法决定抗锯齿的效率，在我们绘制棱角分明的图像时，比如一个矩形、一张位图，我们不需要打开抗锯齿。
		mPaint.setAntiAlias(true);
		//设定是否使用图像抖动处理，会使绘制出来的图片颜色更加平滑和饱满，图像更加清晰
		mPaint.setDither(true);

		mStrokeWidth = getStrokeWidth(1);

		mPaint.setColor(mStrokeColor);
		mPaint.setStrokeCap(mStrokeCap);
		mPaint.setStrokeWidth(mStrokeWidth);
		mPaint.setStyle(mStyle);

		drawList = new ArrayList<DoDrawBase>();

	}

	private int getStrokeWidth(int w) {
		return DoUIModuleHelper.getCalcValue(w * (xZoom + yZoom) / 2);
	}

	/**
	 * 动态修改属性值时会被调用，方法返回值为true表示赋值有效，并执行onPropertiesChanged，否则不进行赋值；
	 *
	 * @_changedValues<key,value>属性集（key名称、value值）；
	 */
	@Override
	public boolean onPropertiesChanging(Map<String, String> _changedValues) {
		return true;
	}

	/**
	 * 属性赋值成功后被调用，可以根据组件定义相关属性值修改UIView可视化操作；
	 *
	 * @_changedValues<key,value>属性集（key名称、value值）；
	 */
	@Override
	public void onPropertiesChanged(Map<String, String> _changedValues) {
		DoUIModuleHelper.handleBasicViewProperChanged(this.model, _changedValues);

		if (_changedValues.containsKey("strokeCap")) {
			int _strokeCap = DoTextHelper.strToInt(_changedValues.get("strokeCap"), 0);
			mStrokeCap = _strokeCap == 1 ? Cap.ROUND : Cap.BUTT;
		}

		if (_changedValues.containsKey("strokeColor")) {
			mStrokeColor = DoUIModuleHelper.getColorFromString(_changedValues.get("strokeColor"), Color.BLACK);
		}

		if (_changedValues.containsKey("strokeWidth")) {
			mStrokeWidth = getStrokeWidth(DoTextHelper.strToInt(_changedValues.get("strokeWidth"), 1));
		}

		if (_changedValues.containsKey("isFull")) {
			boolean _isFull = DoTextHelper.strToBool(_changedValues.get("isFull"), true);
			mStyle = _isFull ? Style.FILL_AND_STROKE : Style.STROKE;
		}

	}

	/**
	 * 同步方法，JS脚本调用该组件对象方法时会被调用，可以根据_methodName调用相应的接口实现方法；
	 *
	 * @_methodName 方法名称
	 * @_dictParas 参数（K,V），获取参数值使用API提供DoJsonHelper类；
	 * @_scriptEngine 当前Page JS上下文环境对象
	 * @_invokeResult 用于返回方法结果对象
	 */
	@Override
	public boolean invokeSyncMethod(String _methodName, JSONObject _dictParas, DoIScriptEngine _scriptEngine, DoInvokeResult _invokeResult) throws Exception {
		if ("definePoint".equals(_methodName)) {
			definePoint(_dictParas, _scriptEngine, _invokeResult);
			return true;
		}
		if ("defineLine".equals(_methodName)) {
			defineLine(_dictParas, _scriptEngine, _invokeResult);
			return true;
		}
		if ("defineCircle".equals(_methodName)) {
			defineCircle(_dictParas, _scriptEngine, _invokeResult);
			return true;
		}
		if ("defineOval".equals(_methodName)) {
			defineOval(_dictParas, _scriptEngine, _invokeResult);
			return true;
		}
		if ("defineArc".equals(_methodName)) {
			defineArc(_dictParas, _scriptEngine, _invokeResult);
			return true;
		}
		if ("defineRect".equals(_methodName)) {
			defineRect(_dictParas, _scriptEngine, _invokeResult);
			return true;
		}
		if ("defineText".equals(_methodName)) {
			defineText(_dictParas, _scriptEngine, _invokeResult);
			return true;
		}
		if ("defineImage".equals(_methodName)) {
			defineImage(_dictParas, _scriptEngine, _invokeResult);
			return true;
		}
		if ("paint".equals(_methodName)) {
			paint(_dictParas, _scriptEngine, _invokeResult);
			return true;
		}
		if ("clear".equals(_methodName)) {
			clear(_dictParas, _scriptEngine, _invokeResult);
			return true;
		}

		return true;
	}

	/**
	 * 异步方法（通常都处理些耗时操作，避免UI线程阻塞），JS脚本调用该组件对象方法时会被调用， 可以根据_methodName调用相应的接口实现方法；
	 * @throws Exception
	 *
	 * @_methodName 方法名称
	 * @_dictParas 参数（K,V），获取参数值使用API提供DoJsonHelper类；
	 * @_scriptEngine 当前page JS上下文环境
	 * @_callbackFuncName 回调函数名 #如何执行异步方法回调？可以通过如下方法：
	 *                    _scriptEngine.callback(_callbackFuncName,
	 *                    _invokeResult);
	 *                    参数解释：@_callbackFuncName回调函数名，@_invokeResult传递回调函数参数对象；
	 *                    获取DoInvokeResult对象方式new
	 *                    DoInvokeResult(this.model.getUniqueKey());
	 */
	@Override
	public boolean invokeAsyncMethod(String _methodName, JSONObject _dictParas, DoIScriptEngine _scriptEngine, String _callbackFuncName) throws Exception {
		if ("saveAsBitmap".equals(_methodName)) {
			this.saveAsBitmap(_dictParas, _scriptEngine, _callbackFuncName);
			return true;
		}
		return false;
	}

	/**
	 * 释放资源处理，前端JS脚本调用closePage或执行removeui时会被调用；
	 */
	@Override
	public void onDispose() {
	}

	/**
	 * 获取当前model实例
	 */
	@Override
	public DoUIModule getModel() {
		return model;
	}

	/**
	 * 画弧；
	 *
	 * @_dictParas 参数（K,V），可以通过此对象提供相关方法来获取参数值（Key：为参数名称）；
	 * @_scriptEngine 当前Page JS上下文环境对象
	 * @_invokeResult 用于返回方法结果对象
	 */
	@Override
	public void defineArc(JSONObject _dictParas, DoIScriptEngine _scriptEngine, DoInvokeResult _invokeResult) throws Exception {

		JSONObject _start = DoJsonHelper.getJSONObject(_dictParas, "start");
		JSONObject _end = DoJsonHelper.getJSONObject(_dictParas, "end");

		if (_start == null || _end == null) {
			throw new Exception("start或end参数值不能为空！");
		}

		DoPoint _p1 = new DoPoint();
		_p1.x = (float) (DoJsonHelper.getInt(_start, "x", 0) * xZoom);
		_p1.y = (float) (DoJsonHelper.getInt(_start, "y", 0) * yZoom);

		DoPoint _p2 = new DoPoint();
		_p2.x = (float) (DoJsonHelper.getInt(_end, "x", 0) * xZoom);
		_p2.y = (float) (DoJsonHelper.getInt(_end, "y", 0) * yZoom);

		int _startAngle = DoJsonHelper.getInt(_dictParas, "startAngle", 0);
		int _sweepAngle = DoJsonHelper.getInt(_dictParas, "sweepAngle", 90);
		boolean _isUseCenter = DoJsonHelper.getBoolean(_dictParas, "useCenter", true);

		DoArcBean _drawArc = new DoArcBean(_p1, _p2, _startAngle, _sweepAngle, _isUseCenter);
		_drawArc.setProperty(mPaint, mStrokeColor, mStrokeWidth, mStrokeCap, mStyle);
		drawList.add(_drawArc);
	}

	/**
	 * 画圆；
	 *
	 * @_dictParas 参数（K,V），可以通过此对象提供相关方法来获取参数值（Key：为参数名称）；
	 * @_scriptEngine 当前Page JS上下文环境对象
	 * @_invokeResult 用于返回方法结果对象
	 */
	@Override
	public void defineCircle(JSONObject _dictParas, DoIScriptEngine _scriptEngine, DoInvokeResult _invokeResult) throws Exception {
		JSONObject _center = DoJsonHelper.getJSONObject(_dictParas, "point");
		int _radius = DoJsonHelper.getInt(_dictParas, "radius", -1);

		if (_center == null) {
			throw new Exception("point参数值不能为空！");
		}

		if (_radius <= 0) {
			throw new Exception("radius参数值为空或不正确！");
		}

		DoPoint _centerPoint = new DoPoint();
		_centerPoint.x = (float) (DoJsonHelper.getInt(_center, "x", 0) * xZoom);
		_centerPoint.y = (float) (DoJsonHelper.getInt(_center, "y", 0) * yZoom);

		DoCircleBean _drawCircle = new DoCircleBean(_centerPoint, getStrokeWidth(_radius));
		_drawCircle.setProperty(mPaint, mStrokeColor, mStrokeWidth, mStrokeCap, mStyle);
		drawList.add(_drawCircle);
	}

	/**
	 * 画线；
	 *
	 * @_dictParas 参数（K,V），可以通过此对象提供相关方法来获取参数值（Key：为参数名称）；
	 * @_scriptEngine 当前Page JS上下文环境对象
	 * @_invokeResult 用于返回方法结果对象
	 */
	@Override
	public void defineLine(JSONObject _dictParas, DoIScriptEngine _scriptEngine, DoInvokeResult _invokeResult) throws Exception {
		JSONObject _start = DoJsonHelper.getJSONObject(_dictParas, "start");
		JSONObject _end = DoJsonHelper.getJSONObject(_dictParas, "end");

		if (_start == null || _end == null) {
			throw new Exception("start或end参数值不能为空！");
		}

		DoPoint _p1 = new DoPoint();
		_p1.x = (float) (DoJsonHelper.getInt(_start, "x", 0) * xZoom);
		_p1.y = (float) (DoJsonHelper.getInt(_start, "y", 0) * yZoom);

		DoPoint _p2 = new DoPoint();
		_p2.x = (float) (DoJsonHelper.getInt(_end, "x", 0) * xZoom);
		_p2.y = (float) (DoJsonHelper.getInt(_end, "y", 0) * yZoom);

		DoLineBean _drawLine = new DoLineBean(_p1, _p2);
		_drawLine.setProperty(mPaint, mStrokeColor, mStrokeWidth, mStrokeCap, mStyle);
		drawList.add(_drawLine);
	}

	/**
	 * 画椭圆；
	 *
	 * @_dictParas 参数（K,V），可以通过此对象提供相关方法来获取参数值（Key：为参数名称）；
	 * @_scriptEngine 当前Page JS上下文环境对象
	 * @_invokeResult 用于返回方法结果对象
	 */
	@Override
	public void defineOval(JSONObject _dictParas, DoIScriptEngine _scriptEngine, DoInvokeResult _invokeResult) throws Exception {
		JSONObject _start = DoJsonHelper.getJSONObject(_dictParas, "start");
		JSONObject _end = DoJsonHelper.getJSONObject(_dictParas, "end");

		if (_start == null || _end == null) {
			throw new Exception("start或end参数值不能为空！");
		}

		DoPoint _p1 = new DoPoint();
		_p1.x = (float) (DoJsonHelper.getInt(_start, "x", 0) * xZoom);
		_p1.y = (float) (DoJsonHelper.getInt(_start, "y", 0) * yZoom);

		DoPoint _p2 = new DoPoint();
		_p2.x = (float) (DoJsonHelper.getInt(_end, "x", 0) * xZoom);
		_p2.y = (float) (DoJsonHelper.getInt(_end, "y", 0) * yZoom);

		DoOvalBean _drawOval = new DoOvalBean(_p1, _p2);
		_drawOval.setProperty(mPaint, mStrokeColor, mStrokeWidth, mStrokeCap, mStyle);
		drawList.add(_drawOval);
	}

	/**
	 * 画点；
	 *
	 * @_dictParas 参数（K,V），可以通过此对象提供相关方法来获取参数值（Key：为参数名称）；
	 * @_scriptEngine 当前Page JS上下文环境对象
	 * @_invokeResult 用于返回方法结果对象
	 */
	@Override
	public void definePoint(JSONObject _dictParas, DoIScriptEngine _scriptEngine, DoInvokeResult _invokeResult) throws Exception {
		JSONArray _points = DoJsonHelper.getJSONArray(_dictParas, "points");
		if (_points == null) {
			return;
		}
		int _len = _points.length();
		List<DoPoint> _mPoints = new ArrayList<DoPoint>();
		for (int i = 0; i < _len; i++) {
			JSONObject _obj = _points.getJSONObject(i);
			if (_obj == null) {
				continue;
			}
			DoPoint _point = new DoPoint();
			_point.x = (float) (DoJsonHelper.getInt(_obj, "x", 0) * xZoom);
			_point.y = (float) (DoJsonHelper.getInt(_obj, "y", 0) * yZoom);
			_mPoints.add(_point);
		}

		DoPointBean _drawPoint = new DoPointBean(_mPoints);
		_drawPoint.setProperty(mPaint, mStrokeColor, mStrokeWidth, mStrokeCap, mStyle);
		drawList.add(_drawPoint);
	}

	/**
	 * 画矩形；
	 *
	 * @_dictParas 参数（K,V），可以通过此对象提供相关方法来获取参数值（Key：为参数名称）；
	 * @_scriptEngine 当前Page JS上下文环境对象
	 * @_invokeResult 用于返回方法结果对象
	 */
	@Override
	public void defineRect(JSONObject _dictParas, DoIScriptEngine _scriptEngine, DoInvokeResult _invokeResult) throws Exception {
		JSONObject _start = DoJsonHelper.getJSONObject(_dictParas, "start");
		JSONObject _end = DoJsonHelper.getJSONObject(_dictParas, "end");

		if (_start == null || _end == null) {
			throw new Exception("start或end参数值不能为空！");
		}

		DoPoint _p1 = new DoPoint();
		_p1.x = (float) (DoJsonHelper.getInt(_start, "x", 0) * xZoom);
		_p1.y = (float) (DoJsonHelper.getInt(_start, "y", 0) * yZoom);

		DoPoint _p2 = new DoPoint();
		_p2.x = (float) (DoJsonHelper.getInt(_end, "x", 0) * xZoom);
		_p2.y = (float) (DoJsonHelper.getInt(_end, "y", 0) * yZoom);

		DoRectBean _drawRect = new DoRectBean(_p1, _p2);
		_drawRect.setProperty(mPaint, mStrokeColor, mStrokeWidth, mStrokeCap, mStyle);
		drawList.add(_drawRect);
	}

	/**
	 * 画文字；
	 *
	 * @_dictParas 参数（K,V），可以通过此对象提供相关方法来获取参数值（Key：为参数名称）；
	 * @_scriptEngine 当前Page JS上下文环境对象
	 * @_invokeResult 用于返回方法结果对象
	 */
	@Override
	public void defineText(JSONObject _dictParas, DoIScriptEngine _scriptEngine, DoInvokeResult _invokeResult) throws Exception {
		String _text = DoJsonHelper.getString(_dictParas, "text", "");
//		if (TextUtils.isEmpty(_text)) {
//			throw new Exception("text参数值不能为空！");
//		}

		JSONObject _coord = DoJsonHelper.getJSONObject(_dictParas, "coord");
		DoPoint _coordPoint = new DoPoint();
		if (_coord != null) {
			_coordPoint.x = (float) (DoJsonHelper.getInt(_coord, "x", 0) * xZoom);
			_coordPoint.y = (float) (DoJsonHelper.getInt(_coord, "y", 0) * yZoom);
		}

		String _fontStyle = DoJsonHelper.getString(_dictParas, "fontStyle", "normal");
		String _textFlag = DoJsonHelper.getString(_dictParas, "textFlag", "normal");
		String _textAlign = DoJsonHelper.getString(_dictParas, "textAlign", "left");
		float _fontSize = DoUIModuleHelper.getDeviceFontSize(model, DoJsonHelper.getInt(_dictParas, "fontSize", 17) + "");
		float _angle = DoJsonHelper.getInt(_dictParas, "angle", 0);

		boolean _isUnderline = false;
		boolean _isStrikethrough = false;

		if ("underline".equals(_textFlag)) { // 下划线
			_isUnderline = true;
		} else if ("strikethrough".equals(_textFlag)) { // 删除线
			_isStrikethrough = true;
		}

		Align _align = Align.LEFT;
		if ("center".equals(_textAlign)) { // 下划线
			_align = Align.CENTER;
		} else if ("right".equals(_textAlign)) { // 删除线
			_align = Align.RIGHT;
		}

		Context c = getContext();
		Resources r;
		if (c == null) {
			r = Resources.getSystem();
		} else {
			r = c.getResources();
		}
		_fontSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, _fontSize, r.getDisplayMetrics());

		DoTextBean _drawText = new DoTextBean(_text, _coordPoint, _fontStyle, _fontSize, _align, _isUnderline, _isStrikethrough, _angle);
		_drawText.setProperty(mPaint, mStrokeColor, mStrokeWidth, mStrokeCap, mStyle);
		drawList.add(_drawText);

	}

	/**
	 * 画图片；
	 *
	 * @_dictParas 参数（K,V），可以通过此对象提供相关方法来获取参数值（Key：为参数名称）；
	 * @_scriptEngine 当前Page JS上下文环境对象
	 * @_invokeResult 用于返回方法结果对象
	 */
	@Override
	public void defineImage(JSONObject _dictParas, DoIScriptEngine _scriptEngine, DoInvokeResult _invokeResult) throws Exception {

		String _source = DoJsonHelper.getString(_dictParas, "source", "");
		if (TextUtils.isEmpty(_source)) {
			throw new Exception("source参数值不能为空！");
		}
		Bitmap _bitmap = null;
		if (_source.startsWith("@")) {
			DoMultitonModule _multitonModule = DoScriptEngineHelper.parseMultitonModule(_scriptEngine, _source);
			if (_multitonModule != null) {
				if (_multitonModule instanceof DoIBitmap) {
					_bitmap = ((DoIBitmap) _multitonModule).getData();
				}
			}
		}

		if (_bitmap == null) {
			String _path = DoIOHelper.getLocalFileFullPath(this.model.getCurrentPage().getCurrentApp(), _source);
			_bitmap = DoImageLoadHelper.getInstance().loadLocal(_path, -1, -1);
		}

		JSONObject _coord = DoJsonHelper.getJSONObject(_dictParas, "coord");
		DoPoint _coordPoint = new DoPoint();
		if (_coord != null) {
			_coordPoint.x = (float) (DoJsonHelper.getInt(_coord, "x", 0) * xZoom);
			_coordPoint.y = (float) (DoJsonHelper.getInt(_coord, "y", 0) * yZoom);
		}

		DoBitmapBean _drawBitmap = new DoBitmapBean(_bitmap, _coordPoint);
		_drawBitmap.setProperty(mPaint, mStrokeColor, mStrokeWidth, mStrokeCap, mStyle);
		drawList.add(_drawBitmap);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		for (int i = 0; i < drawList.size(); i++) {
			drawList.get(i).draw(canvas);
		}
	}

	@Override
	public void paint(JSONObject _dictParas, DoIScriptEngine _scriptEngine, DoInvokeResult _invokeResult) throws Exception {
		invalidate();
	}

	/**
	 * 保存为Bitmap；
	 *
	 * @throws Exception
	 * @_dictParas 参数（K,V），可以通过此对象提供相关方法来获取参数值（Key：为参数名称）；
	 * @_scriptEngine 当前Page JS上下文环境对象
	 * @_callbackFuncName 回调函数名
	 */
	@Override
	public void saveAsBitmap(JSONObject _dictParas, DoIScriptEngine _scriptEngine, String _callbackFuncName) throws Exception {
		String _address = DoJsonHelper.getString(_dictParas, "bitmap", "");
		if (_address == null || _address.length() <= 0)
			throw new Exception("bitmap参数不能为空！");
		DoMultitonModule _multitonModule = DoScriptEngineHelper.parseMultitonModule(_scriptEngine, _address);
		if (_multitonModule == null)
			throw new Exception("bitmap参数无效！");
		if (_multitonModule instanceof DoIBitmap) {
			DoIBitmap _bitmap = (DoIBitmap) _multitonModule;
			this.destroyDrawingCache();
			this.setDrawingCacheEnabled(true);
			this.buildDrawingCache();
			Bitmap _mBitmap = this.getDrawingCache();
			_bitmap.setData(_mBitmap);
			DoInvokeResult _invokeResult = new DoInvokeResult(model.getUniqueKey());
			_scriptEngine.callback(_callbackFuncName, _invokeResult);
		}

	}

	@Override
	public void clear(JSONObject _dictParas, DoIScriptEngine _scriptEngine, DoInvokeResult _invokeResult) throws Exception {
		//清空画布
		drawList.clear();
		invalidate();
	}

	/**
	 * 重绘组件，构造组件时由系统框架自动调用；
	 * 或者由前端JS脚本调用组件onRedraw方法时被调用（注：通常是需要动态改变组件（X、Y、Width、Height）属性时手动调用）
	 */
	@Override
	public void onRedraw() {
		this.setLayoutParams(DoUIModuleHelper.getLayoutParams(this.model));
	}
}
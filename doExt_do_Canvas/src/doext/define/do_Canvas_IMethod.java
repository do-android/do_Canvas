package doext.define;

import org.json.JSONObject;

import core.interfaces.DoIScriptEngine;
import core.object.DoInvokeResult;

/**
 * 声明自定义扩展组件方法
 */
public interface do_Canvas_IMethod {
	void defineArc(JSONObject _dictParas, DoIScriptEngine _scriptEngine, DoInvokeResult _invokeResult) throws Exception;

	void defineCircle(JSONObject _dictParas, DoIScriptEngine _scriptEngine, DoInvokeResult _invokeResult) throws Exception;

	void defineLine(JSONObject _dictParas, DoIScriptEngine _scriptEngine, DoInvokeResult _invokeResult) throws Exception;

	void defineOval(JSONObject _dictParas, DoIScriptEngine _scriptEngine, DoInvokeResult _invokeResult) throws Exception;

	void definePoint(JSONObject _dictParas, DoIScriptEngine _scriptEngine, DoInvokeResult _invokeResult) throws Exception;

	void defineRect(JSONObject _dictParas, DoIScriptEngine _scriptEngine, DoInvokeResult _invokeResult) throws Exception;

	void defineText(JSONObject _dictParas, DoIScriptEngine _scriptEngine, DoInvokeResult _invokeResult) throws Exception;

	void paint(JSONObject _dictParas, DoIScriptEngine _scriptEngine, DoInvokeResult _invokeResult) throws Exception;

	void defineImage(JSONObject _dictParas, DoIScriptEngine _scriptEngine, DoInvokeResult _invokeResult) throws Exception;

	void clear(JSONObject _dictParas, DoIScriptEngine _scriptEngine, DoInvokeResult _invokeResult) throws Exception;

	void saveAsBitmap(JSONObject _dictParas, DoIScriptEngine _scriptEngine, String _callbackFuncName) throws Exception;

}
package doext.define;

import core.object.DoUIModule;
import core.object.DoProperty;
import core.object.DoProperty.PropertyDataType;

public abstract class do_Canvas_MAbstract extends DoUIModule {

	protected do_Canvas_MAbstract() throws Exception {
		super();
	}

	/**
	 * 初始化
	 */
	@Override
	public void onInit() throws Exception {
		super.onInit();
		//注册属性
		this.registProperty(new DoProperty("strokeCap", PropertyDataType.Number, "0", false));
		this.registProperty(new DoProperty("strokeColor", PropertyDataType.String, "00000000", false));
		this.registProperty(new DoProperty("strokeWidth", PropertyDataType.Number, "1", false));
		this.registProperty(new DoProperty("isFull", PropertyDataType.Bool, "true", false));
	}
}
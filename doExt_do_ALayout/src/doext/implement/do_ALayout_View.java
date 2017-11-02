package doext.implement;

import java.util.Map;
import org.json.JSONObject;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.FrameLayout;
import core.DoServiceContainer;
import core.helper.DoJsonHelper;
import core.helper.DoTextHelper;
import core.helper.DoUIModuleHelper;
import core.helper.DoUIModuleHelper.LayoutParamsType;
import core.interfaces.DoIPageView;
import core.interfaces.DoIScriptEngine;
import core.interfaces.DoIUIModuleView;
import core.object.DoInvokeResult;
import core.object.DoUIModule;
import doext.define.do_ALayout_IMethod;
import doext.define.do_ALayout_MAbstract;

/**
 * 自定义扩展UIView组件实现类，此类必须继承相应VIEW类，并实现DoIUIModuleView,do_ALayout_IMethod接口；
 * #如何调用组件自定义事件？可以通过如下方法触发事件：
 * this.model.getEventCenter().fireEvent(_messageName, jsonResult);
 * 参数解释：@_messageName字符串事件名称，@jsonResult传递事件参数对象； 获取DoInvokeResult对象方式new
 * DoInvokeResult(this.model.getUniqueKey());
 */
public class do_ALayout_View extends FrameLayout implements DoIUIModuleView, do_ALayout_IMethod, OnClickListener, OnLongClickListener {

	/**
	 * 每个UIview都会引用一个具体的model实例；
	 */
	private do_ALayout_MAbstract model;
	private DoIPageView pageView;

	public do_ALayout_View(Context context) {
		super(context);
	}

	public DoIPageView getPageView() {
		return pageView;
	}

	public void setPageView(DoIPageView pageView) {
		this.pageView = pageView;
	}

	/**
	 * 初始化加载view准备,_doUIModule是对应当前UIView的model实例
	 */
	@Override
	public void loadView(DoUIModule _doUIModule) throws Exception {
		this.model = (do_ALayout_MAbstract) _doUIModule;
		this.setOnClickListener(this);
		this.setOnLongClickListener(this);
		for (int i = 0; i < this.model.getChildUIModules().size(); i++) {
			DoUIModule _childUI = this.model.getChildUIModules().get(i);
			View _view = (View) _childUI.getCurrentUIModuleView();
			_childUI.setLayoutParamsType(LayoutParamsType.Alayout.toString());
			this.addView(_view);
		}
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
		if (_changedValues.containsKey("enabled")) {
			boolean _isEnable = DoTextHelper.strToBool(_changedValues.get("enabled"), false);
			this.setEnabled(_isEnable);
		}

		if (_changedValues.containsKey("bgImage") || _changedValues.containsKey("bgImageFillType")) {
			try {
				DoUIModuleHelper.setBgImage(this.model, _changedValues);
			} catch (Exception _err) {
				DoServiceContainer.getLogEngine().writeError("DoALayout setBgImage \n", _err);
			}
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
		if ("add".equals(_methodName)) {
			this.add(_dictParas, _scriptEngine, _invokeResult);
			return true;
		}
		return false;
	}

	/**
	 * 异步方法（通常都处理些耗时操作，避免UI线程阻塞），JS脚本调用该组件对象方法时会被调用， 可以根据_methodName调用相应的接口实现方法；
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
	public boolean invokeAsyncMethod(String _methodName, JSONObject _dictParas, DoIScriptEngine _scriptEngine, String _callbackFuncName) {
		//...do something
		return false;
	}

	/**
	 * 释放资源处理，前端JS脚本调用closePage或执行removeui时会被调用；
	 */
	@Override
	public void onDispose() {
		//...do something
	}

	/**
	 * 重绘组件，构造组件时由系统框架自动调用；
	 * 或者由前端JS脚本调用组件onRedraw方法时被调用（注：通常是需要动态改变组件（X、Y、Width、Height）属性时手动调用）
	 * 
	 * @throws Exception
	 */
	@Override
	public void onRedraw() throws Exception {
		this.model.computeSize();
		if (this.model.getLayoutParamsType() != null) {
			boolean _isStretch = DoTextHelper.strToBool(this.model.getPropertyValue("isStretch"), true);
			MarginLayoutParams layoutParams = DoUIModuleHelper.getLayoutParams(this.model);
			if (LayoutParamsType.LinearLayout.toString().equals(this.model.getLayoutParamsType()) && !_isStretch) {
				layoutParams.topMargin = (int) (layoutParams.topMargin + this.model.workAreaY);
				layoutParams.leftMargin = (int) (layoutParams.leftMargin + this.model.workAreaX);
			}
			this.setLayoutParams(layoutParams);
		}
		for (int i = 0; i < this.model.getChildUIModules().size(); i++) {
			DoUIModule _childUI = this.model.getChildUIModules().get(i);
			_childUI.getCurrentUIModuleView().onRedraw();
		}
	}

	/**
	 * 获取当前model实例
	 */
	@Override
	public DoUIModule getModel() {
		return model;
	}

	/**
	 * 插入一个UI；
	 * 
	 * @_dictParas 参数（K,V），可以通过此对象提供相关方法来获取参数值（Key：为参数名称）；
	 * @_scriptEngine 当前Page JS上下文环境对象
	 * @_invokeResult 用于返回方法结果对象
	 */
	@Override
	public void add(JSONObject _dictParas, DoIScriptEngine _scriptEngine, DoInvokeResult _invokeResult) throws Exception {
		String _id = DoJsonHelper.getString(_dictParas, "id", "");
		String _viewtemplate = DoJsonHelper.getString(_dictParas, "path", "");
		String _targetX = DoJsonHelper.getString(_dictParas, "x", "");
		String _targetY = DoJsonHelper.getString(_dictParas, "y", "");
		String _address = this.model.addUI(_scriptEngine, _viewtemplate, _targetX, _targetY, _id);
		_invokeResult.setResultText(_address);
	}

	@Override
	public void onClick(View v) {
		doALayout_Touch();
	}

	@Override
	public boolean onLongClick(View v) {
		doALayout_LongTouch();
		return true;//true添加短板震动效果
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (!isEnabled() || (!this.model.getEventCenter().containsEvent("touch") && !this.model.getEventCenter().containsEvent("longTouch"))) {
			return false;
		}
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			doALayout_TouchDown();
			break;
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			doALayout_TouchUp();
			break;
		}
		return super.onTouchEvent(event);
	}

	private void doALayout_Touch() {
		DoInvokeResult _invokeResult = new DoInvokeResult(this.model.getUniqueKey());
		this.model.getEventCenter().fireEvent("touch", _invokeResult);
	}

	private void doALayout_LongTouch() {
		DoInvokeResult _invokeResult = new DoInvokeResult(this.model.getUniqueKey());
		this.model.getEventCenter().fireEvent("longTouch", _invokeResult);
	}

	private void doALayout_TouchUp() {
		DoInvokeResult _invokeResult = new DoInvokeResult(this.model.getUniqueKey());
		this.model.getEventCenter().fireEvent("touchUp", _invokeResult);
	}

	private void doALayout_TouchDown() {
		DoInvokeResult _invokeResult = new DoInvokeResult(this.model.getUniqueKey());
		this.model.getEventCenter().fireEvent("touchDown", _invokeResult);
	}

}
package doext.define;

import org.json.JSONObject;

import android.view.View;
import android.view.ViewGroup;
import core.helper.DoTextHelper;
import core.helper.DoUIModuleHelper;
import core.helper.DoUIModuleHelper.LayoutParamsType;
import core.object.DoUIModule;
import core.object.DoProperty;
import core.object.DoProperty.PropertyDataType;
import core.object.DoUIModuleCollection;

public abstract class do_ALayout_MAbstract extends DoUIModuleCollection {

	protected do_ALayout_MAbstract() throws Exception {
		super();
	}

	/**
	 * 初始化
	 */
	@Override
	public void onInit() throws Exception {
		super.onInit();
		//注册属性
		this.registProperty(new DoProperty("bgImage", PropertyDataType.String, "", false));
		this.registProperty(new DoProperty("bgImageFillType", PropertyDataType.String, "fillxy", false));
		this.registProperty(new DoProperty("enabled", PropertyDataType.Bool, "true", false));
		this.registProperty(new DoProperty("isStretch", PropertyDataType.Bool, "true", true));
		this.registProperty(new DoProperty("layoutAlign", PropertyDataType.String, "MiddelCenter", true));
	}

	// 控件转换到设备上的坐标和尺寸
	@Override
	public double getRealX() {
		return super.getRealX() + this.workAreaX;
	}

	@Override
	public double getRealY() {
		return super.getRealY() + this.workAreaY;
	}

	@Override
	public double getRealWidth() {
		return this.workAreaWidth;
	}

	@Override
	public double getRealHeight() {

		return this.workAreaHeight;

	}

	public double workAreaX;
	public double workAreaY;
	private double workAreaWidth;
	private double workAreaHeight;

	// 装载配置
	@Override
	public void loadModel(JSONObject _moduleNode) throws Exception {
		super.loadModel(_moduleNode);
	}

	public void computeSize() throws Exception {
		boolean _isStretch = DoTextHelper.strToBool(this.getPropertyValue("isStretch"), true);
		String _layoutAlign = this.getPropertyValue("layoutAlign");
		if (_layoutAlign == null)
			_layoutAlign = "MiddelCenter";
		if (!_isStretch) {
			double _zoom = Math.min(this.getXZoom(), this.getYZoom());

			this.workAreaWidth = this.getWidth() * _zoom;
			if (_zoom < this.getXZoom()) {
				// 九种备选值：TopLeft, TopCenter, TopRight, MiddleLeft, MiddleCenter,
				// MiddleRight, BottomLeft, BottomCenter, BottomRight
				if (_layoutAlign.endsWith("Left")) {
					this.workAreaX = 0;
				} else if (_layoutAlign.endsWith("Right")) {
					this.workAreaX = super.getRealWidth() - this.getWidth() * _zoom;
				} else {
					// 默认Middle
					this.workAreaX = (super.getRealWidth() - this.getWidth() * _zoom) / 2;
				}
			} else {
				this.workAreaX = 0;
			}
			this.workAreaHeight = this.getHeight() * _zoom;
			if (_zoom < this.getYZoom()) {
				// 九种备选值：TopLeft, TopCenter, TopRight, MiddleLeft, MiddleCenter,
				// MiddleRight, BottomLeft, BottomCenter, BottomRight
				if (_layoutAlign.startsWith("Top")) {
					this.workAreaY = 0;
				} else if (_layoutAlign.startsWith("Bottom")) {
					this.workAreaY = super.getRealHeight() - this.getHeight() * _zoom;
				} else {
					// 默认Center
					this.workAreaY = (super.getRealHeight() - this.getHeight() * _zoom) / 2;
				}
			} else {
				this.workAreaY = 0;
			}
			this.setInnerXZoom(_zoom);
			this.setInnerYZoom(_zoom);

		} else {
			this.workAreaX = 0;
			this.workAreaY = 0;
			this.workAreaWidth = super.getRealWidth();
			this.workAreaHeight = super.getRealHeight();
		}
	}

	@Override
	public void addSubview(DoUIModule _insertViewModel) {
		ViewGroup _view = (ViewGroup) this.getCurrentUIModuleView();
		this.getChildUIModules().add(_insertViewModel);
		View _insertView = (View) _insertViewModel.getCurrentUIModuleView();
		_insertViewModel.setLayoutParamsType(LayoutParamsType.Alayout.toString());
		_view.addView(_insertView, DoUIModuleHelper.getLayoutParams(_insertViewModel));
	}

}
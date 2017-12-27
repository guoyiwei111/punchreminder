
package com.example.guoyiwei.dk;

import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.fence.GeoFenceClient;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.DPoint;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMap.OnMapClickListener;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.PolygonOptions;
import com.example.guoyiwei.dk.services.PreferencesService;
import com.example.guoyiwei.dk.util.Const;
import com.example.guoyiwei.dk.util.JSONUtil;

import org.codehaus.jackson.type.TypeReference;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 多边形地理围栏
 *
 * @author hongming.wang
 * @since 3.2.0
 */
public class PolygonSelectActivity extends CheckPermissionsActivity
		implements
        OnClickListener,
			OnMapClickListener,
			LocationSource,
        AMapLocationListener,
        OnCheckedChangeListener {

	private TextView tvGuide;
	private EditText etCustomId;
	private CheckBox cbAlertIn;
	private CheckBox cbAlertOut;
	private CheckBox cbAldertStated;
	private Button btAddFence;
    private Button btResetFence;

	/**
	 * 用于显示当前的位置
	 * <p>
	 * 示例中是为了显示当前的位置，在实际使用中，单独的地理围栏可以不使用定位接口
	 * </p>
	 */
	private AMapLocationClient mlocationClient;
	private OnLocationChangedListener mListener;
	private AMapLocationClientOption mLocationOption;

	private MapView mMapView;
	private AMap mAMap;
	// 多边形围栏的边界点
	private List<LatLng> polygonPoints = new ArrayList<LatLng>();

	private List<Marker> markerList = new ArrayList<Marker>();

	// 当前的坐标点集合，主要用于进行地图的可视区域的缩放
	private LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();

	private BitmapDescriptor bitmap = null;
	private MarkerOptions markerOption = null;



	// 触发地理围栏的行为，默认为进入提醒
	private int activatesAction = GeoFenceClient.GEOFENCE_IN;
	// 地理围栏的广播action
	private static final String GEOFENCE_BROADCAST_ACTION = "com.example.geofence.polygon";

	// 记录已经添加成功的围栏

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		String mmm = sHA1(this);
		System.out.println("---------------------"+mmm);

		setContentView(R.layout.activity_geofence_new);
		setTitle("设置地理围栏");


		btAddFence = (Button) findViewById(R.id.bt_addFence);
        btResetFence = (Button) findViewById(R.id.bt_resetFence);
		tvGuide = (TextView) findViewById(R.id.tv_guide);
		etCustomId = (EditText) findViewById(R.id.et_customId);

		cbAlertIn = (CheckBox) findViewById(R.id.cb_alertIn);
		cbAlertOut = (CheckBox) findViewById(R.id.cb_alertOut);
		cbAldertStated = (CheckBox) findViewById(R.id.cb_alertStated);

		mMapView = (MapView) findViewById(R.id.map);
		mMapView.onCreate(savedInstanceState);
		bitmap = BitmapDescriptorFactory
				.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW);
		markerOption = new MarkerOptions().icon(bitmap).draggable(true);
		init();
	}

	void init() {
		if (mAMap == null) {
			mAMap = mMapView.getMap();
			mAMap.getUiSettings().setRotateGesturesEnabled(false);
			mAMap.moveCamera(CameraUpdateFactory.zoomBy(6));
			setUpMap();
		}

		resetView_polygon();

		btAddFence.setOnClickListener(this);
        btResetFence.setOnClickListener(this);
		cbAlertIn.setOnCheckedChangeListener(this);
		cbAlertOut.setOnCheckedChangeListener(this);
		cbAldertStated.setOnCheckedChangeListener(this);

		IntentFilter filter = new IntentFilter(
				ConnectivityManager.CONNECTIVITY_ACTION);
		filter.addAction(GEOFENCE_BROADCAST_ACTION);



        try {
            PreferencesService service = new PreferencesService(this);
            String res =service.getValue("fence");
            allFence = (List<List<DPoint>>) JSONUtil.toObject(res,new   TypeReference<List<List<DPoint>>>() {});
            for(int i=0;i<allFence.size();i++){
                drawFence(allFence.get(i));

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
	}

	/**
	 * 设置一些amap的属性
	 */
	private void setUpMap() {
		mAMap.setOnMapClickListener(this);
		mAMap.setLocationSource(this);// 设置定位监听
		mAMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
		MyLocationStyle myLocationStyle = new MyLocationStyle();
		// 自定义定位蓝点图标
		myLocationStyle.myLocationIcon(
				BitmapDescriptorFactory.fromResource(R.drawable.gps_point));
		// 自定义精度范围的圆形边框颜色
		myLocationStyle.strokeColor(Color.argb(0, 0, 0, 0));
		// 自定义精度范围的圆形边框宽度
		myLocationStyle.strokeWidth(0);
		// 设置圆形的填充颜色
		myLocationStyle.radiusFillColor(Color.argb(0, 0, 0, 0));
		// 将自定义的 myLocationStyle 对象添加到地图上
		mAMap.setMyLocationStyle(myLocationStyle);
		mAMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
		// 设置定位的类型为定位模式 ，可以由定位、跟随或地图根据面向方向旋转几种

		 mAMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onResume() {
		super.onResume();
		mMapView.onResume();
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onPause() {
		super.onPause();
		mMapView.onPause();
		deactivate();
        /*
        List<List<DPoint>> mm =  allFence;
        JSONArray array = new JSONArray();

        for(int i=0;i<mm.size();i++){

            List<DPoint> kk = mm.get(i);
            JSONArray tmparray = new JSONArray();
            for(int j=0;j<kk.size();j++){
                JSONObject point = new JSONObject();
                try {
                    point.put("latitude",kk.get(j).getLatitude());
                    point.put("longitude",kk.get(j).getLongitude());
                    tmparray.put(point);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
            array.put(tmparray);

        }*/
        try {
            String res  = JSONUtil.getJsonString(allFence);
            PreferencesService service = new PreferencesService(this);
            service.setValue("fence",res);
        } catch (Exception e) {
            e.printStackTrace();
        }



	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mMapView.onSaveInstanceState(outState);
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mMapView.onDestroy();



		if (null != mlocationClient) {
			mlocationClient.onDestroy();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.bt_addFence :
				addFence();
				break;
            case R.id.bt_resetFence :
                resetFence();
                break;
			default :
				break;
		}
	}

	private void drawFence(List<DPoint> fence) {

        drawPolygon(fence);


		// 设置所有maker显示在当前可视区域地图中
		LatLngBounds bounds = boundsBuilder.build();
		mAMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 150));
		polygonPoints.clear();
		removeMarkers();
	}



	private void drawPolygon(List<DPoint> fence) {

		if (null == fence || fence.isEmpty()) {
			return;
		}

			List<LatLng> lst = new ArrayList<LatLng>();

			PolygonOptions polygonOption = new PolygonOptions();
			for (DPoint point : fence) {
				lst.add(new LatLng(point.getLatitude(), point.getLongitude()));
				boundsBuilder.include(
						new LatLng(point.getLatitude(), point.getLongitude()));
			}
			polygonOption.addAll(lst);

			polygonOption.strokeColor(Const.STROKE_COLOR)
					.fillColor(Const.FILL_COLOR).strokeWidth(Const.STROKE_WIDTH);

			mAMap.addPolygon(polygonOption);

	}









	@Override
	public void onMapClick(LatLng latLng) {
		if (null == polygonPoints) {
			polygonPoints = new ArrayList<LatLng>();
		}
		polygonPoints.add(latLng);
		addPolygonMarker(latLng);
		tvGuide.setBackgroundColor(getResources().getColor(R.color.gary));
		tvGuide.setText("已选择" + polygonPoints.size() + "个点");
		if (polygonPoints.size() >= 3) {
			btAddFence.setEnabled(true);
		}
	}

	/**
	 * 定位成功后回调函数
	 */
	@Override
	public void onLocationChanged(AMapLocation amapLocation) {
		if (mListener != null && amapLocation != null) {
			if (amapLocation != null && amapLocation.getErrorCode() == 0) {
				mListener.onLocationChanged(amapLocation);// 显示系统小蓝点
			} else {
				String errText = "定位失败," + amapLocation.getErrorCode() + ": "
						+ amapLocation.getErrorInfo();
				Log.e("AmapErr", errText);
			}
		}
	}

	/**
	 * 激活定位
	 */
	@Override
	public void activate(OnLocationChangedListener listener) {
		mListener = listener;
		if (mlocationClient == null) {
			mlocationClient = new AMapLocationClient(this);
			mLocationOption = new AMapLocationClientOption();
			// 设置定位监听
			mlocationClient.setLocationListener(this);
			// 设置为高精度定位模式
			mLocationOption.setLocationMode(AMapLocationMode.Hight_Accuracy);
			// 只是为了获取当前位置，所以设置为单次定位
			mLocationOption.setOnceLocation(true);
			// 设置定位参数
			mlocationClient.setLocationOption(mLocationOption);
			mlocationClient.startLocation();
		}
	}

	/**
	 * 停止定位
	 */
	@Override
	public void deactivate() {
		mListener = null;
		if (mlocationClient != null) {
			mlocationClient.stopLocation();
			mlocationClient.onDestroy();
		}
		mlocationClient = null;
	}

	// 添加多边形的边界点marker
	private void addPolygonMarker(LatLng latlng) {
		markerOption.position(latlng);
		Marker marker = mAMap.addMarker(markerOption);
		markerList.add(marker);
	}

	private void removeMarkers() {
		if (null != markerList && markerList.size() > 0) {
			for (Marker marker : markerList) {
				marker.remove();
			}
			markerList.clear();
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		switch (buttonView.getId()) {
			case R.id.cb_alertIn :
				if (isChecked) {
					activatesAction |= GeoFenceClient.GEOFENCE_IN;
				} else {
					activatesAction = activatesAction
							& (GeoFenceClient.GEOFENCE_OUT
									| GeoFenceClient.GEOFENCE_STAYED);
				}
				break;
			case R.id.cb_alertOut :
				if (isChecked) {
					activatesAction |= GeoFenceClient.GEOFENCE_OUT;
				} else {
					activatesAction = activatesAction
							& (GeoFenceClient.GEOFENCE_IN
									| GeoFenceClient.GEOFENCE_STAYED);
				}
				break;
			case R.id.cb_alertStated :
				if (isChecked) {
					activatesAction |= GeoFenceClient.GEOFENCE_STAYED;
				} else {
					activatesAction = activatesAction
							& (GeoFenceClient.GEOFENCE_IN
									| GeoFenceClient.GEOFENCE_OUT);
				}
				break;
			default :
				break;
		}

	}

	private void resetView_polygon() {
		tvGuide.setBackgroundColor(getResources().getColor(R.color.red));
		tvGuide.setText("请点击地图选择围栏的边界点,至少3个点");
		tvGuide.setVisibility(View.VISIBLE);
		tvGuide.setVisibility(View.VISIBLE);
		polygonPoints = new ArrayList<LatLng>();
		btAddFence.setEnabled(false);
	}

	/**
	 * 添加围栏
	 *
	 * @since 3.2.0
	 * @author hongming.wang
	 *
	 */
	private void addFence() {
		addPolygonFence();
	}

    private void resetFence() {
        mAMap.clear();
        allFence.clear();
        polygonPoints = null;
    }

	/**
	 * 添加多边形围栏
	 *
	 * @since 3.2.0
	 * @author hongming.wang
	 *
	 */
	private void addPolygonFence() {
		String customId = etCustomId.getText().toString();
		if (null == polygonPoints || polygonPoints.size() < 3) {
			Toast.makeText(getApplicationContext(), "参数不全", Toast.LENGTH_SHORT)
					.show();
			btAddFence.setEnabled(true);
			return;
		}
		List<DPoint> pointList = new ArrayList<DPoint>();
		for (LatLng latLng : polygonPoints) {
			pointList.add(new DPoint(latLng.latitude, latLng.longitude));
		}
		//fenceClient.addGeoFence(pointList, customId);
        drawFence(pointList);
        allFence.add(pointList);

	}
	List<List<DPoint>> allFence = new ArrayList<List<DPoint>>();

	public static String sHA1(Context context) {
		try {
			PackageInfo info = context.getPackageManager().getPackageInfo(
					context.getPackageName(), PackageManager.GET_SIGNATURES);
			byte[] cert = info.signatures[0].toByteArray();
			MessageDigest md = MessageDigest.getInstance("SHA1");
			byte[] publicKey = md.digest(cert);
			StringBuffer hexString = new StringBuffer();
			for (int i = 0; i < publicKey.length; i++) {
				String appendString = Integer.toHexString(0xFF & publicKey[i])
						.toUpperCase(Locale.US);
				if (appendString.length() == 1)
					hexString.append("0");
				hexString.append(appendString);
				hexString.append(":");
			}
			String result = hexString.toString();
			return result.substring(0, result.length()-1);
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}

}

package com.chenming.lbsplay;

import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;

public class MainActivity extends AppCompatActivity {

	public LocationClient mLocationClient ;
	private TextView positionText ;
	private MapView mapView ;
	private BaiduMap baiduMap ;
	private boolean isFirstLocate = true ;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		positionText = (TextView)findViewById(R.id.positionText) ;
		mapView = (MapView)findViewById(R.id.baidumap) ;

		mLocationClient = new LocationClient(getApplicationContext()) ;
		mLocationClient.registerLocationListener( new MyLocationListener() );
		initLocation() ;
		mLocationClient.start();

		baiduMap = mapView.getMap() ;
		baiduMap.setMyLocationEnabled(true); //开启定位图层
		/*//地图操作部分
		//设置地图级别
		MapStatusUpdate update = MapStatusUpdateFactory.zoomTo(19) ;
		baiduMap.animateMapStatus(update);
		//移动地图到某个位置
		LatLng latLng = new LatLng(24,130) ;
		update = MapStatusUpdateFactory.newLatLng(latLng) ;
		baiduMap.animateMapStatus(update);*/

	}

	public void initLocation(){
		LocationClientOption option = new LocationClientOption() ;
		option.setScanSpan(5000);
		option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
		option.setIsNeedAddress(true);
		mLocationClient.setLocOption(option);
	}
	public class MyLocationListener implements BDLocationListener{
		@Override
		public void onReceiveLocation(BDLocation bdLocation) {
			StringBuilder currentPosition = new StringBuilder() ;
			//经纬度
			currentPosition.append("维度:").append(bdLocation.getLatitude()).append("\n") ;
			currentPosition.append("经度:").append(bdLocation.getLongitude()).append("\n") ;
			//定位方式
			currentPosition.append("定位方式:") ;
			if (bdLocation.getLocType() == BDLocation.TypeGpsLocation) {
				currentPosition.append("GPS").append("\n") ;;
			}else if(bdLocation.getLocType() == BDLocation.TypeNetWorkLocation){
				currentPosition.append("网络").append("\n") ; ;
			}
			//详细位置信息
			currentPosition.append("国家：").append(bdLocation.getCountry()).append("\n") ;
			currentPosition.append("省：").append(bdLocation.getProvince()).append("\n") ;
			currentPosition.append("市：").append(bdLocation.getCity()).append("\n") ;
			currentPosition.append("区：").append(bdLocation.getDistrict()).append("\n") ;

			positionText.setText(currentPosition) ;

			navigateTo(bdLocation);
		}
	}
	public void navigateTo(BDLocation bdLocation){
		if(isFirstLocate) {
			LatLng latLng = new LatLng(bdLocation.getLatitude(),bdLocation.getLongitude()) ;
			MapStatus mapStatus = new MapStatus.Builder()
					.zoom(16f)
					.target(latLng)
					.build() ;
			MapStatusUpdate update = MapStatusUpdateFactory.newMapStatus(mapStatus) ;
			baiduMap.animateMapStatus(update);
			isFirstLocate = false ;
		}


		MyLocationData.Builder builder = new MyLocationData.Builder() ;
		builder.latitude(bdLocation.getLatitude()) ;
		builder.longitude(bdLocation.getLongitude()) ;
		MyLocationData locationData = builder.build() ;
		baiduMap.setMyLocationData( locationData );
	}
	@Override
	protected void onResume(){
		super.onResume();
		mapView.onResume();
	}
	@Override
	protected void onPause(){
		super.onPause();
		mapView.onPause();
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mLocationClient.stop();
		mapView.onDestroy();
		baiduMap.setMyLocationEnabled(false);
	}
}

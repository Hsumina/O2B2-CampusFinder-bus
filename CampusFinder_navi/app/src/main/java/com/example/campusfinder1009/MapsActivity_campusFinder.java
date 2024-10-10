package com.example.campusfinder1009;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.android.PolyUtil;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.TravelMode;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MapsActivity_campusFinder extends FragmentActivity implements OnMapReadyCallback {

    // 상수
    private static final String TAG = "MapsActivity_campusFinder";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final LatLng CENTRAL_LIBRARY = new LatLng(35.9702279, 126.9554595);
    private static final LatLng ENGINEERING_BUILDING = new LatLng(35.9676680, 126.95811456);
    private static final LatLng DEFAULT_LOCATION = ENGINEERING_BUILDING;
    private static final float DEFAULT_ZOOM = 17f;
    private static final String API_KEY = "API_KEY";

    // UI 요소
    private TextView distanceTextView;

    // 지도 관련 객체
    private GoogleMap mMap;
    private Marker centralLibraryMarker;
    private Marker engineeringBuildingMarker;

    // 위치 정보
    private FusedLocationProviderClient fusedLocationClient;
    private LatLng currentLocation;
    private LatLng startLocation;
    private LatLng endLocation;

    // API 및 네트워크 관련
    private GeoApiContext geoApiContext;
    private boolean isNetworkAvailable = false;

    // 유틸리티
    private ExecutorService executorService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_campus_finder);

        executorService = Executors.newSingleThreadExecutor();

        // 네트워크 연결 상태 확인
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        isNetworkAvailable = activeNetworkInfo != null && activeNetworkInfo.isConnected();

        if (!isNetworkAvailable) {
            Toast.makeText(this, "인터넷 연결을 확인해주세요.", Toast.LENGTH_LONG).show();
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        distanceTextView = findViewById(R.id.distance_text);

        // 출발지 선택을 위한 스피너 설정
        Spinner startSpinner = findViewById(R.id.start_spinner);
        // 목적지 선택을 위한 스피너 설정
        Spinner endSpinner = findViewById(R.id.end_spinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.locations_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        startSpinner.setAdapter(adapter);
        endSpinner.setAdapter(adapter);

        startSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0: // 현재 위치
                        startLocation = currentLocation;
                        break;
                    case 1: // 중앙도서관
                        startLocation = CENTRAL_LIBRARY;
                        break;
                    case 2: // 공과대학
                        startLocation = ENGINEERING_BUILDING;
                        break;
                }
                if (startLocation != null) {
                    moveCamera(startLocation);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        endSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0: // 현재 위치
                        endLocation = currentLocation;
                        break;
                    case 1: // 중앙도서관
                        endLocation = CENTRAL_LIBRARY;
                        break;
                    case 2: // 공과대학
                        endLocation = ENGINEERING_BUILDING;
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        Button findRouteButton = findViewById(R.id.find_route_button);
        findRouteButton.setOnClickListener(v -> {
            if (startLocation != null && endLocation != null) {
                drawRoute(startLocation, endLocation);
            } else {
                Toast.makeText(this, "출발지와 목적지를 선택해주세요.", Toast.LENGTH_SHORT).show();
            }
        });

        // Google Directions API를 사용하기 위한 context 초기화
        geoApiContext = new GeoApiContext.Builder()
                .apiKey(API_KEY)
                .build();

        Log.d(TAG, "API Key: " + API_KEY);
        Log.d(TAG, "GeoApiContext initialized: " + (geoApiContext != null));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setZoomControlsEnabled(true);

        // 기본 위치(공과대학)로 카메라 이동
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, DEFAULT_ZOOM));

        mMap.setOnMapLoadedCallback(() -> {
            Log.d(TAG, "Map loaded callback");
            addMarkersToMap();
            setupMapListeners();
            enableMyLocation();

            // 지도가 로드된 후 기본 위치로 다시 이동
            moveCamera(DEFAULT_LOCATION);
        });

        mMap.setOnCameraIdleListener(() -> {
            LatLng center = mMap.getCameraPosition().target;
            Log.d(TAG, "Camera center: " + center.latitude + ", " + center.longitude);
        });
    }

    private void setupMapListeners() {
        mMap.setOnMarkerClickListener(marker -> {
            focusOn(marker.getPosition());
            return false;
        });

        mMap.setOnMapClickListener(latLng -> {
            currentLocation = latLng;
            mMap.clear();
            addMarkersToMap();
            mMap.addMarker(new MarkerOptions().position(currentLocation).title("선택한 위치"));
        });
    }

    private boolean isMapVisible() {
        return mMap != null && mMap.getMapType() != GoogleMap.MAP_TYPE_NONE;
    }

    private void moveCamera(LatLng location) {
        if (mMap != null) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, DEFAULT_ZOOM));
        }
    }

    private void focusOn(LatLng location) {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 17));
    }

    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
                if (location != null) {
                    currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(currentLocation).title("내 위치"));
                    // 현재 위치로 이동하지 않고, 기본 위치(공과대학)를 유지합니다.
                }
            });
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private void addMarkersToMap() {
        if (mMap != null) {
            mMap.clear();
            centralLibraryMarker = mMap.addMarker(new MarkerOptions()
                    .position(CENTRAL_LIBRARY)
                    .title("중앙도서관"));
            engineeringBuildingMarker = mMap.addMarker(new MarkerOptions()
                    .position(ENGINEERING_BUILDING)
                    .title("공과대학"));
            if (currentLocation != null) {
                mMap.addMarker(new MarkerOptions().position(currentLocation).title("현재 위치"));
            }
        }
    }


    private boolean isValidLatLng(LatLng latLng) {
        return latLng != null &&
                latLng.latitude >= -90 && latLng.latitude <= 90 &&
                latLng.longitude >= -180 && latLng.longitude <= 180;
    }

    private void drawRoute(LatLng origin, LatLng destination) {
        if (!isNetworkAvailable) {
            Toast.makeText(this, "인터넷 연결을 확인해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isValidLatLng(origin) || !isValidLatLng(destination)) {
            Toast.makeText(this, "유효하지 않은 좌표입니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d(TAG, "Requesting directions from " + origin + " to " + destination);

        try {
            DirectionsApiRequest request = DirectionsApi.newRequest(geoApiContext)
                    .mode(TravelMode.WALKING)
                    .origin(new com.google.maps.model.LatLng(origin.latitude, origin.longitude))
                    .destination(new com.google.maps.model.LatLng(destination.latitude, destination.longitude));

            request.setCallback(new PendingResult.Callback<DirectionsResult>() {
                @Override
                public void onResult(DirectionsResult result) {
                    runOnUiThread(() -> {
                        try {
                            if (result.routes != null && result.routes.length > 0) {
                                displayRoute(result, origin, destination);
                            } else {
                                Toast.makeText(MapsActivity_campusFinder.this, "경로를 찾을 수 없습니다. 위치를 확인해주세요.", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Error processing directions result", e);
                            Toast.makeText(MapsActivity_campusFinder.this, "경로 처리 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onFailure(Throwable e) {
                    Log.e(TAG, "Direction request failed", e);
                    runOnUiThread(() -> {
                        String errorMessage = "API 오류: " + e.getMessage();
                        Toast.makeText(MapsActivity_campusFinder.this, errorMessage, Toast.LENGTH_SHORT).show();
                    });
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error setting up directions request", e);
            Toast.makeText(this, "경로 요청 설정 중 오류가 발생했습니다: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void displayRoute(DirectionsResult result, LatLng origin, LatLng destination) {
        mMap.clear();
        addMarkersToMap();

        List<LatLng> decodedPath = PolyUtil.decode(result.routes[0].overviewPolyline.getEncodedPath());
        mMap.addPolyline(new PolylineOptions().addAll(decodedPath).color(Color.RED));
        mMap.addMarker(new MarkerOptions().position(origin).title("출발"));
        mMap.addMarker(new MarkerOptions().position(destination).title("도착"));

        LatLngBounds bounds = calculateBounds(decodedPath);
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));

        updateDistanceText(result);
    }

    private LatLngBounds calculateBounds(List<LatLng> path) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (LatLng point : path) {
            builder.include(point);
        }
        return builder.build();
    }

    private void updateDistanceText(DirectionsResult result) {
        float distance = 0;
        for (int i = 0; i < result.routes[0].legs.length; i++) {
            distance += result.routes[0].legs[i].distance.inMeters;
        }
        final String distanceText = String.format("거리: %.2f km", distance / 1000);
        distanceTextView.setText(distanceText);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (geoApiContext != null) {
            geoApiContext.shutdown();
        }
        executorService.shutdown();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableMyLocation();
            } else {
                Toast.makeText(this, "위치 권한이 거부되었습니다", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
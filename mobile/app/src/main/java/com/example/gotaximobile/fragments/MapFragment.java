package com.example.gotaximobile.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.gotaximobile.models.MapPin;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MapFragment extends Fragment {

    private MapView map;

    private final List<MapPin> pins = new ArrayList<>();
    private Polyline routeOverlay;
    private final OkHttpClient httpClient = new OkHttpClient();
    private RouteInfoListener routeInfoListener;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Configuration.getInstance().setUserAgentValue(requireContext().getPackageName());

        map = new MapView(getContext());
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setBuiltInZoomControls(false);
        map.setMultiTouchControls(true);

        IMapController mapController = map.getController();
        mapController.setZoom(15);

        mapController.setCenter(new GeoPoint(45.2571, 19.8335)); // Novi Sad

        if (!pins.isEmpty()) renderPins();
        return map;
    }

    public void setPins(List<MapPin> newPins) {
        pins.clear();
        for (MapPin pin : newPins) {
            addPin(pin);
        }
        map.post(this::renderPins);
    }

    public void addPin(MapPin pin) {
        if (pin.snapToRoad) {
            snapToRoad(pin, new Callback() {
                @Override
                public void onSuccess(GeoPoint point) {
                    pin.lat = point.getLatitude();
                    pin.lng = point.getLongitude();
                    pins.add(pin);
                    renderSinglePin(pin);
                }

                @Override
                public void onError(Exception e) {
                    pins.add(pin);
                    renderSinglePin(pin);
                }
            });
        } else {
            pins.add(pin);
            renderSinglePin(pin);
        }

    }

    private void renderPins() {
        if (map == null) return;
        map.getOverlays().clear();
        for (MapPin pin : pins) {
            renderSinglePin(pin);
        }
    }

    private void renderSinglePin(MapPin pin) {
        if (map == null) return;
        Marker marker = new Marker(map);
        marker.setPosition(pin.toGeoPoint());
        if (pin.popup != null && !pin.popup.isEmpty()) marker.setTitle(pin.popup);
        if (pin.iconResId != 0)
            marker.setIcon(ContextCompat.getDrawable(requireContext(), pin.iconResId));
        map.getOverlays().add(marker);
        map.invalidate();
    }

    public void snapToRoad(MapPin pin, Callback callback) {
        if (!pin.snapToRoad) {
            callback.onSuccess(pin.toGeoPoint());
            return;
        }

        String url = "https://router.project-osrm.org/nearest/v1/driving/"
                + pin.lng + "," + pin.lat + "?number=1";

        Request request = new Request.Builder().url(url).build();
        httpClient.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onError(e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    callback.onError(new IOException("Unexpected code " + response));
                    return;
                }

                String body = response.body().string();
                try {
                    JSONObject json = new JSONObject(body);
                    JSONArray waypoints = json.getJSONArray("waypoints");
                    if (waypoints.length() > 0) {
                        JSONArray location = waypoints.getJSONObject(0).getJSONArray("location");
                        double snappedLng = location.getDouble(0);
                        double snappedLat = location.getDouble(1);
                        GeoPoint snappedPoint = new GeoPoint(snappedLat, snappedLng);
                        callback.onSuccess(snappedPoint);
                    } else {
                        callback.onError(new Exception("No road found nearby"));
                    }
                } catch (JSONException e) {
                    callback.onError(e);
                }
            }
        });
    }

    public void drawRoute(GeoPoint start, GeoPoint end, List<GeoPoint> stops) {
        if (stops == null) {
            stops = new ArrayList<>();
        }
        RoadManager roadManager = new OSRMRoadManager(requireContext(),
                Configuration.getInstance().getUserAgentValue());

        ArrayList<GeoPoint> waypoints = new ArrayList<>();
        waypoints.add(start);
        waypoints.addAll(stops);
        waypoints.add(end);

        new Thread(() -> {
            Road road = roadManager.getRoad(waypoints);
            double durationSeconds = road.mDuration;
            double distanceKm = road.mLength;
            requireActivity().runOnUiThread(() -> {
                if (routeInfoListener != null) {
                    routeInfoListener.onRouteInfo(durationSeconds, distanceKm);
                }
            });

            Polyline polyline = RoadManager.buildRoadOverlay(road);

            requireActivity().runOnUiThread(() -> {
                if (routeOverlay != null) {
                    map.getOverlays().remove(routeOverlay);
                }
                routeOverlay = polyline;
                map.getOverlays().add(routeOverlay);
                map.invalidate();
            });
        }).start();
    }

    public void setRouteInfoListener(RouteInfoListener listener) {
        this.routeInfoListener = listener;
    }


    public interface Callback {
        void onSuccess(GeoPoint point);

        void onError(Exception e);
    }

    public interface RouteInfoListener {
        void onRouteInfo(double durationSeconds, double distanceKm);
    }

    @Override
    public void onResume() {
        super.onResume();
        map.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        map.onPause();
    }

    public void drawRouteFromPoints(List<GeoPoint> points) {
        if (map == null || points == null || points.size() < 2) return;

        requireActivity().runOnUiThread(() -> {
            if (routeOverlay != null) {
                map.getOverlays().remove(routeOverlay);
            }

            Polyline polyline = new Polyline();
            polyline.setPoints(points);

            routeOverlay = polyline;
            map.getOverlays().add(routeOverlay);
            map.invalidate();

            map.getController().setZoom(14);
            map.getController().setCenter(points.get(points.size()/2));
        });
    }




}

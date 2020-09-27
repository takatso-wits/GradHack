package co.micronano.gradhack;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.mapboxsdk.utils.BitmapUtils;

import java.util.ArrayList;
import java.util.List;

import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconOffset;

public class LocateGP extends AppCompatActivity implements
        OnMapReadyCallback, MapView.OnDidFinishRenderingMapListener {


    private static final String ICON_ID = "red-pin-icon-id";
    private static final float DEFAULT_DESIRED_ICON_OFFSET = -16;
    private static final float STARTING_DROP_HEIGHT = -100;
    private static final long DROP_SPEED_MILLISECONDS = 1200;
    private static final String SYMBOL_LAYER_ID = "symbol-layer-id";
    private MapView mapView;
    private SymbolLayer pinSymbolLayer;
    private Style style;
    private TimeInterpolator currentSelectedTimeInterpolator = new BounceInterpolator();
    private ValueAnimator animator;
    private boolean firstRunThrough = true;
    private boolean animationHasStarted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));
        setContentView(R.layout.activity_locate_g_p);
        getSupportActionBar().hide();

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
    }


    @Override
    public void onDidFinishRenderingMap(boolean fully) {
        initAnimation(currentSelectedTimeInterpolator);
        initInterpolatorButtons();

    }

    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {
        mapboxMap.setStyle(new Style.Builder().fromUri(Style.LIGHT)
// Add GeoJsonSource with random Features to the map.
                        .withSource(new GeoJsonSource("source-id",
                                FeatureCollection.fromFeatures(new Feature[] {
                                        Feature.fromGeometry(Point.fromLngLat(
                                                119.86083984375,
                                                -1.834403324493515)),
                                        Feature.fromGeometry(Point.fromLngLat(
                                                116.06637239456177,
                                                5.970619502704659)),
                                        Feature.fromGeometry(Point.fromLngLat(
                                                114.58740234375,
                                                4.54357027937176)),
                                        Feature.fromGeometry(Point.fromLngLat(
                                                118.19091796875,
                                                5.134714634014467)),
                                        Feature.fromGeometry(Point.fromLngLat(
                                                110.36865234374999,
                                                1.4500404973608074)),
                                        Feature.fromGeometry(Point.fromLngLat(
                                                109.40185546874999,
                                                0.3076157096439005)),
                                        Feature.fromGeometry(Point.fromLngLat(
                                                115.79589843749999,
                                                1.5159363834516861)),
                                        Feature.fromGeometry(Point.fromLngLat(
                                                113.291015625,
                                                -0.9667509997666298)),
                                        Feature.fromGeometry(Point.fromLngLat(
                                                116.40083312988281,
                                                -0.3392008994314591))
                                })
                        ))
                        .withImage(ICON_ID, BitmapUtils.getBitmapFromDrawable(
                                getResources().getDrawable(R.drawable.mapbox_marker_icon_default))), new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {
                        LocateGP.this.style = style;
                        mapView.addOnDidFinishRenderingMapListener(LocateGP.this);
                    }
                }
        );

    }
    private void initAnimation(TimeInterpolator desiredTimeInterpolator) {
        if (animator != null) {
            animator.cancel();
        }
        animator = ValueAnimator.ofFloat(STARTING_DROP_HEIGHT, -17);
        animator.setDuration(DROP_SPEED_MILLISECONDS);
        animator.setInterpolator(desiredTimeInterpolator);
        animator.setStartDelay(1000);
        animator.start();
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                if (!animationHasStarted) {
                    initSymbolLayer();
                    animationHasStarted = true;
                }
                pinSymbolLayer.setProperties(iconOffset(new Float[] {0f, (Float) valueAnimator.getAnimatedValue()}));
            }
        });
    }

    private void initSymbolLayer() {
        pinSymbolLayer = new SymbolLayer(SYMBOL_LAYER_ID,
                "source-id");
        pinSymbolLayer.setProperties(
                iconImage(ICON_ID),
                iconIgnorePlacement(true),
                iconAllowOverlap(true),
                iconOffset(new Float[] {0f, DEFAULT_DESIRED_ICON_OFFSET}));
        style.addLayer(pinSymbolLayer);
    }

    private void initInterpolatorButtons() {

        FloatingActionButton bounceInterpolatorFab = findViewById(R.id.fab_bounce_interpolator);
        bounceInterpolatorFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentSelectedTimeInterpolator = new BounceInterpolator();
                resetIcons();
            }
        });

        FloatingActionButton linearInterpolatorFab = findViewById(R.id.fab_linear_interpolator);
        linearInterpolatorFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentSelectedTimeInterpolator = new LinearInterpolator();
                firstRunThrough = false;
                resetIcons();
            }
        });

        FloatingActionButton accelerateInterpolatorFab = findViewById(R.id.fab_accelerate_interpolator);
        accelerateInterpolatorFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentSelectedTimeInterpolator = new AccelerateInterpolator();
                firstRunThrough = false;
                resetIcons();
            }
        });

        FloatingActionButton decelerateInterpolatorFab = findViewById(R.id.fab_decelerate_interpolator);
        decelerateInterpolatorFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentSelectedTimeInterpolator = new DecelerateInterpolator();
                firstRunThrough = false;
                resetIcons();
            }
        });
    }

    private void resetIcons() {
        if (!firstRunThrough) {
            animationHasStarted = false;
            style.removeLayer(SYMBOL_LAYER_ID);
            initAnimation(currentSelectedTimeInterpolator);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (animator != null) {
            animator.end();
        }
        mapView.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }


}
package edu.caltech.cs3.platypi;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

/**
 * Vanilla class for holding the list of overlays to draw to the map.
 */
public class SignalItemizedOverlay extends ItemizedOverlay<OverlayItem> {
    private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
    private Context mContext;

    public SignalItemizedOverlay(final Drawable defaultMarker) {
        super(defaultMarker);
    }

    public SignalItemizedOverlay(final Drawable defaultMarker, final Context context) {
        super(defaultMarker);
        mContext = context;
    }

    public SignalItemizedOverlay(final Context context) {
        super(new ShapeDrawable(new OvalShape()));
        mContext = context;
    }

    public void addOverlay(OverlayItem overlay) {
        mOverlays.add(overlay);
    }

    @Override
    public void draw(Canvas canvas, MapView mapView, boolean shadow) {
        
        // rescale the datapoint markers
        for (OverlayItem overlayItem: mOverlays) {
            Drawable marker = overlayItem.getMarker(0);
            double latitude = overlayItem.getPoint().getLatitudeE6() / 1e6;
            int width_px = degreesToPixels(0.001, mapView, latitude);
            int height_px = degreesToPixels(0.001, mapView, 0);
            marker.setBounds(0, 0, width_px, height_px);
            overlayItem.setMarker(marker);
        }
        
        // I don't know where else to specify that I don't want shadows
        super.draw(canvas, mapView, false);
    }
    
    public static int degreesToPixels(double degrees, MapView map, double latitude) {
        return (int) (map.getProjection().metersToEquatorPixels((float) (degrees*111319.9)) * Math.cos(Math.toRadians(latitude)));         
    }

    public void populateOverlay() {
        populate();
    }
    
    @Override
    protected OverlayItem createItem(int i) {
        return mOverlays.get(i);
    }

    @Override
    public int size() {
        return mOverlays.size();
    }

    @Override
    protected boolean onTap(int index) {
      OverlayItem item = mOverlays.get(index);
      AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
      dialog.setTitle(item.getTitle());
      dialog.setMessage(item.getSnippet());
      dialog.show();

      return true;
    }

}
package cs3.platypi.client;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.maps.client.InfoWindowContent;
import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.Maps;
import com.google.gwt.maps.client.control.LargeMapControl;
import com.google.gwt.maps.client.event.PolylineMouseOutHandler;
import com.google.gwt.maps.client.event.PolylineMouseOutHandler.PolylineMouseOutEvent;
import com.google.gwt.maps.client.event.PolylineMouseOverHandler;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.maps.client.geom.Size;
import com.google.gwt.maps.client.overlay.Icon;
import com.google.gwt.maps.client.overlay.Marker;
import com.google.gwt.maps.client.overlay.MarkerOptions;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RichTextArea;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.maps.client.overlay.Polyline;
import cs3.platypi.shared.SignalMetadata;
import com.google.gwt.maps.client.InfoWindow;
import com.google.gwt.user.client.Window;

public class PhoneSignalEntryPoint implements EntryPoint, ValueChangeHandler<String> {
  // when this boolean is true, we run the maps api without the key.
  // this is for local testing so the maps key error stops popping up.
  // MAKE SURE THIS IS FALSE BEFORE PUSHING
  boolean localRun = false;
  final LatLng CALTECH = LatLng.newInstance(34.139, -118.124);
  final LatLng CALTECHLB = LatLng.newInstance(34.135909,-118.127854);
  final LatLng CALTECHUR = LatLng.newInstance(34.141841,-118.121288);
  
  final RichTextArea LBlat = new RichTextArea(); //left bottom
  final RichTextArea LBlong = new RichTextArea();
  final RichTextArea URlat = new RichTextArea(); // upper right
  final RichTextArea URlong = new RichTextArea();
  
  private InfoWindowContent infoWindow;
  
  PhoneSignal collecter;

  private LatLng center;
  private LatLng LB;
  private LatLng UR;
  private VerticalPanel vp;
  protected MapWidget map;
  private ListBox carriers;
  private ListBox phoneTypes;
  // should query database to get this
  private String[] allCarriers = {
      "att", "verizon", "tmobile", "sprint"  
  };
  private String[] allPhoneTypes = {
    "0", "1","2"  
  };
  
  private double minLatitude;
  private double minLongitude;
  private double maxLatitude;
  private double maxLongitude;
  
  /**
   * Create a remote service proxy to talk to the server-side service.
   */
  private final PhoneSignalServiceAsync collabService = GWT.create(PhoneSignalService.class);

  /**
   * Operate on history tokens.
   * 
   * @param event
   */
  public void onValueChange(ValueChangeEvent<String> event) {

  }

  /**
   * This is the entry point method, meaning the first method called when this
   * module is initialized.
   */
  public void onModuleLoad() {
    collecter = new PhoneSignal(collabService);
    
    center = CALTECH;
    // Make the loading display invisible and the application visible.
    //          RootPanel.get("application").add(collecter);
    //      RootPanel.get("loading").setVisible(false);

    LBlat.setText("latitude1");
    LBlat.setHeight("40px");
    LBlat.setWidth("150px");
    LBlong.setText("longitude1");
    LBlong.setHeight("40px");
    LBlong.setWidth("150px");
    URlat.setText("latitude2");
    URlat.setHeight("40px");
    URlat.setWidth("150px");
    URlong.setText("longitude2");
    URlong.setHeight("40px");
    URlong.setWidth("150px");
    
    carriers = new ListBox(true);
    phoneTypes = new ListBox(true);
    HTML selectCarriers = new HTML("Carriers");
    HTML selectPhoneTypes = new HTML("Phone Types");
    
    for (String c: allCarriers){
      carriers.addItem(c);
    }
    
    for (String t : allPhoneTypes) {
      phoneTypes.addItem(t);
    }

    Button boundingBox = new Button("Set bounding box", new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        // TODO Auto-generated method stub
    
        // leave this for now

        try {
          minLatitude = Double.parseDouble(LBlat.getText());
          minLongitude = Double.parseDouble(LBlong.getText());
          maxLatitude = Double.parseDouble(URlat.getText());
          maxLongitude = Double.parseDouble(URlong.getText());          
        } catch (NumberFormatException e){
          Window.alert("Check your params");
        }

        
        LB = LatLng.newInstance(Double.parseDouble(LBlat.getText()), Double.parseDouble(LBlong.getText()));
        UR = LatLng.newInstance(Double.parseDouble(URlat.getText()), Double.parseDouble(URlong.getText()));

        loadMap();
      }

    });
    
    Button boundingBoxCaltech = new Button("Set box to Caltech", new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        // TODO Auto-generated method stub
        
        // leave this for now
        center = CALTECH;
        
        LBlat.setText(Double.toString(CALTECHLB.getLatitude()));
        LBlong.setText(Double.toString(CALTECHLB.getLongitude()));
        URlat.setText(Double.toString(CALTECHUR.getLatitude()));
        URlong.setText(Double.toString(CALTECHUR.getLongitude()));
        
        minLatitude = Double.parseDouble(LBlat.getText());
        minLongitude = Double.parseDouble(LBlong.getText());
        maxLatitude = Double.parseDouble(URlat.getText());
        maxLongitude = Double.parseDouble(URlong.getText());
        
        loadMap();
      }

    });
    
    HorizontalPanel hp = new HorizontalPanel();
    
    VerticalPanel carrierSelect = new VerticalPanel();
    carrierSelect.add(selectCarriers);
    carrierSelect.add(carriers);
    
    VerticalPanel phoneTypeSelect = new VerticalPanel();
    phoneTypeSelect.add(selectPhoneTypes);
    phoneTypeSelect.add(phoneTypes);
    
    hp.add(carrierSelect);
    hp.add(phoneTypeSelect);

    HorizontalPanel latlong2 = new HorizontalPanel();
    latlong2.add(LBlat);
    latlong2.add(LBlong);
    latlong2.add(URlat);
    latlong2.add(URlong);
    latlong2.add(boundingBox);
    latlong2.add(boundingBoxCaltech);
    
    VerticalPanel addEmpty = new VerticalPanel();
    HTML empty = new HTML("<br>");
    addEmpty.add(empty);
    addEmpty.add(latlong2);
    
    hp.add(addEmpty);

    vp = new VerticalPanel();

    vp.add(hp);
    vp.add(collecter);
    RootPanel.get("buttons").add(vp);
  }

  private void runApp(){
    center = LatLng.newInstance((minLatitude + maxLatitude)/2,
        (minLongitude + maxLongitude)/2);
    buildUi();
    List<String> carrierParams = new ArrayList<String>();
    for (int i = 0; i < carriers.getItemCount(); i++){
       if (carriers.isItemSelected(i)){
         carrierParams.add(carriers.getItemText(i));
       }
    }
    if (carrierParams.isEmpty()){
      carrierParams = null;
    }
    List<String> phoneTypeParams = new ArrayList<String>();
    for (int i = 0; i < phoneTypes.getItemCount(); i++){
      if (phoneTypes.isItemSelected(i)){
        phoneTypeParams.add(phoneTypes.getItemText(i));
      }
    }
    if (phoneTypeParams.isEmpty()){
      phoneTypeParams = null;
    }
    
    addPoints(minLatitude, minLongitude, 
       maxLatitude, maxLongitude,carrierParams,
        phoneTypeParams);
  }
  
  private void loadMap(){
    if (localRun){
      Maps.loadMapsApi("", "2", false, new Runnable() {
        public void run() {
          runApp();
        }
      });
    } else {
      Maps.loadMapsApi("AIzaSyC7K_2VTNtYJH8uz7pDqa5G5MebAwe309k", "2", false, new Runnable() {
        public void run() {
          runApp();
        }
      });
    }
  }

  private void addPoints(Double minLatitude, Double minLongitude,
      Double maxLatitude, Double maxLongitude, List<String> carrierParams, List<String> phoneParams){
    List<SignalMetadata> list = collecter.returnMetadata(minLatitude, minLongitude,
        maxLatitude, maxLongitude, carrierParams, phoneParams);
    System.out.println(list.size());
    
    for (SignalMetadata pt: list){
      System.out.println(pt.getLatitude() + pt.getLongitude());
      final double lat = pt.getLatitude();
      final double lon = pt.getLongitude();
      LatLng newpt = LatLng.newInstance(pt.getLatitude(), pt.getLongitude());
      LatLng newpt2 = LatLng.newInstance(pt.getLatitude(), pt.getLongitude() + 0.000005);
      String color;
      final int strength = pt.getSignal();
      
      LatLng[] newptArray = new LatLng[2];
      newptArray[0] = newpt;
      newptArray[1] = newpt2;
      
      String redHex = "00";
      int redValue = 255 - 4*(strength + 113);
      if (redValue != 0){
        redHex = Integer.toHexString((Integer) redValue );    
      }
      
      int greenValue = 4*(strength + 113);
      String greenHex = "00";
      if (greenValue != 0){
        greenHex = Integer.toHexString((Integer) greenValue);
      }
      
      String hexValue = "#" + redHex + greenHex + "00";
      Polyline marker = new Polyline(newptArray, hexValue,10, 0.7);
      
      marker.addPolylineMouseOverHandler(new PolylineMouseOverHandler() {
        @Override
        public void onMouseOver(PolylineMouseOverEvent event) {
          map.getInfoWindow().open(LatLng.newInstance(
              lat, lon), new InfoWindowContent(
              "Location: " + lat + " " + lon +
              "<br/>Signal strength: " + ((Integer) strength).toString()));
        }
      });
      marker.addPolylineMouseOutHandler(new PolylineMouseOutHandler() {
        @Override
        public void onMouseOut(PolylineMouseOutEvent event) {
          map.getInfoWindow().close();
        }
      });
      
      map.addOverlay(marker);

    }
  }
  
  private void buildUi() {
    map = new MapWidget(center, 2);
    map.setSize("100%", "100%");
    // Add some controls for the zoom level
    map.addControl(new LargeMapControl());
    map.setZoomLevel(17);

    LayoutPanel dock = new LayoutPanel();
    dock.add(map);

    RootLayoutPanel.get().add(dock);

  }

}

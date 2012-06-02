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

public class PhoneSignalEntryPoint implements EntryPoint, ValueChangeHandler<String> {
  final LatLng CALTECH = LatLng.newInstance(34.139, -118.124);
  final LatLng CALTECH2 = LatLng.newInstance(34.15, -118.124);
  final LatLng CALTECH1 = LatLng.newInstance(34.139, -118.129);

  final RichTextArea latitude = new RichTextArea();
  final RichTextArea longitude = new RichTextArea();
  PhoneSignal collecter;

  private LatLng center;
  private VerticalPanel vp;
  protected MapWidget map;
  private ListBox carriers;
  // should query database to get this
  private String[] allCarriers = {
    "AT&T", "Verizon", "T-Mobile", "Sprint"  
  };
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


    latitude.setText("latitude");
    latitude.setHeight("40px");
    latitude.setWidth("150px");
    longitude.setText("longitude");
    longitude.setHeight("40px");
    longitude.setWidth("150px");
    
    carriers = new ListBox(true);
    HTML selectCarriers = new HTML("Select carriers");
    
    for (String c: allCarriers){
      carriers.addItem(c);
    }
    
    Button setLoc = new Button("Set map center", new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        center = LatLng.newInstance(Double.parseDouble(latitude.getText()), Double.parseDouble(longitude.getText()));
        Maps.loadMapsApi("AIzaSyC7K_2VTNtYJH8uz7pDqa5G5MebAwe309k", "2", false, new Runnable() {
          public void run() {
            runApp();
          }
        });

      }
    });

    Button caltech = new Button("Center at Caltech", new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        // TODO Auto-generated method stub
        center = CALTECH;
        latitude.setText(Double.toString(CALTECH.getLatitude()));
        longitude.setText(Double.toString(CALTECH.getLongitude()));
        Maps.loadMapsApi("AIzaSyC7K_2VTNtYJH8uz7pDqa5G5MebAwe309k", "2", false, new Runnable() {
          public void run() {
            runApp();
          }
        });
      }

    });

    HorizontalPanel hp = new HorizontalPanel();
    
    VerticalPanel carrierSelect = new VerticalPanel();
    carrierSelect.add(selectCarriers);
    carrierSelect.add(carriers);
    
    hp.add(carrierSelect);
    
    HorizontalPanel latlongparam = new HorizontalPanel();

    latlongparam.add(latitude);
    latlongparam.add(longitude);
    latlongparam.add(setLoc);
    latlongparam.add(caltech);
    
    VerticalPanel addEmpty = new VerticalPanel();
    HTML empty = new HTML("<br>");
    addEmpty.add(empty);
    addEmpty.add(latlongparam);
    
    hp.add(addEmpty);

    vp = new VerticalPanel();

    vp.add(hp);
    vp.add(collecter);
    RootPanel.get("buttons").add(vp);
  }

  private void runApp(){
    buildUi();
    List<String> carrierParams = new ArrayList<String>();
    for (int i = 0; i < carriers.getItemCount(); i ++){
       if (carriers.isItemSelected(i)){
         carrierParams.add(carriers.getItemText(i));
       }
    }
    if (carrierParams.isEmpty()) {
      // add all carriers to the list
      for (String c: allCarriers){
        carrierParams.add(c);
      }
    }
    addPoints(carrierParams);
  }
  
  private void addPoints(List<String> carrierParams){
    List<SignalMetadata> list = collecter.returnMetadata(carrierParams);
    System.out.println(list.size());
    
    for (SignalMetadata pt: list){
      System.out.println(pt.getLatitude() + pt.getLongitude());
      LatLng newpt = LatLng.newInstance(pt.getLatitude(), pt.getLongitude());
      LatLng newpt2 = LatLng.newInstance(pt.getLatitude(), pt.getLongitude() + 0.000005);
      String color;
      int strength = pt.getSignal();
      
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
      Polyline marker = new Polyline(newptArray, hexValue,10, 0.3);
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

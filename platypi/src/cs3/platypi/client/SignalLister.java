package cs3.platypi.client;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.maps.client.overlay.Marker;
import com.google.gwt.user.client.rpc.AsyncCallback;

import cs3.platypi.shared.SignalMetadata;

/**
 * Used in conjunction with <code>CollaboratorService.getDocumentList()</code>.
 */
public class SignalLister implements AsyncCallback<List<SignalMetadata>> {

  private PhoneSignal signalCollacter;

  public SignalLister(PhoneSignal signalCollacter) {
    this.signalCollacter = signalCollacter;
  }
  
  public void getAllSignalList() {
      System.out.println("Fetching signal list");
      signalCollacter.signalService.getAllSignalList(this);
  }
  
  public void getAllSignalListAvg() {
      System.out.println("Fetching signal list");
      signalCollacter.signalService.getAllSignalListAvg(this);
  }

  public void getSignalList(Double minLatitude, Double minLongitude,
          Double maxLatitude, Double maxLongitude, List<String> carrierParams, List<String> phoneTypes) {
    System.out.println("Fetching signal list");
    signalCollacter.signalService.getSignalList(minLatitude, 
            minLongitude, maxLatitude, maxLongitude, carrierParams, phoneTypes, this);
  }

  public void getSignalList(Double minLatitude, Double minLongitude,
          Double maxLatitude, Double maxLongitude, List<String> params) {
    System.out.println("Fetching signal list");
    signalCollacter.signalService.getSignalList(minLatitude, 
            minLongitude, maxLatitude, maxLongitude, params, this);
  }

  public void getSignalList(Double minLatitude, Double minLongitude,
          Double maxLatitude, Double maxLongitude) {
    System.out.println("Fetching signal list");
    signalCollacter.signalService.getSignalList(minLatitude, 
            minLongitude, maxLatitude, maxLongitude, this);
  }

  @Override
  public void onFailure(Throwable caught) {
    System.out.println("Error retrieving signal list" + "; caught exception "
        + caught.getClass() + " with message: " + caught.getMessage());
    GWT.log("Error getting document list.", caught);
  }

  @Override
  public void onSuccess(List<SignalMetadata> result) {
    if (result == null || result.size() == 0) {
      System.out.println("No signal available.");
    } else {
      System.out.println("Signal list updated.");
      GWT.log("Got " + result.size() + " signal Info.");

      signalCollacter.mainSignalList.clear();
      for (SignalMetadata meta : result) {
        signalCollacter.addMetadataToList(meta);
        signalCollacter.mainSignalList.addItem(meta.toString());

        System.out.println(meta.toString());
        //                System.out.println(meta.getLatitude() + " " + meta.getLongitude() + " "
        //                        + meta.getSignal());
      }
    }

  }
}

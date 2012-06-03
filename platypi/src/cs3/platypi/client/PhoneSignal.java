package cs3.platypi.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.HTML;

import cs3.platypi.shared.SignalMetadata;

public class PhoneSignal extends Composite implements ClickHandler, ChangeHandler {

    protected PhoneSignalServiceAsync signalService;
    protected SignalLister lister = new SignalLister(this);
    protected SignalSaver saver = new SignalSaver(this);
    protected ListBox mainSignalList = new ListBox();
    public HTMLTable signalTable = new Grid();
    protected List<SignalMetadata> signalMetadataList = new ArrayList<SignalMetadata>();

    public PhoneSignal(PhoneSignalServiceAsync signalService) {
        this.signalService = signalService;

        // Example of using saveSignal and getSignalList
        List<SignalMetadata> signalList = new ArrayList<SignalMetadata>();
        SignalMetadata s1 = new SignalMetadata("judy", "att", 34.137987,-118.125558, 15.5, "1", 123, 0);
        SignalMetadata s2 = new SignalMetadata("judy", "att", 4.3, 5.1, 15.5, "1", 123, 3);
        signalList.add(s1);
        signalList.add(s2);
        // Do not call both save and get in the same run. 
//         saver.saveSignal(signalList);
        lister.getSignalList();

        VerticalPanel outerVp = new VerticalPanel();
        outerVp.add(new HTML("<h2>Available Signals</h2>"));
        
        outerVp.add(new HTML("Latitude Longitude Signal"));
        mainSignalList.setVisibleItemCount(10);
        outerVp.add(mainSignalList);
        
        
        System.out.println(signalMetadataList.size());

        initWidget(outerVp);
    }

    @Override
    public void onChange(ChangeEvent event) {
        // TODO Auto-generated method stub

    }

    public void addMetadataToList(SignalMetadata meta){
      this.signalMetadataList.add(meta);
    }
    
    public List<SignalMetadata> returnMetadata(){
      return signalMetadataList;
    }
    
    public List<SignalMetadata> returnMetadata(List<String> carrierParams){
      return signalMetadataList;
    }
    
    @Override
    public void onClick(ClickEvent event) {
        // TODO Auto-generated method stub

    }
}
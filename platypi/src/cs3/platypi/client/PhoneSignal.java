package cs3.platypi.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;

import cs3.platypi.shared.SignalMetadata;

public class PhoneSignal extends Composite implements ClickHandler, ChangeHandler {

    protected PhoneSignalServiceAsync signalService;
    protected SignalLister lister = new SignalLister(this);
    protected SignalSaver saver = new SignalSaver(this);

    public PhoneSignal(PhoneSignalServiceAsync signalService) {
        this.signalService = signalService;

        // Example of using saveSignal and getSignalList
        List<SignalMetadata> signalList = new ArrayList<SignalMetadata>();
        SignalMetadata s1 = new SignalMetadata(1, 2, 3);
        SignalMetadata s2 = new SignalMetadata(4, 5, 6);
        signalList.add(s1);
        signalList.add(s2);
        // Do not call both save and get in the same run. 
        // saver.saveSignal(signalList);
        lister.getSignalList();

        HorizontalPanel outerHp = new HorizontalPanel();
        outerHp.add(new HTML("<h2>Available Signals</h2>"));
        initWidget(outerHp);
    }

    @Override
    public void onChange(ChangeEvent event) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onClick(ClickEvent event) {
        // TODO Auto-generated method stub

    }
}
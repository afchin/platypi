package cs3.platypi.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.RootPanel;

public class PhoneSignalEntryPoint implements EntryPoint, ValueChangeHandler<String> {

    PhoneSignal collecter;

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

        // Make the loading display invisible and the application visible.
        RootPanel.get("application").add(collecter);
        RootPanel.get("loading").setVisible(false);

    }

}

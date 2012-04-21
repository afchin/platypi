package cs3.platypi.client;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

import cs3.platypi.shared.SignalMetadata;

/**
 * Used in conjunction with <code>CollaboratorService.getDocumentList()</code>.
 */
public class SignalSaver implements AsyncCallback<Void> {

    private PhoneSignal signalCollacter;

    public SignalSaver(PhoneSignal signalCollacter) {
        this.signalCollacter = signalCollacter;
    }

    public void saveSignal(List<SignalMetadata> signal) {
        System.out.println("save signal");
        signalCollacter.signalService.saveSignalInfo(signal, this);
    }

    @Override
    public void onFailure(Throwable caught) {
        System.out.println("Error save a signal list" + "; caught exception " + caught.getClass()
                + " with message: " + caught.getMessage());
        GWT.log("Error saving signal list.", caught);
    }

    @Override
    public void onSuccess(Void result) {
        System.out.println("succesfully save a list of signal");
    }

}

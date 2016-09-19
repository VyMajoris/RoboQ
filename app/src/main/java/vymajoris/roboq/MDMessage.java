package vymajoris.roboq;

import com.google.firebase.database.MutableData;
import com.google.firebase.messaging.RemoteMessage;

public class MDMessage {

    public MutableData getMutableData() {
        return mutableData;
    }

    public void setMutableData(MutableData mutableData) {
        this.mutableData = mutableData;
    }

    MutableData mutableData;
}
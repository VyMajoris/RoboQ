package vymajoris.roboq;

import com.google.firebase.messaging.RemoteMessage;

public class FCMMessage {

    public  RemoteMessage getRootMessage() {
        return rootMessage;
    }

    public FCMMessage setRootMessage(RemoteMessage rootMessage) {
        this.rootMessage = rootMessage;
        return this;
    }

    public RemoteMessage rootMessage;
}
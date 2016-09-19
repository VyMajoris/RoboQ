package vymajoris.roboq;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.MutableData;

/**
 * Created by VyMajoriss on 9/18/2016.
 */

public class MyOnsuccessListener implements OnSuccessListener {

    public MyOnsuccessListener(MutableData mutableData) {
        this.mutableData = mutableData;
    }

    public MutableData getMutableData() {
        return mutableData;
    }

    public void setMutableData(MutableData mutableData) {
        this.mutableData = mutableData;
    }

    MutableData mutableData;

    @Override
    public void onSuccess(Object o) {


    }
}

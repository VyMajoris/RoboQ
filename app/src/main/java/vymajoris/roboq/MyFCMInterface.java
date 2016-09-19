package vymajoris.roboq;

/**
 * Created by VyMajoriss on 9/17/2016.
 */

public interface MyFCMInterface {

    void onAuthFail(String error);

    void onAuthSuccess(String msg);
}

package vymajoris.roboq;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface RoboQApi {
    @POST("getTicket")
    Call<JsonObject> getTicket(@Body JsonObject body);

    @POST("forfeitTicket/")
    Call<JsonObject> forfeitTicket(@Body JsonObject body);


}

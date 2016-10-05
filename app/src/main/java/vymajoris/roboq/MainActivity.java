package vymajoris.roboq;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.glxn.qrgen.android.QRCode;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MainActivity extends AppCompatActivity {
    String deviceID = FirebaseInstanceId.getInstance().getToken();
    public static final String API = "http://ec2-52-43-169-138.us-west-2.compute.amazonaws.com:8080/";

    ProgressDialog progressDoalog;

    Long qPosLong;
    TextView qPos;
    ImageView qrCode;
    TextView qSize;
    Button ticketBtn;
    Retrofit retrofit = new Retrofit.Builder().baseUrl(API).addConverterFactory(GsonConverterFactory.create()).build();
    RoboQApi roboqAPI = retrofit.create(RoboQApi.class);
    private boolean onQueue = false;
    private TextView qrCodeAuthText;
    private TextView ticketRetrivedText;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    final DatabaseReference queuersRef = database.getReference("estbXYZ/queue/queuers");
    private Drawable ticketBtnBackground;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progressDoalog = new ProgressDialog(MainActivity.this);
        progressDoalog.setMessage("Carregando...");
        progressDoalog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDoalog.show();

        setContentView(R.layout.activity_main);

        qPos = (TextView) findViewById(R.id.queuePos);
        qrCode = (ImageView) findViewById(R.id.qrCodeImg);
        qrCode.setVisibility(View.INVISIBLE);
        qSize = (TextView) findViewById(R.id.queueSize);
        qrCodeAuthText = (TextView) findViewById(R.id.qrCodeAuthText);
        ticketBtn = (Button) findViewById(R.id.ticketBtn);
        ticketBtnBackground = ticketBtn.getBackground();
        ticketRetrivedText = (TextView) findViewById(R.id.ticketRetrieved);


        queuersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                qSize.setText(getResources().getString(R.string.queueSize) + " " + String.valueOf(dataSnapshot.getChildrenCount()));
                qSize.setVisibility(View.VISIBLE);
                progressDoalog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        queuersRef.child(deviceID).child("pos").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                System.out.println(" POS CHILD CHANGE");

                qPosLong = dataSnapshot.getValue(new GenericTypeIndicator<Long>() {
                    @Override
                    public int hashCode() {
                        return super.hashCode();
                    }
                });
                System.out.println(qPosLong);
                if (qPosLong != null) {
                    onQueue = true;
                    updateFront(true);
                } else {
                    onQueue = false;
                    updateFront(false);
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        ticketBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {


                if (!onQueue) {

                    System.out.println("on queue");
                    System.out.println(onQueue);
                    System.out.println("DEVI E IDEDED");
                    System.out.println(deviceID);
                    queuersRef.runTransaction(new Transaction.Handler() {
                        @Override
                        public Transaction.Result doTransaction(final MutableData mutableData) {

                            Long pos = mutableData.getChildrenCount() == 0 ? 1 : mutableData.getChildrenCount() + 1;
                            System.out.println("Pos");
                            System.out.println(pos);
                            mutableData.child(deviceID).child("pos").setValue(pos);
                            return Transaction.success(mutableData);
                        }

                        @Override
                        public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                            onQueue = true;
                            updateFront(onQueue);

                        }
                    });


                } else {

                    JsonObject json = new JsonObject();
                    json.add("deviceID", new JsonParser().parse('"' + FirebaseInstanceId.getInstance().getToken() + '"'));
                    final Call<JsonObject> forfeitTicketQueue = roboqAPI.forfeitTicket(json);
                    forfeitTicketQueue.enqueue(new Callback<JsonObject>() {
                        @Override
                        public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                            System.out.println(response);
                            onQueue = false;
                            Snackbar.make(findViewById(android.R.id.content), R.string.ticketForfeited, 5000).show();
                        }

                        @Override
                        public void onFailure(Call<JsonObject> call, Throwable t) {
                            System.out.println(t);
                            Snackbar.make(findViewById(android.R.id.content), t.getMessage(), 10000).show();
                        }
                    });
                }

            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(FCMMessage event) {
        if (event.getRootMessage().getData().get("auth_status").equals("false")) {
        } else {
            onQueue = false;
            updateFront(onQueue);
        }
        Snackbar.make(findViewById(android.R.id.content), event.getRootMessage().getNotification().getBody(), 10000).show();
    }

    private void updateFront(boolean onQueue) {
        if (onQueue) {

            ticketBtn.setBackgroundColor(Color.RED);
            qrCode.setVisibility(View.VISIBLE);
            qrCode.setImageBitmap(Bitmap.createScaledBitmap(BitMapUtil.changeColor(QRCode.from(deviceID).bitmap(), Color.WHITE, Color.TRANSPARENT), 1000, 1000, false));
            ticketBtn.setEnabled(true);
            ticketBtn.setText(R.string.forfeitTicket);
            qrCodeAuthText.setVisibility(View.VISIBLE);


            qPos.setText(getResources().getString(R.string.queuePos) + qPosLong);
            qPos.setVisibility(View.VISIBLE);
            ticketRetrivedText.setVisibility(View.VISIBLE);

        } else {
            ticketBtn.setEnabled(true);
            qrCode.setVisibility(View.INVISIBLE);
            qrCode.setImageBitmap(null);
            qrCodeAuthText.setVisibility(View.INVISIBLE);
            ticketRetrivedText.setVisibility(View.INVISIBLE);
            ticketBtn.setBackground(ticketBtnBackground);
            ticketBtn.setText(R.string.ticketBtn);
            qPos.setVisibility(View.INVISIBLE);


        }

    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }


}

package com.example.projet_final;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class saveLocation extends Service {

    private DatabaseReference mReference;
    private static GoogleMap Mmap;
    private static String userID;
    private FusedLocationProviderClient mFusedLocationClient;
    private FirebaseDatabase mFirebaseDatabase;
    private Location location;
    private Notification notification;
    /*private NotificationManager maneger;
    private final String channelID="channel_ID";*/

    //theread class
    final class theTheread implements Runnable{


        public theTheread(){

        }

        @Override
        public void run() {
            Log.i("seveLocation","theredRun");
            int i=0;
            mReference.child("users").child(userID).child("stat").setValue("online");
            //notication();
            final long[] time = {System.currentTimeMillis() + 1000};
            while (true){
                i++;
                //Log.i("seveLocation","while i="+i);
                mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            location = task.getResult();
                            Log.i("seveLocation","secc");
                            LatLng user_location = new LatLng(location.getLatitude(), location.getLongitude());
                            /*marker=new MarkerOptions().position(user_location).title("Marker in your location");
                            mMap.addMarker(marker);
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(user_location));
                            CameraPosition cameraPosition = new CameraPosition.Builder()
                                    .target(user_location)      // Sets the center of the map to user_location
                                    .zoom(15)                   // Sets the zoom to 15 (city zoom)
                                    .build();                   // Creates a CameraPosition from the builder
                            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));*/
                            //Toast.makeText(saveLocation.this,"Latitude:"+location.getLatitude()+"   Longitude:"+location.getLongitude(),Toast.LENGTH_SHORT).show();
                            Log.i("seveLocation","Latitude:"+location.getLatitude()+"   Longitude:"+location.getLongitude());
                            mReference.child("users").child(userID).child("Latitude").setValue(location.getLatitude());
                            mReference.child("users").child(userID).child("Longitude").setValue(location.getLongitude());
                            Log.i("seveLocation","saved");
                            //SystemClock.sleep(1000);
                            /*while (System.currentTimeMillis()< time[0]){

                            }
                            time[0] +=1000;*/

                        }else{
                            Log.i("seveLocation","not succ");
                        }
                    }
                });

            }
        }



    }

    //the therad end


    private Thread saveL;
    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("seveLocation","serviceCreat");
        init();
        //creatNotification();
    }

   /* private void creatNotification() {
        if(Build.VERSION.SDK_INT >=Build.VERSION_CODES.O){
            NotificationChannel notificationChannel= new NotificationChannel(channelID
                    , "channelName"
                    , NotificationManager.IMPORTANCE_DEFAULT);
            maneger=getSystemService(NotificationManager.class);
            maneger.createNotificationChannel(notificationChannel);
        }
    }*/

    public void init(){
        mFirebaseDatabase=FirebaseDatabase.getInstance();
        mReference=mFirebaseDatabase.getReference();
        mFusedLocationClient= LocationServices.getFusedLocationProviderClient(saveLocation.this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this,"thereadStart",Toast.LENGTH_SHORT).show();
        Log.i("seveLocation","serviceStart");
        saveL=new Thread(new theTheread());
        saveL.start();
        notication();
        return START_NOT_STICKY;
        //return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        saveL.interrupt();
        mReference.child("users").child(userID).child("stat").setValue("offline");
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static void setMmap(GoogleMap mmap) {
        Mmap = mmap;
    }

    public static void setUserID(String userID) {
        saveLocation.userID = userID;
    }

    private void notication() {
        Intent NIntent=new Intent(saveLocation.this,MapsActivity.class);
        PendingIntent pendingIntent=PendingIntent.getActivities(saveLocation.this,0, new Intent[]{NIntent},0);
        final String channelID="channel_ID";
        notification=new NotificationCompat.Builder(this,channelID)
                .setContentTitle("working")
                .setContentText("you are working now")
                .setSmallIcon(R.drawable.ic_work_black_24dp)
                .setContentIntent(pendingIntent)
                .build();
        startForeground(123,notification);
    }
}

package com.tkctechnologies.codename_tkc.droneandroid;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Camera;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.tkctechnologies.codename_tkc.droneandroid.config.config.Config_holder;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    ImageButton imageButtons[];
    final String TAG = "DroneAndroid";
    TextView indicator;
    GoogleMap droneMap;

    /*Polyline line = map.addPolyline(new PolylineOptions()
     .add(new LatLng(51.5, -0.1), new LatLng(40.7, -74.0))
     .width(5)
     .color(Color.RED));*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        startActivity(new Intent(this,Main2Activity.class));
        imageButtons = new ImageButton[]{
                (ImageButton) findViewById(R.id.move_forward),
                (ImageButton) findViewById(R.id.move_left),
                (ImageButton) findViewById(R.id.move_right),
                (ImageButton) findViewById(R.id.move_down),
                (ImageButton) findViewById(R.id.camera_down),
                (ImageButton) findViewById(R.id.camera_up),
                (ImageButton) findViewById(R.id.camera_left),
                (ImageButton) findViewById(R.id.camera_right)
        };
        indicator = (TextView) findViewById(R.id.indicator);
        for (ImageButton imageButton : imageButtons) {
            imageButton.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
//                        for both motionEvent.ACTION_DOWN && motionEvent.ACTION_UP
                    if (motionEvent.getAction() == MotionEvent.ACTION_DOWN || motionEvent.getAction() == MotionEvent.ACTION_UP) {
                        String buttonId = getResources().getResourceEntryName(view.getId());
                        Log.i(TAG, "Click : " + buttonId);
                        indicator.setText("operation : " + buttonId);
                        sendData(buttonId);
                        return true;
                    }
                    return false;
                }
            });
        }
        final EditText editText = new EditText(this);
        editText.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        new AlertDialog.Builder(this)
                .setCancelable(false)
                .setMessage("Input the ip address and the port of the server (format ip:port)")
                .setView(editText)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        try {
                            Config_holder.ipAddress = editText.getText().toString().split(":")[0];
                            Config_holder.port = Integer.parseInt(editText.getText().toString().split(":")[1]);
                            indicator.setText("Configured : (" + Config_holder.ipAddress + "," + Config_holder.port + ")");
                        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                            Toast.makeText(getApplicationContext(), "The format is inccorrect", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setTitle("IP Address prompt")
                .create().show();
        VideoView videoView = (VideoView) findViewById(R.id.videoview);
        // MediaController mediaController = new MediaController(this);
        //mediaController.setAnchorView(videoView);
        videoView.setVideoURI(Uri.parse("http://192.168.43.111:8081"));
        videoView.seekTo(0);
//        videoView.start();


        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.start();
            }
        });
        videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                mediaPlayer.pause();
                return false;
            }
        });
    }

    void sendData(final String data) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Socket socket = new Socket(Config_holder.ipAddress, Config_holder.port);
                    OutputStream outputStream = socket.getOutputStream();
                    outputStream.write(data.getBytes());
                    outputStream.close();
                    socket.close();
                } catch (final IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            indicator.setText("operation : " + data + " failed ");

                        }
                    });
                }
            }
        }).start();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if(droneMap!=null){
                    droneMap.addPolyline(new PolylineOptions().add(new LatLng(location.getLatitude(),location.getLongitude())).color(Color.RED));
                    droneMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(),location.getLongitude())));
                }
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        droneMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        droneMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        droneMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
}

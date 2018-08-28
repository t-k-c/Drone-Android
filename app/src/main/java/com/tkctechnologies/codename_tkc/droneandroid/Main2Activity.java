package com.tkctechnologies.codename_tkc.droneandroid;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import com.tkctechnologies.codename_tkc.droneandroid.config.config.Config_holder;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class Main2Activity extends AppCompatActivity {
    Button button;
    RelativeLayout relativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
  /*      button = (Button) findViewById(R.id.innerButton);
        relativeLayout = (RelativeLayout) findViewById(R.id.container);
        final int decalageY = relativeLayout.getHeight() - button.getHeight();
        final int decalageX = relativeLayout.getWidth() - button.getWidth();
        final float oGX = button.getX();
        final float oGY = button.getY();
        Toast.makeText(this, button.getLeft() + ":" + button.getTop(), Toast.LENGTH_LONG).show();*/
/*        button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_MOVE){
                   view.setX(motionEvent.getX());
                   view.setY(motionEvent.getY());
                }
                if(motionEvent.getAction() ==  MotionEvent.ACTION_UP){
                    view.setLeft(0);
                    view.setTop(0);
                }
                return false;
            }
        });*/
       /* button.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View view, DragEvent dragEvent) {
                Log.e("time", "drag at " + dragEvent.getX() + " " + dragEvent.getY());
                return false;
            }
        });*/
       final VideoView videoView = (VideoView) findViewById(R.id.vid);
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);

//       videoView.setVideoURI(Uri.parse("http://192.168.137.16:1998"));
//        videoView.start();
      // final
       // videoView.setVideoURI(Uri.parse("http://192.168.137.16:1998"));
        videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                videoView.pause();
                return false;
            }
        });
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                videoView.start();
            }
        });
        new Thread(new Runnable() {
            @Override
            public void run() {
                //new File(Environment.getExternalStorageDirectory()+"test/vid.mp4");

                    try {
                        final File file = File.createTempFile("video",".mp4");
                        Socket socket = new Socket(Config_holder.ipAddress, Config_holder.port);
//                       runOnUiThread(new Runnable() {
//                           @Override
//                           public void run() {
//                               videoView.setVideoURI(Uri.parse(file.getPath()));
//                           }
//                       });
//                        videoView.seekTo(0);
//                        videoView.start();
                          OutputStream outputStream = socket.getOutputStream();
                          outputStream.write("video".getBytes());
                          InputStream inputStream = socket.getInputStream();
                        final FileOutputStream fileOutputStream = new FileOutputStream(file);
                        final DataInputStream dataInputStream = new DataInputStream(inputStream);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                byte[] buffer = new byte[124];
                                int length;
                                try{
                                    while ((length = dataInputStream.read(buffer)) != -1) {
                                    fileOutputStream.write(buffer,0,length);
                                        Log.e("time","Copied "+length+" bytes");
                                }
                                }
                                catch(Exception e){
//                                  continue
                                }
                            }
                        }).start();
                        dataInputStream.close();
                        fileOutputStream.close();
                        inputStream.close();
                        outputStream.close();
                        socket.close();


                    } catch (IOException e) {
                        e.printStackTrace();
                    }


            }
        }).start();

    }
}

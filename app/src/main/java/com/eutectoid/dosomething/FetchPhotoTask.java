package com.eutectoid.dosomething;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by robbt on 4/9/16.
 */
public class FetchPhotoTask  extends AsyncTask<String,Void,Bitmap> {

        public interface OnTaskCompleted{
            void onTaskCompleted();
        }

        private OnTaskCompleted listener;

        public FetchPhotoTask(OnTaskCompleted listener){
            this.listener = listener;
        }

        protected void onPostExecute(Bitmap bitmap){
            // Call the interface method
            if (listener != null)
                listener.onTaskCompleted();
        }
        private Bitmap fetchImage( String urlstr )
        {
            try
            {
                URL url;
                url = new URL( urlstr );

                HttpURLConnection c = ( HttpURLConnection ) url.openConnection();
                c.setDoInput( true );
                c.connect();
                InputStream is = c.getInputStream();
                Bitmap img;
                img = BitmapFactory.decodeStream(is);
                return img;
            }
            catch ( MalformedURLException e )
            {
                Log.d("RemoteImageHandler", "fetchImage passed invalid URL: " + urlstr);
            }
            catch ( IOException e )
            {
                Log.d( "RemoteImageHandler", "fetchImage IO exception: " + e );
            }
            return null;
        }
        public Bitmap doInBackground(String... args) {
            for (String parameter : args) {
                Bitmap photoImage = fetchImage(parameter);
                return photoImage;
            }

            return null;
        }
    };


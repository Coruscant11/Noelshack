package com.coruscant11.noelshackimageupload.network;

import android.app.NotificationManager;
import android.content.Context;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.NotificationCompat;

import com.coruscant11.noelshackimageupload.NoelshackBackgroundService;
import com.coruscant11.noelshackimageupload.R;

import net.yazeed44.imagepicker.model.ImageEntry;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class BackgroundUploader extends AsyncTask<Void, Integer, String> {

    private ImageEntry m_Image;
    private int m_ImageLength;

    private NoelshackBackgroundService m_Service;

    private NotificationManager m_NotificationManager;
    private NotificationCompat.Builder m_Builder;

    public BackgroundUploader(ImageEntry image, NoelshackBackgroundService service) {
        this.m_Image = image;
        this.m_ImageLength = (int) new File(image.path).length();
        this.m_Service = service;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        this.m_NotificationManager = (NotificationManager) this.m_Service.getSystemService(Context.NOTIFICATION_SERVICE);
        this.m_Builder = new NotificationCompat.Builder(this.m_Service);
        this.m_Builder.setContentTitle(this.m_Service.getString(R.string.fewSec))
                .setContentText(this.m_Service.getString(R.string.progress))
                .setSmallIcon(R.drawable.ic_cloudupload)
                .setProgress(100, 0, false);
        this.m_NotificationManager.notify(1, this.m_Builder.build());
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        int actualProgress = (int) (progress[0]);
        int finalProgress = (actualProgress * 100) / this.m_ImageLength;
        this.m_Builder.setProgress(100, finalProgress, false);
        this.m_NotificationManager.notify(1, this.m_Builder.build());
    }

    @Override
    protected String doInBackground(Void... params) {
        File file = new File(this.m_Image.path);

        HttpURLConnection.setFollowRedirects(false);
        HttpURLConnection connection = null;
        String fileName = file.getName();
        try {
            connection = (HttpURLConnection) new URL("http://www.noelshack.com/api.php").openConnection();
            connection.setRequestMethod("POST");
            String boundary = "---------------------------boundary";
            String tail = "\r\n--" + boundary + "--\r\n";
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            connection.setDoOutput(true);

            String metadataPart = "--" + boundary + "\r\n"
                    + "Content-Disposition: form-data; name=\"metadata\"\r\n\r\n"
                    + "" + "\r\n";

            String fileHeader1 = "--" + boundary + "\r\n"
                    + "Content-Disposition: form-data; name=\"fichier\"; filename=\""
                    + fileName + "\"\r\n"
                    + "Content-Type: image/png\r\n"
                    + "Content-Transfer-Encoding: binary\r\n";

            long fileLength = file.length() + tail.length();
            String fileHeader2 = "Content-length: " + fileLength + "\r\n";
            String fileHeader = fileHeader1 + fileHeader2 + "\r\n";
            String stringData = metadataPart + fileHeader;

            long requestLength = stringData.length() + fileLength;
            connection.setRequestProperty("Content-length", "" + requestLength);
            connection.setFixedLengthStreamingMode((int) requestLength);
            connection.connect();

            DataOutputStream out = new DataOutputStream(connection.getOutputStream());
            out.writeBytes(stringData);
            out.flush();

            int progress = 0;
            int bytesRead = 0;
            byte buf[] = new byte[1024];
            BufferedInputStream bufInput = new BufferedInputStream(new FileInputStream(file));
            while ((bytesRead = bufInput.read(buf)) != -1) {
                // write output
                out.write(buf, 0, bytesRead);
                out.flush();
                progress += bytesRead;
                // update progress bar
                publishProgress(progress);
            }

            // Write closing boundary and close stream
            out.writeBytes(tail);
            out.flush();
            out.close();

            // Get server response
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line = "";
            StringBuilder builder = new StringBuilder();
            while((line = reader.readLine()) != null) {
                builder.append(line);
            }

            String finalUrl = builder.toString().replaceAll("www\\.noelshack\\.com/([0-9]+)-([0-9]+)-(.+)$", "image.noelshack.com/fichiers/$1/$2/$3");
            return finalUrl;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) connection.disconnect();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        this.m_Builder.setProgress(0, 0, true);
        this.m_NotificationManager.notify(1, this.m_Builder.build());

        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        this.m_Builder.setSound(uri);

        this.m_Builder.setContentTitle(this.m_Service.getString(R.string.finished))
                .setContentText(this.m_Service.getString(R.string.copied))
                .setSmallIcon(R.drawable.ic_ok)
                .setAutoCancel(true)
                .setProgress(0, 0, false);

        BackgroundEndProcess endProcess = new BackgroundEndProcess(this.m_Service, s, this.m_Image);
        endProcess.process();

        this.m_NotificationManager.notify(1, this.m_Builder.build());
    }
}

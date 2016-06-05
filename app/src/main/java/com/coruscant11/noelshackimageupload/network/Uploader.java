package com.coruscant11.noelshackimageupload.network;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.widget.Toast;

import com.coruscant11.noelshackimageupload.Noelshack;
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

public class Uploader extends AsyncTask<Void, Integer, String>
{

    private Noelshack m_Context;
    private ImageEntry m_Image;
    private String m_FilePictureUri;
    private ProgressDialog m_ProgressDialog;
    private int m_FileLength;

    public Uploader(ImageEntry image, Noelshack c) {
        this.m_Image = image;
        this.m_FilePictureUri = image.path;
        this.m_Context = c;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        this.m_FileLength = (int) new File(m_FilePictureUri).length();

        m_ProgressDialog = new ProgressDialog(m_Context);
        m_ProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        m_ProgressDialog.setTitle(m_Context.getString(R.string.fewSec));
        m_ProgressDialog.setMessage(m_Context.getString(R.string.wait));
        m_ProgressDialog.setMax(100);
        m_ProgressDialog.show();
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        int actualProgress = (int) (progress[0]);
        int finalProgress = (actualProgress * 100) / m_FileLength;
        m_ProgressDialog.setProgress(finalProgress);
    }

    @Override
    protected String doInBackground(Void... params) {
        File file = new File(m_FilePictureUri);

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
    protected void onPostExecute(String result) {
        m_ProgressDialog.setMessage(m_Context.getResources().getString(R.string.waitCard));
        ActivityEndProcess endProcess = new ActivityEndProcess(m_Context, result, m_Image);
        endProcess.process();
        m_ProgressDialog.dismiss();
    }
}
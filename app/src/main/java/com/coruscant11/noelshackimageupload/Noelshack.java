package com.coruscant11.noelshackimageupload;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.coruscant11.noelshackimageupload.adapters.GridViewAdapter;
import com.coruscant11.noelshackimageupload.datas.UploadSave;
import com.coruscant11.noelshackimageupload.managers.CameraCapture;
import com.coruscant11.noelshackimageupload.managers.HistoryManager;
import com.coruscant11.noelshackimageupload.network.Uploader;
import com.getbase.floatingactionbutton.FloatingActionButton;

import net.yazeed44.imagepicker.model.ImageEntry;
import net.yazeed44.imagepicker.util.Picker;

import java.util.ArrayList;

public class Noelshack extends ActionBarActivity {

    private FloatingActionButton m_FloatingButtonGallery, m_FloatingButtonCamera;

    private GridView m_GridView;
    private GridViewAdapter m_GridViewAdapter;

    public static final String HISTORY_FILE = "sdcard/NoelshackHistory.nlshk";

    public static final String NOELSHACK_UPLOAD_REDIRECTION = "com.coruscant11.noelshackimageupload.UPLOAD_REDIRECTION";
    public static final String NOELSHACK_IMAGE_PATH = "com.coruscant11.noelshackimageupload.IMAGE_PATH";

    public static Noelshack m_s_Context;
    public int h;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_noelshack);

        this.checkPermissions();
        this.loadViews(); // Charge les views
        this.loadGridView(); // Charge la GridView
        this.refreshGridView(); // Recharge la GridView

        this.checkIntent();



        m_s_Context = this;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_noelshack, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings:
                return true;
            case R.id.action_testHistory:
                ArrayList<UploadSave> array = HistoryManager.getHistory();
                Toast.makeText(Noelshack.this, String.valueOf(array.size()), Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_deleteHistory:
                HistoryManager.deleteHistory();
                this.refreshGridView();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void checkPermissions() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 51);
        }
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 52);
        }
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 53);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 51: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    System.exit(0);
                }
                return;
            }
            case 52: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    this.refreshGridView();
                } else {
                    System.exit(0);
                }
                return;
            }
            case 53: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    System.exit(0);
                }
                return;
            }
        }
    }

    private void loadViews() {
        this.m_FloatingButtonGallery = (FloatingActionButton) findViewById(R.id.selectPictureButton);
        this.m_FloatingButtonCamera = (FloatingActionButton) findViewById(R.id.cameraButton);

        this.m_FloatingButtonGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Picker.Builder(Noelshack.m_s_Context, new PickListener(), R.style.MIP_theme).build().startActivity();
            }

        });

        this.m_FloatingButtonCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                CameraCapture camera = new CameraCapture(Noelshack.m_s_Context);
                camera.takePhoto();
            }
        });
    }

    public void loadGridView() {
        /* Chargement de l'historique */
        ArrayList<UploadSave> history = HistoryManager.getHistoryReversed();
        if(history == null || history.isEmpty())
            history = new ArrayList<UploadSave>();

        /* Définition du gridview, "galerie" de l'historique */
        this.m_GridView = (GridView) findViewById(R.id.gridView);
        this.m_GridViewAdapter = new GridViewAdapter(this, R.layout.gridview_item, history);

        this.m_GridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                UploadSave item = (UploadSave) parent.getItemAtPosition(position);
                SOC.copyToClipboard(item.getUrl(), v.getContext());
            }
        });

        this.m_GridView.setAdapter(this.m_GridViewAdapter);
    }

    public void refreshGridView() {
        /* Chargement de l'historique */
        ArrayList<UploadSave> history = HistoryManager.getHistoryReversed();
        if(history == null || history.isEmpty())
            history = new ArrayList<UploadSave>();

        this.m_GridViewAdapter = new GridViewAdapter(this, R.layout.gridview_item, history);
        this.m_GridView.setAdapter(this.m_GridViewAdapter);
    }

    public void upload(ImageEntry image, Noelshack context) {
        Uploader uploader = new Uploader(image, context);
        uploader.execute();
    }

    public void upload(ArrayList<ImageEntry> images, Noelshack context) {
        for(ImageEntry image : images) {
            Uploader uploader = new Uploader(image, context);
            uploader.execute();
        }
    }

    /* Vérifie l'intent en cas d'upload d'image depuis la galerie */
    private void checkIntent() {

        /* Récupération des données de l'intent */
        Intent intent = this.getIntent();
        Bundle extras = intent.getExtras();
        String action = intent.getAction();

        if (Intent.ACTION_SEND_MULTIPLE.equals(action)) {
            if(extras.containsKey(Intent.EXTRA_STREAM)) {
                /* Récupération de la liste des emplacements des fichiers */
                ArrayList<ImageEntry> images = new ArrayList<ImageEntry>();
                ArrayList<Parcelable> parceArray = extras.getParcelableArrayList(Intent.EXTRA_STREAM);

                for(Parcelable parcel : parceArray) {
                    Uri uri = (Uri) parcel;
                    String path = this.getRealPathFromURI(uri, this);
                    if(path != null) {
                        images.add(new ImageEntry(new ImageEntry.Builder(path)));
                    }
                }

                this.upload(images, this);
            }
        } else if (Intent.ACTION_SEND.equals(action)) {
            if(extras.containsKey(Intent.EXTRA_STREAM)) {
                Uri uri = (Uri) extras.getParcelable(Intent.EXTRA_STREAM);
                String filename = Noelshack.getRealPathFromURI(uri, this);
                if(filename != null) {
                    Intent intent2 = new Intent(this, NoelshackBackgroundService.class);
                    intent2.setAction(Noelshack.NOELSHACK_UPLOAD_REDIRECTION);
                    intent2.putExtra(Noelshack.NOELSHACK_IMAGE_PATH, filename);
                    intent2.setType("text/plain");
                    this.startService(intent2);
                }
            }

            this.onBackPressed();
        }
    }

    private class PickListener implements Picker.PickListener {

        @Override
        public void onPickedSuccessfully(ArrayList<ImageEntry> images) {
            upload(images, Noelshack.m_s_Context);
        }

        @Override
        public void onCancel() {
            Toast.makeText(Noelshack.m_s_Context, "Cancel", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CameraCapture.REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            String picPath = getRealPathFromURI(Uri.parse(CameraCapture.m_CurrentPhotoPath), this);
            ImageEntry image = new ImageEntry(new ImageEntry.Builder(picPath));
            this.upload(image, this);
        }
    }
    
    public static String getRealPathFromURI(Uri contentURI, Context context) {
        Cursor cursor = context.getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            return contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(idx);
        }
    }
}

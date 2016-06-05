package com.coruscant11.noelshackimageupload;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.os.Bundle;

import com.coruscant11.noelshackimageupload.network.BackgroundUploader;

import net.yazeed44.imagepicker.model.ImageEntry;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class NoelshackBackgroundService extends IntentService {

    public NoelshackBackgroundService() {
        super("NoelshackBackgroundService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        if(intent != null) {
            String imagePath = extras.getString(Noelshack.NOELSHACK_IMAGE_PATH);
            ImageEntry image = new ImageEntry(new ImageEntry.Builder(imagePath));
            this.upload(image, this);
        }
    }

    public void upload(ImageEntry image, NoelshackBackgroundService backgroundService) {
        BackgroundUploader uploader = new BackgroundUploader(image, backgroundService);
        uploader.execute();
    }
}

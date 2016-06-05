package com.coruscant11.noelshackimageupload.network;

import com.coruscant11.noelshackimageupload.Noelshack;

import net.yazeed44.imagepicker.model.ImageEntry;

public class ActivityEndProcess extends UploadEndProcess {

    private Noelshack m_Noelshack;

    public ActivityEndProcess(Noelshack noelshack, String urlUploaded, ImageEntry image) {
        super(urlUploaded, image);

        this.m_Noelshack = noelshack;
    }

    @Override
    public void process() {
        this.saveToHistory();
        this.m_Noelshack.refreshGridView();
    }
}

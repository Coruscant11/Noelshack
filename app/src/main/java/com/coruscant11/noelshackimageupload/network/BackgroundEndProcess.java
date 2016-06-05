package com.coruscant11.noelshackimageupload.network;

import com.coruscant11.noelshackimageupload.NoelshackBackgroundService;
import com.coruscant11.noelshackimageupload.SOC;

import net.yazeed44.imagepicker.model.ImageEntry;

public class BackgroundEndProcess extends UploadEndProcess {

    private NoelshackBackgroundService m_Service;

    public BackgroundEndProcess(NoelshackBackgroundService service, String urlUploaded, ImageEntry image) {
        super(urlUploaded, image);

        this.m_Service = service;
    }

    @Override
    public void process() {
        this.saveToHistory();
        SOC.copyToClipboard(this.m_Url, this.m_Service);
    }
}

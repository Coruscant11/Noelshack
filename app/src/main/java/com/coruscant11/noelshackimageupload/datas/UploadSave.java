package com.coruscant11.noelshackimageupload.datas;

import net.yazeed44.imagepicker.model.ImageEntry;

import java.io.Serializable;

public class UploadSave implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 52775L;

    protected String m_Url; // URL Noelshack de l'image
    protected ImageEntry m_Image; // Location de l'image dans la sdcard

    public UploadSave(String url, ImageEntry image) {
        this.m_Url = url;
        this.m_Image = image;
    }

    public ImageEntry getImage() {
        return m_Image;
    }

    public void setImage(ImageEntry image) {
        this.m_Image = image;
    }

    public String getUrl() {
        return m_Url;
    }

    public void setUrl(String url) {
        this.m_Url = url;
    }
}

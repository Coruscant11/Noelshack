package com.coruscant11.noelshackimageupload.network;

import android.app.*;
import android.widget.*;
import android.os.*;
import android.util.*;

import com.coruscant11.noelshackimageupload.Noelshack;
import com.coruscant11.noelshackimageupload.datas.History;
import com.coruscant11.noelshackimageupload.datas.UploadSave;
import com.coruscant11.noelshackimageupload.managers.HistoryManager;

import net.yazeed44.imagepicker.model.ImageEntry;

import java.io.IOException;
import java.util.ArrayList;


public abstract class UploadEndProcess {

    protected String m_Url;
    protected ImageEntry m_Image;

    public UploadEndProcess(String urlUploaded, ImageEntry image) {
        this.m_Url = urlUploaded;
        this.m_Image = image;
    }

    protected void saveToHistory() {
        ArrayList<UploadSave> historyArray = HistoryManager.getHistory();
        UploadSave uploadSave = new UploadSave(m_Url, m_Image);

        historyArray.add(uploadSave);
        HistoryManager.saveHistory(historyArray);
    }

    protected abstract void process();
}

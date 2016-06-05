package com.coruscant11.noelshackimageupload.datas;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class History {

    private ArrayList history;
    private String url;

    public History(String historyFile) throws IOException {
        this.url = historyFile;
        File historyf = new File(historyFile);
        if(historyf.exists() == false) {
            historyf.createNewFile();
            history = new ArrayList<UploadSave>();
            saveHistory(history);
        }
    }

    public ArrayList<UploadSave> load() throws IOException, ClassNotFoundException {
        FileInputStream fis = new FileInputStream(url);
        ObjectInputStream ois = new ObjectInputStream(fis);
        history = (ArrayList<UploadSave>) ois.readObject();
        ois.close();
        fis.close();
        return history;
    }

    public void saveHistory(ArrayList<UploadSave> array) throws IOException {
        FileOutputStream fos = new FileOutputStream(url);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(array);
        oos.flush();
        fos.flush();
        oos.close();
        fos.close();
    }
}



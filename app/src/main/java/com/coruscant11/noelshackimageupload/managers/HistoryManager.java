package com.coruscant11.noelshackimageupload.managers;

import android.widget.Toast;

import com.coruscant11.noelshackimageupload.Noelshack;
import com.coruscant11.noelshackimageupload.datas.History;
import com.coruscant11.noelshackimageupload.datas.UploadSave;

import java.io.File;
import java.util.ArrayList;

public class HistoryManager {

    /* Fonction de supression de l'historique */
    public static void deleteHistory() {
        File file = new File(Noelshack.HISTORY_FILE); // Récupération du fichier de l'historique

        /* Si le fichier existe bel et bien, on le supprime */
        if(file.exists()) {
            file.delete();
        }
    }

    /* Sauvegarde l'historique */
    public static void saveHistory(ArrayList<UploadSave> array) {
        try {
            History history = new History(Noelshack.HISTORY_FILE);
            history.saveHistory(array);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    /* Chargement de l'historique */
    public static ArrayList<UploadSave> getHistory() {
        try {
            History history = new History(Noelshack.HISTORY_FILE);
            ArrayList<UploadSave> historyArray = history.load();
            return historyArray;
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /* Chargement de l'historique inversée */
    public static ArrayList<UploadSave> getHistoryReversed() {
        try {
            History history = new History(Noelshack.HISTORY_FILE);
            ArrayList<UploadSave> uploadSaves = history.load();
            return reverseHistory(uploadSaves);
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /* Inversement de l'historique */
    public static ArrayList<UploadSave> reverseHistory(ArrayList<UploadSave> liste) {
        ArrayList<UploadSave> result = new ArrayList<UploadSave>(); // ArrayList finale

        for(int i=liste.size()-1; i>=0; i--) { // On parcours l'ArrayList a l'envers
            result.add(liste.get(i)); // Et on ajoute a chaque fois l'�l�ment � L'ArrayList finale
        }

        return result; // On retourne le tout
    }


    /* Fonction retournant l'url de l'image stockée sur le téléphone a partir de l'URL de l'image sur noelshack */
    public String getPicturePathByUrl(String webUrl) {
        return null;
    }
}

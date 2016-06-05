package com.coruscant11.noelshackimageupload;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

public class SOC {

    public static void share(String textToShare, Context c) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "Picture's URL");
        intent.putExtra(Intent.EXTRA_TEXT, textToShare);
        c.startActivity(intent);
    }

    public static void copyToClipboard(String text, Context c) {
        ClipboardManager clipboard = (ClipboardManager)c.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Picture's URL", text);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(c, c.getString(R.string.copied), Toast.LENGTH_SHORT).show();
    }

    public static void openInNavigator(String url, Context c) {
        Intent intent = new Intent( Intent.ACTION_VIEW, Uri.parse(url) );
        c.startActivity(intent);
    }
}


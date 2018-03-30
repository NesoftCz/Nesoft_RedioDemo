package com.neusoft.radio;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

/**
 * @author xinjn
 *
 */
public class RadioUtils {
    public static final String AUTHORITY = "com.neusoft.sourcectrl";
    public static final String TAB_VOLUME_TABNAME = "volume_tab";
    public final static String LAST_APP_SOURCE = "LAST_APP_SOURCE";
    public static final String CURRENT_MEDIA_SOURCE = "CURRENT_MEDIA_SOURCE";
    public static final String RADIO_PACKAGE_NAME = "com.neusoft.radio";

    public static boolean isCurrentMediaSource(Context context) {
        String sourceName = "";
        String str = "content://" + AUTHORITY + "/" + TAB_VOLUME_TABNAME + "/1";
        Uri uri = Uri.parse(str);
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(uri, new String[] { CURRENT_MEDIA_SOURCE },
                null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            sourceName = cursor.getString(cursor.getColumnIndex(CURRENT_MEDIA_SOURCE));
        }

        return sourceName.equals(RADIO_PACKAGE_NAME);
    }

    public static void setCurrentMediaSource(Context context) {
        String str = "content://" + AUTHORITY + "/" + TAB_VOLUME_TABNAME + "/1";
        Uri uri = Uri.parse(str);
        ContentResolver resolver = context.getContentResolver();
        ContentValues values = new ContentValues();
        values.put(CURRENT_MEDIA_SOURCE, RADIO_PACKAGE_NAME);
        resolver.update(uri, values, null, null);
    }

    public static void responseVR(Context context, boolean isSuccess, long recevieTimer) {
        // 成功回复0；失败回复1
        int flag = isSuccess ? 0 : 1;
        Intent intent = new Intent("com.vrmms.intent.VRMMSMAIN");
        intent.putExtra("response", flag);
        intent.putExtra("timer", recevieTimer);
        intent.putExtra("pkg", "com.neusoft.radio");
        context.sendBroadcast(intent);
    }

    public static boolean isBandAndFreqAvail(String band, String freq) {
        boolean isBandAvail;
        boolean isFreqAvail;
        isBandAvail = band.equals("FM") || band.equals("AM");
        isFreqAvail = freq.equals("999999");
        if (band.equals("FM")) {
            Double fmFreq = Double.parseDouble(freq);
            if (isNumeric(freq) && fmFreq >= 87.5 && fmFreq <= 108) {
                isFreqAvail = true;
            }
        } else if (band.equals("AM")) {
            Double amFreq = Double.parseDouble(freq);
            if (isNumeric(freq) && amFreq >= 531 && amFreq <= 1602 && ((amFreq - 531) % 9 == 0)) {
                isFreqAvail = true;
            }
        }
        return isBandAvail && isFreqAvail;
    }

    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*.[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }

    public static void updateLastAppSource(Context context, String lastSourceName) {
        Log.d("CtlUtils", "updateLastAppSource E");
        String str = "content://" + AUTHORITY + "/" + TAB_VOLUME_TABNAME
                + "/1";
        Uri uri = Uri.parse(str);
        ContentResolver resolver = context.getContentResolver();
        ContentValues values = new ContentValues();
        values.put(LAST_APP_SOURCE, lastSourceName);
        resolver.update(uri, values, null, null);
    }

}

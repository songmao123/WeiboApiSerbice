package com.modong.service.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by 青松 on 2016/8/22.
 */
public class EmojiAssetDbHelper {

    private static final String DB_NAME = "emoji.db";

    private Context context;

    public EmojiAssetDbHelper(Context context) {
        this.context = context;
    }

    public SQLiteDatabase openDatabase() {
        File dbFile = context.getDatabasePath(DB_NAME);
        if (!dbFile.exists()) {
            try {
                SQLiteDatabase checkDB = context.openOrCreateDatabase(DB_NAME, context.MODE_PRIVATE, null);
                if(checkDB != null) {
                    checkDB.close();
                }
                copyDatabase(dbFile);
            } catch (IOException e) {
                throw new RuntimeException("Error creating source database", e);
            }
        }
        return SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READONLY);
    }

    private void copyDatabase(File dbFile) throws IOException {
        InputStream is = context.getAssets().open(DB_NAME);
        OutputStream os = new FileOutputStream(dbFile);

        byte[] buffer = new byte[1024];
        while (is.read(buffer) > 0) {
            os.write(buffer);
        }
        os.flush();
        os.close();
        is.close();
    }

    public synchronized byte[] getEmojiBlob(String key) {
        long startTime = System.currentTimeMillis();
        byte[] bytes = null;
        SQLiteDatabase database = this.openDatabase();
        Cursor cursor = null;
        try {
            cursor = database.rawQuery("select * from emoji where phrase = ?", new String[]{key});
            if (cursor != null && cursor.moveToNext()) {
                bytes = cursor.getBlob(cursor.getColumnIndex(WeiboDbConstants.COLUMN_PICBYTES));
            }
            Log.i("sqsong", "Query Emoji Cost Time: " + (System.currentTimeMillis() - startTime) + "ms");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            database.close();
        }
        return bytes;
    }

}

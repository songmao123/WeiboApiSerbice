package com.modong.service.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.modong.service.model.Emotion;
import com.modong.service.model.EmotionItem;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 青松 on 2016/8/22.
 */
public class EmojiAssetDbHelper {

    public static final String DB_EMOTION = "emotion.db";

    private Context context;

    public EmojiAssetDbHelper(Context context) {
        this.context = context;
    }

    public SQLiteDatabase openDatabase(String dbName) {
        File dbFile = context.getDatabasePath(dbName);
        if (!dbFile.exists()) {
            try {
                SQLiteDatabase checkDB = context.openOrCreateDatabase(dbName, context.MODE_PRIVATE, null);
                if(checkDB != null) {
                    checkDB.close();
                }
                copyDatabase(dbFile, dbName);
            } catch (IOException e) {
                throw new RuntimeException("Error creating source database", e);
            }
        }
        return SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READONLY);
    }

    private void copyDatabase(File dbFile, String dbName) throws IOException {
        InputStream is = context.getAssets().open(dbName);
        OutputStream os = new FileOutputStream(dbFile);

        byte[] buffer = new byte[1024];
        while (is.read(buffer) > 0) {
            os.write(buffer);
        }
        os.flush();
        os.close();
        is.close();
    }

    public synchronized List<EmotionItem> queryEmotionList(String dbName) {
        long startTime = System.currentTimeMillis();
        List<EmotionItem> itemList = new ArrayList<>();
        SQLiteDatabase database = this.openDatabase(dbName);
        Cursor cursor = null;
        try {
            cursor = database.query(WeiboDbConstants.TABLE_NAME_EMOTION, null, null, null, null, null, null);
            Map<String, List<Emotion>> map = new LinkedHashMap<>();
            List<String> descriptions = new ArrayList<>();
            while (cursor != null && cursor.moveToNext()) {
                String category = cursor.getString(cursor.getColumnIndex(WeiboDbConstants.COLUMN_CATEGORY));
                String description = cursor.getString(cursor.getColumnIndex(WeiboDbConstants.COLUMN_DESCRIPTION));
                String name = cursor.getString(cursor.getColumnIndex(WeiboDbConstants.COLUMN_NAME));
                String value = cursor.getString(cursor.getColumnIndex(WeiboDbConstants.COLUMN_VALUE));

                Emotion emotion = new Emotion();
                emotion.setCategory(category);
                emotion.setDescription(description);
                emotion.setName(name);
                emotion.setValue(value);

                List<Emotion> emotions = map.get(description);
                if (emotions == null) {
                    emotions = new ArrayList<>();
                    emotions.add(emotion);
                    descriptions.add(description);
                    map.put(description, emotions);
                } else {
                    emotions.add(emotion);
                }
            }

            for (String description : descriptions) {
                EmotionItem emotionItem = new EmotionItem();
                emotionItem.setCategory(description);
                emotionItem.setEmotionList(map.get(description));
                itemList.add(emotionItem);
            }
            Log.i("sqsong", "Query Emotion Items Cost Time: " + (System.currentTimeMillis() - startTime) + "ms");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            database.close();
        }
        return itemList;
    }

    public synchronized String queryEmotionValue(String dbName, String name) {
        String value = name;
        SQLiteDatabase database = this.openDatabase(dbName);
        Cursor cursor = null;
        try {
            cursor = database.rawQuery("select * from emotion where name = ?", new String[]{name});
            if (cursor != null && cursor.moveToNext()) {
                value = cursor.getString(cursor.getColumnIndex(WeiboDbConstants.COLUMN_VALUE));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            database.close();
        }
        return value;
    }

}

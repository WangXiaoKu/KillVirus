package androidstudio.killvirus;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Writer : 王苦苦
 * <p/>
 * Copyright : 王宏伟
 * <p/>
 * Describe :
 * <p/>
 * Created : 2015 ; 18701 ; 2015/12/4.
 */
public class VirusDB {
    /**
     * 检查当前的MD5值是否和病毒数据库中的病毒特征码匹配
     *
     * @param appMd5
     * @return
     */
    public static String checkVirus(String appMd5) {
        String desc = null;

        SQLiteDatabase db = SQLiteDatabase.openDatabase("/data/data/androidstudio.killvirus/files/antivirus.db", null, SQLiteDatabase.OPEN_READONLY);

        Cursor cursor = db.rawQuery("select desc from datable where md5 = ?", new String[]{appMd5});
//        Cursor cursor = db.query("antivirus.db", new String[]{"desc"}, "md5=?", new String[]{appMd5}, null, null, null);
        if (cursor.moveToNext()) {
            desc = cursor.getString(cursor.getColumnIndex("desc"));
            Log.i("Virus", desc);
        }

        return desc;
    }
}

package com.example.myapplication

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log


class DbHelper(context: Context) : SQLiteOpenHelper(context, DbHelper.DATABASE_NAME, null, DbHelper.DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(DATABASE_CREATE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        Log.w(TAG, "Upgrade from version $oldVersion to $newVersion")

        if (oldVersion <  2) {
            clearDb(db)
            return
        }

        if (oldVersion < 3) {
            upgradeVersion3(db)
        }
    }

    private fun upgradeVersion2(db: SQLiteDatabase) {
        db.execSQL("ALTER TABLE $DATABASE_TABLE ADD COLUMN $KEY_VALUE INTEGER DEFAULT 0;")
        val resultColumns = arrayOf(KEY_ID, KEY_NAME, KEY_VALUE)

        val cursor = db.query(DATABASE_TABLE, resultColumns, null, null, null, null, null, null)
        if (cursor != null) {
            var hasItem = cursor.moveToFirst()
            while (hasItem) {
                val id = cursor.getInt(cursor.getColumnIndex(KEY_ID))
                val nameStr = cursor.getString(cursor.getColumnIndex(KEY_NAME))
                val values = ContentValues()
                values.put(KEY_VALUE, nameStr.length)
                db.update(DATABASE_TABLE, values, "$KEY_ID=?", arrayOf(id.toString()))
                hasItem = cursor.moveToNext()
            }
            cursor.close()
        }
    }

    private fun upgradeVersion3(db: SQLiteDatabase) {
        db.execSQL("ALTER TABLE $DATABASE_TABLE ADD COLUMN $KEY_EVEN BOOLEAN DEFAULT 0;")
        val resultColumns = arrayOf(KEY_ID, KEY_NAME, KEY_VALUE, KEY_EVEN)

        val cursor = db.query(DATABASE_TABLE, resultColumns, null, null, null, null, null, null)
        if (cursor != null) {
            var hasItem = cursor.moveToFirst()
            while (hasItem) {
                val id = cursor.getInt(cursor.getColumnIndex(KEY_ID))
                val valueInt = cursor.getInt(cursor.getColumnIndex(KEY_VALUE))
                val values = ContentValues()
                values.put(KEY_EVEN, (valueInt % 2 == 0))
                db.update(DATABASE_TABLE, values, "$KEY_ID=?", arrayOf(id.toString()))
                hasItem = cursor.moveToNext()
            }
            cursor.close()
        }
    }

    fun clearDb(db: SQLiteDatabase = writableDatabase) {
        db.execSQL("DROP TABLE IF EXISTS $DATABASE_TABLE")
        onCreate(db)
    }

    companion object {

        const val KEY_ID = "_ID"
        const val KEY_NAME = "NAME"
        const val KEY_VALUE = "VALUE"
        const val KEY_EVEN = "EVEN"
        const val DATABASE_TABLE = "simpletable"

        val RESULT_COLUMNS = arrayOf(KEY_ID, KEY_NAME, KEY_VALUE, KEY_EVEN)

        private val TAG = DbHelper::class.java.simpleName

        private const val DATABASE_NAME = "simpledatabase.sqlite"
        private const val DATABASE_VERSION = 3

        private const val DATABASE_CREATE =
            "CREATE TABLE $DATABASE_TABLE ($KEY_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "$KEY_NAME TEXT NOT NULL, $KEY_VALUE INTEGER DEFAULT 0, " +
                    "$KEY_EVEN BOOLEAN DEFAULT 0);"
    }
}

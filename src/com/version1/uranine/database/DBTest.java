package com.version1.uranine.database;

import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import com.version1.uranine.database.DBOpenHelper;

public class DBTest extends AndroidTestCase {

	
	public void testCreateDatabase() {
		DBOpenHelper helper = new DBOpenHelper(getContext());		// Activity getApplicationContext()
		@SuppressWarnings("unused")
		SQLiteDatabase db = helper.getWritableDatabase();			// 

	}

}

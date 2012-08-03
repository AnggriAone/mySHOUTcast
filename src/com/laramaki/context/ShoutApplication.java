package com.laramaki.context;

import org.orman.dbms.Database;
import org.orman.dbms.sqliteandroid.SQLiteAndroid;
import org.orman.mapper.MappingSession;

import com.laramaki.model.Favorite;
import com.laramaki.model.Station;

import android.app.Application;

public class ShoutApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		Database db = new SQLiteAndroid(this, "shoutcastme.db");
		registerEntities();
		MappingSession.registerDatabase(db);
		MappingSession.start();
	}
	
	private void registerEntities() {
		MappingSession.registerEntity(Station.class);
		MappingSession.registerEntity(Favorite.class);
	}
}

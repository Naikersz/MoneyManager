package com.example.moneymanager;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {StoredValue.class}, version = 1, exportSchema = false)
public abstract class MoneyDatabase extends RoomDatabase {
    private static volatile MoneyDatabase instance;

    public abstract MoneyDao moneyDao();

    public static MoneyDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (MoneyDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            MoneyDatabase.class,
                            "money-manager.db"
                        )
                        .allowMainThreadQueries()
                        .build();
                }
            }
        }
        return instance;
    }
}

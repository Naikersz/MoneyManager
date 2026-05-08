package com.example.moneymanager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface MoneyDao {
    @Query("SELECT value FROM app_data WHERE data_key = :key LIMIT 1")
    @Nullable
    String getValue(@NonNull String key);

    @Query("SELECT COUNT(*) FROM app_data")
    int count();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void put(StoredValue value);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void putAll(List<StoredValue> values);
}

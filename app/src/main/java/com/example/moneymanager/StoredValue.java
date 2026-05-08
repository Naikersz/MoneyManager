package com.example.moneymanager;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "app_data")
public class StoredValue {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "data_key")
    public String key;

    @NonNull
    @ColumnInfo(name = "value")
    public String value;

    @ColumnInfo(name = "updated_at")
    public long updatedAt;

    public StoredValue() {
        this.key = "";
        this.value = "";
        this.updatedAt = 0L;
    }

    @Ignore
    public StoredValue(@NonNull String key, @NonNull String value, long updatedAt) {
        this.key = key;
        this.value = value;
        this.updatedAt = updatedAt;
    }
}

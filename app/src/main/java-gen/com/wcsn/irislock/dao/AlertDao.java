package com.wcsn.irislock.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import com.wcsn.irislock.bean.Alert;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "ALERT".
*/
public class AlertDao extends AbstractDao<Alert, Long> {

    public static final String TABLENAME = "ALERT";

    /**
     * Properties of entity Alert.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Type = new Property(1, int.class, "type", false, "TYPE");
        public final static Property AlertInfo = new Property(2, String.class, "alertInfo", false, "ALERT_INFO");
        public final static Property Time = new Property(3, String.class, "time", false, "TIME");
        public final static Property Week = new Property(4, String.class, "week", false, "WEEK");
        public final static Property AlertImage = new Property(5, String.class, "alertImage", false, "ALERT_IMAGE");
    };


    public AlertDao(DaoConfig config) {
        super(config);
    }
    
    public AlertDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"ALERT\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"TYPE\" INTEGER NOT NULL ," + // 1: type
                "\"ALERT_INFO\" TEXT NOT NULL ," + // 2: alertInfo
                "\"TIME\" TEXT NOT NULL ," + // 3: time
                "\"WEEK\" TEXT NOT NULL ," + // 4: week
                "\"ALERT_IMAGE\" TEXT);"); // 5: alertImage
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"ALERT\"";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, Alert entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindLong(2, entity.getType());
        stmt.bindString(3, entity.getAlertInfo());
        stmt.bindString(4, entity.getTime());
        stmt.bindString(5, entity.getWeek());
 
        String alertImage = entity.getAlertImage();
        if (alertImage != null) {
            stmt.bindString(6, alertImage);
        }
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public Alert readEntity(Cursor cursor, int offset) {
        Alert entity = new Alert( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getInt(offset + 1), // type
            cursor.getString(offset + 2), // alertInfo
            cursor.getString(offset + 3), // time
            cursor.getString(offset + 4), // week
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5) // alertImage
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, Alert entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setType(cursor.getInt(offset + 1));
        entity.setAlertInfo(cursor.getString(offset + 2));
        entity.setTime(cursor.getString(offset + 3));
        entity.setWeek(cursor.getString(offset + 4));
        entity.setAlertImage(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(Alert entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(Alert entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
}

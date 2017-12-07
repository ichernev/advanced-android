package com.exercise.security;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

/**
 * @author Philip
 * @since 2017-12-07
 */

public class SimpleProvider extends ContentProvider {

    private static final String AUTHORITY = "com.exercise.security.data.SimpleProvider";
    public static final int LINKS = 100;
    public static final int LINK_ID = 110;

    private static final String BASE_PATH = "links";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH);

    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/link";
    public static final String CONTENT_TABLE_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/links";

    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sURIMatcher.addURI(AUTHORITY, BASE_PATH, LINKS);
        sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/#", LINK_ID);
    }

    private SimpleDatabase mDB;

    @Override
    public boolean onCreate() {
        mDB = new SimpleDatabase(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(SimpleDatabase.TABLE_LINKS);

        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            case LINK_ID:
                queryBuilder.appendWhere(SimpleDatabase.ID + "=" + uri.getLastPathSegment());
                break;
            case LINKS:
                // no filter
                break;
            default:
                throw new IllegalArgumentException("Unknown URI");
        }

        Cursor cursor = queryBuilder.query(mDB.getReadableDatabase(), projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        switch (sURIMatcher.match(uri)){
            case LINKS:
                return CONTENT_TABLE_TYPE;
            case LINK_ID:
                return CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        SQLiteDatabase sqlDB = mDB.getWritableDatabase();
        long rowID = sqlDB.insert(SimpleDatabase.TABLE_LINKS, null, values);
        sqlDB.close();

        return ContentUris.withAppendedId(CONTENT_URI, rowID);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = mDB.getWritableDatabase();
        int rowsAffected = 0;
        switch (uriType) {
            case LINKS:
                rowsAffected = sqlDB.delete(SimpleDatabase.TABLE_LINKS, selection, selectionArgs);
                break;
            case LINK_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsAffected = sqlDB.delete(SimpleDatabase.TABLE_LINKS, SimpleDatabase.ID + "=" + id, null);
                } else {
                    rowsAffected = sqlDB.delete(SimpleDatabase.TABLE_LINKS, selection + " and " + SimpleDatabase.ID + "=" + id, selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown or Invalid URI " + uri);
        }
        sqlDB.close();

        getContext().getContentResolver().notifyChange(uri, null);
        return rowsAffected;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        int count = 0;
        SQLiteDatabase sqlDB = mDB.getWritableDatabase();
        switch (sURIMatcher.match(uri)) {
            case LINKS:
                count = sqlDB.update(SimpleDatabase.TABLE_LINKS, values, selection, selectionArgs);
                break;

            case LINK_ID:
                count = sqlDB.update(SimpleDatabase.TABLE_LINKS, values,
                        SimpleDatabase.ID + " = " + uri.getPathSegments().get(1) +
                                (!TextUtils.isEmpty(selection) ? " AND (" +selection + ')' : ""), selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri );
        }

        sqlDB.close();

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }
}

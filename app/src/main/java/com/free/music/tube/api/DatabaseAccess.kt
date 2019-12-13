package com.free.music.tube.api

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import android.widget.Toast
import com.free.music.tube.models.Artist
import com.free.music.tube.models.IModel
import com.free.music.tube.models.Track
import java.util.*

class DatabaseAccess(private val mContext: Context) {
    private val mDBHelper: DatabaseHelper = DatabaseHelper(mContext)
    private var mDb: SQLiteDatabase? = null

    // search tracks from the database.
    fun searchTracks(query: String, responseListener: ResponseListener<Track>) {
        val result: MutableList<Track> = ArrayList()

        try {
            mDBHelper.updateDataBase()
            mDb = mDBHelper.writableDatabase
            try {
                mDb!!.rawQuery("SELECT * FROM raw_tracks WHERE track_title LIKE '%" + Uri.encode(query) + "%'  LIMIT 75", null).use { cursor ->
                    if (cursor != null && cursor.moveToFirst()) {
                        do {
                            result.add(Track(cursor.getInt(0),
                                    cursor.getInt(1),
                                    cursor.getString(2),
                                    cursor.getString(3),
                                    cursor.getString(4),
                                    cursor.getString(5),
                                    cursor.getString(6),
                                    cursor.getString(7),
                                    "https://freemusicarchive.blob.core.windows.net/storage-freemusicarchive-org/" + cursor.getString(8),
                                    "https://freemusicarchive.blob.core.windows.net/storage-freemusicarchive-org/" + cursor.getString(9).replace("https://freemusicarchive.org/file/", ""),
                                    cursor.getString(10),
                                    cursor.getString(11)))
                        } while (cursor.moveToNext())
                        cursor.close()
                    }
                    if (result.isEmpty()) {
                        searchTracksIfNoResults(query, responseListener)
                        responseListener.onSuccessResult(result)
                    } else {
                        responseListener.onSuccessResult(result)
                    }
                }
            } finally {
                mDb!!.close()
            }
        } catch (e: Exception) {
            Toast.makeText(
                mContext, "Something went wrong, please stay with us, we are working.", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    private fun searchTracksIfNoResults(query: String, responseListener: ResponseListener<Track>) {
        val result: MutableList<Track> = ArrayList()

        try {
            mDBHelper.updateDataBase()
            mDb = mDBHelper.writableDatabase
            var cursor = mDb!!.rawQuery("SELECT * FROM raw_tracks WHERE track_title LIKE '%" + Uri.encode(query) + "%'", null)

            try {
                val ch = Uri.encode(query)[(Math.random() * query.length).toInt()]
                val selectQueryCh = "SELECT * FROM raw_tracks WHERE track_title LIKE '%$ch%' LIMIT 75"
                cursor = mDb!!.rawQuery(selectQueryCh, null)
                if (cursor != null && cursor.moveToFirst()) {
                    do {
                        result.add(Track(cursor.getInt(0),
                                cursor.getInt(1),
                                cursor.getString(2),
                                cursor.getString(3),
                                cursor.getString(4),
                                cursor.getString(5),
                                cursor.getString(6),
                                cursor.getString(7),
                                "https://freemusicarchive.blob.core.windows.net/storage-freemusicarchive-org/" + cursor.getString(8),
                                "https://freemusicarchive.blob.core.windows.net/storage-freemusicarchive-org/" + cursor.getString(9).replace("https://freemusicarchive.org/file/", ""),
                                cursor.getString(10),
                                cursor.getString(11)))
                    } while (cursor.moveToNext())
                    cursor.close()
                }
                responseListener.onSuccessResult(result)
            } finally {
                cursor?.close()
                mDb!!.close()
            }
        } catch (e: Exception) {
            Toast.makeText(mContext, "Something went wrong, please stay with us, we are working.", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    // search tracks from the database.
    fun showsTracks(responseListener: ResponseListener<Track>) { val result: MutableList<Track> = ArrayList()
        try {
            mDBHelper.updateDataBase()
            mDb = mDBHelper.writableDatabase
            try {
                mDb!!.rawQuery("SELECT * FROM raw_tracks ORDER BY random() LIMIT 150", null).use { cursor ->
                        if (cursor != null && cursor.moveToFirst()) {
                            do {
                                result.add(Track(cursor.getInt(0),
                                        cursor.getInt(1),
                                        cursor.getString(2),
                                        cursor.getString(3),
                                        cursor.getString(4),
                                        cursor.getString(5),
                                        cursor.getString(6),
                                        cursor.getString(7),
                                        "https://freemusicarchive.blob.core.windows.net/storage-freemusicarchive-org/" + cursor.getString(8),
                                        "https://freemusicarchive.blob.core.windows.net/storage-freemusicarchive-org/" + cursor.getString(9).replace("https://freemusicarchive.org/file/", ""),
                                        cursor.getString(10),
                                        cursor.getString(11)))
                            } while (cursor.moveToNext())
                            cursor.close()
                            if (result.isNotEmpty()) {
                                responseListener.onSuccessResult(result)
                            } else {
                                responseListener.onError()
                            }
                        }
                    }
            } finally {
                mDb!!.close()
            }
        } catch (e: Exception) {
            Toast.makeText(mContext, "Something went wrong, please stay with us, we are working.", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    // show artists from the database.
    fun showArtists(responseListener: ResponseListener<Artist>) {
        val result: MutableList<Artist> = ArrayList()
        var image = ""
        try {
            mDBHelper.updateDataBase()
            mDb = mDBHelper.writableDatabase
            try {
                mDb!!.rawQuery("SELECT * FROM raw_artists ORDER BY random() LIMIT 150", null)
                    .use { cursor ->
                        if (cursor != null && cursor.moveToFirst()) {
                            do {
                                if (cursor.getString(1).contains("file")) {
                                    image = "https://freemusicarchive.blob.core.windows.net/storage-freemusicarchive-org/" + cursor.getString(1).replace("https://freemusicarchive.org/file/", "")
                                } else if (cursor.getString(1).contains("img")) {
                                    image = "https://freemusicarchive.blob.core.windows.net/storage-freemusicarchive-org/img/" + cursor.getString(1).replace("https://freemusicarchive.org/img/", "")
                                }
                                if (!image.contains("album-default-live")) {
                                    result.add(Artist(cursor.getInt(0),
                                            image,
                                            cursor.getString(2),
                                            cursor.getString(3),
                                            cursor.getString(4),
                                            cursor.getString(5)))
                                }
                            } while (cursor.moveToNext())
                            cursor.close()
                        }
                        if (result.isNotEmpty()) {
                            responseListener.onSuccessResult(result)
                        } else {
                            responseListener.onError()
                        }
                    }
            } finally {
                mDb!!.close()
            }
        } catch (e: Exception) {
            Toast.makeText(mContext, "Something went wrong, please stay with us, we are working.", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    // show artist songs from the database.
    fun showArtistTracks(artist: Artist, responseListener: ResponseListener<Track>) {
        val result: MutableList<Track> = ArrayList()

        try {
            mDBHelper.updateDataBase()
            mDb = mDBHelper.writableDatabase
            try {
                mDb!!.rawQuery("SELECT * FROM raw_tracks WHERE artist_id=" + artist.id, null).use { cursor ->
                        if (cursor != null && cursor.moveToFirst()) {
                            do {
                                result.add(Track(cursor.getInt(0),
                                        cursor.getInt(1),
                                        cursor.getString(2),
                                        cursor.getString(3),
                                        cursor.getString(4),
                                        cursor.getString(5),
                                        cursor.getString(6),
                                        cursor.getString(7),
                                        "https://freemusicarchive.blob.core.windows.net/storage-freemusicarchive-org/" + cursor.getString(8),
                                        "https://freemusicarchive.blob.core.windows.net/storage-freemusicarchive-org/" + cursor.getString(9).replace("https://freemusicarchive.org/file/", ""),
                                        cursor.getString(10),
                                        cursor.getString(11)))
                            } while (cursor.moveToNext())
                            cursor.close()
                        }
                        if (result.isNotEmpty()) {
                            responseListener.onSuccessResult(result)
                        } else {
                            responseListener.onError()
                        }
                    }
            } finally {
                mDb!!.close()
            }
        } catch (e: Exception) {
            Toast.makeText(mContext, "Something went wrong, please stay with us, we are working.", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    interface ResponseListener<T : IModel?> {
        fun onSuccessResult(dataSet: List<T>?)
        fun onError()
    }
}
package com.free.music.tube.api

import android.content.ContentResolver
import android.provider.MediaStore
import com.free.music.tube.models.Song
import java.util.ArrayList

object SongHandler{

    fun getAllSongs(contentResolver: ContentResolver) : List<Song> {

        val dataSet = ArrayList<Song>()
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val selection = MediaStore.Audio.Media.IS_MUSIC + "<> ?"
        val selections = arrayOf("0")

        val cursor = contentResolver.query(uri, null, selection, selections, MediaStore.Audio.Media.DEFAULT_SORT_ORDER)

        if(cursor != null && cursor.moveToFirst()) {
            val id = cursor.getColumnIndex(MediaStore.Audio.Media._ID)
            val title = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
            val artist = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
            val data = cursor.getColumnIndex(MediaStore.Audio.Media.DATA)
            val duration = cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)
            val album = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)
            val albumId = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)

            do {
                val song = Song(cursor.getInt(id))

                song.title = cursor.getString(title)
                song.artist = cursor.getString(artist)
                song.data = cursor.getString(data)
                song.duration = cursor.getInt(duration)
                song.album = cursor.getString(album)
                song.albumId = cursor.getInt(albumId)

                dataSet.add(song)
            } while (cursor.moveToNext())
            cursor.close()
        }

        return dataSet
    }
}
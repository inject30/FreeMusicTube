package com.free.music.tube.models

import android.content.ContentUris
import android.net.Uri
import java.io.Serializable

class Song(var id: Int) : IModel, Serializable {

    var title: String = ""
    var artist: String = ""
    var data: String = ""
    var durationString: String = ""
    var album: String = ""
    var albumArt: String = ""

    var duration: Int = 0
        set(duration) {
            field = duration
            val sec = duration / 1000
            this.durationString = "" + sec / 60 + ":" + (if (sec % 60 < 10) "0" else "") + sec % 60
        }

    var albumId: Int = 0
        set(albumId) {
            field = albumId
            val albumArtUri =
                ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), albumId.toLong())
            this.albumArt = albumArtUri.toString()
        }
}
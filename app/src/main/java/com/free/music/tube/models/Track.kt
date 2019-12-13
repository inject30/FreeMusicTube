package com.free.music.tube.models

import java.io.Serializable

data class Track(var trackId: Int,
                 var artistId: Int,
                 var artistName: String,
                 var artistUrl: String,
                 var artistWebsite: String,
                 var licenseImageFile: String,
                 var licenseUrl: String,
                 var trackDuration: String,
                 var trackFile: String,
                 var trackImageFile: String,
                 var trackTitle: String,
                 var trackUrl: String) : IModel, Serializable
package com.free.music.tube.models

import java.io.Serializable

data class Artist(
    var id: Int,
    var imageFile: String,
    var name: String?,
    var url: String?,
    var website: String?,
    var wikipedia: String?) : IModel, Serializable

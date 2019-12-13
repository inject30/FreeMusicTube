package com.free.music.tube.player

import android.app.DownloadManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Environment
import android.os.Handler
import android.view.View
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.squareup.picasso.Picasso
import com.free.music.tube.R
import com.free.music.tube.activities.PermissionActivity
import com.free.music.tube.models.IModel
import com.free.music.tube.models.Song
import com.free.music.tube.models.Track
import com.free.music.tube.permissions.PermissionCheckerImpl
import com.free.music.tube.permissions.requestWriteExternalStoragePermission
import com.free.music.tube.utils.FileUtils
import java.io.File
import java.io.IOException

class AppPlayer(context: Context, iModel: IModel) :
    SeekBar.OnSeekBarChangeListener,
    MediaPlayer.OnPreparedListener,
    MediaPlayer.OnCompletionListener,
    MediaPlayer.OnErrorListener,
    MediaPlayer.OnBufferingUpdateListener,
    AudioManager.OnAudioFocusChangeListener,
    DialogInterface.OnDismissListener,
    DialogInterface.OnCancelListener {

    private lateinit var permissions: PermissionCheckerImpl

    private val mContext: Context = context
    private var mIModel: IModel = iModel
    private lateinit var mSong: Song
    private lateinit var mTrack: Track

    private val mMaterialDialog: MaterialDialog

    private var mMediaPlayer: MediaPlayer? = null
    private var mAudioManager: AudioManager? = null
    private var mSeekBar: SeekBar? = null

    private var mPlayButton: ImageView
    private var mAlbumCover: ImageView
    private var mTrackTitle: TextView
    private var mTrackArtist: TextView
    private var mDownloadView: TextView


    private var mPaused = false
    private var mPrepared = false

    private val mHandler = Handler()

    private val mRunnable = object : Runnable {
        override fun run() {
            if (mSeekBar != null && mMediaPlayer != null) {
                val seekBar = mSeekBar
                val currentPosition = mMediaPlayer!!.currentPosition.toDouble()
                java.lang.Double.isNaN(currentPosition)
                val d3 = currentPosition * 100.0

                val duration = mMediaPlayer!!.duration.toDouble()
                java.lang.Double.isNaN(duration)
                seekBar!!.progress = (d3 / duration).toInt()
                mHandler.postDelayed(this as Runnable, 1000L)
            }
        }
    }

    init {
        mIModel = iModel

        this.mMaterialDialog = MaterialDialog(mContext).show {
            customView(R.layout.player)
        }

        permissions = PermissionCheckerImpl(mContext)

        mAudioManager = mMaterialDialog.view.context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        mAudioManager!!.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN)
        mAlbumCover = mMaterialDialog.view.findViewById(R.id.player_album_cover)
        mTrackTitle = mMaterialDialog.view.findViewById(R.id.player_track_title)
        mTrackArtist = mMaterialDialog.view.findViewById(R.id.player_track_artist)
        mSeekBar = mMaterialDialog.view.findViewById(R.id.player_seek_bar)
        mPlayButton = mMaterialDialog.view.findViewById(R.id.player_play_button)
        mDownloadView = mMaterialDialog.view.findViewById(R.id.player_download_button)
        val licenseImage = mMaterialDialog.view.findViewById<ImageView>(R.id.license_image)

        if (mIModel is Track) {
            mTrack = mIModel as Track

            mTrackTitle.text = mTrack.trackTitle
            mTrackArtist.text = mTrack.artistName

            if (mTrack.trackImageFile.isNotEmpty()) {
                Picasso.with(mMaterialDialog.view.context)
                    .load(mTrack.trackImageFile)
                    .placeholder(R.drawable.default_track_art)
                    .error(R.drawable.default_track_art)
                    .into(mAlbumCover)
            }

            if (mTrack.licenseImageFile.isNotEmpty()) {
                val licenseImageFile = mTrack.licenseImageFile.replace("http", "https")

                Picasso.with(mMaterialDialog.view.context)
                    .load(licenseImageFile)
                    .into(licenseImage)
            }

            licenseImage.setOnClickListener {
                if (mTrack.licenseUrl.isEmpty()) {
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(mTrack.licenseUrl))
                    mMaterialDialog.view.context.startActivity(browserIntent)
                } else {
                    Toast.makeText(mMaterialDialog.view.context, R.string.unknown_license, Toast.LENGTH_LONG).show()
                }
            }

            mDownloadView.setOnClickListener {
                if (permissions.canWriteToExternalStorage) {
                    download(mMaterialDialog.view.context)
                }  else  (mContext as PermissionActivity).requestWriteExternalStoragePermission()
            }

        } else if (mIModel is Song) {
            mDownloadView.visibility = View.GONE
            licenseImage.visibility = View.GONE

            mSong = mIModel as Song

            mTrackTitle.text = mSong.title
            mTrackArtist.text = mSong.artist

            if (mSong.albumArt.isNotEmpty()) {
                Picasso.with(mMaterialDialog.view.context)
                    .load(mSong.albumArt)
                    .placeholder(R.drawable.default_track_art)
                    .error(R.drawable.default_track_art)
                    .into(mAlbumCover)
            }
        }

        mSeekBar!!.max = 100
        mSeekBar!!.progress = 0
        mSeekBar!!.secondaryProgress = 0
        mSeekBar!!.isEnabled = false
        mSeekBar!!.setOnSeekBarChangeListener(this)

        mPlayButton.setOnClickListener {
            when {
                mPlayButton.isSelected -> pause()
                mMediaPlayer == null -> play()
                else -> resume()
            }
        }

        progressHandler()

        mMaterialDialog.setOnDismissListener(this)
        mMaterialDialog.setOnCancelListener(this)
    }

    private fun progressHandler() {
        mHandler.postDelayed(mRunnable, 100L)
    }

    fun create() {
        mAudioManager = mContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager

        if (mAudioManager != null) {
            mAudioManager!!.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN)
        }

        mMaterialDialog.show()

        (mContext as PermissionActivity).createBothPlayer(this)
    }

    private fun pause() {
        mPaused = true
        mSeekBar!!.isEnabled = false
        mPlayButton.isSelected = false

        if (mPaused && mMediaPlayer!!.isPlaying) {
            mPlayButton.setBackgroundResource(R.drawable.ic_play_black_24dp)
            mMediaPlayer!!.pause()
        }
    }

    private fun resume() {
        mPaused = false
        mPlayButton.isSelected = true

        if (mPrepared) {
            mPlayButton.setBackgroundResource(R.drawable.ic_pause_black_24dp)
            mMediaPlayer!!.start()
            mSeekBar!!.isEnabled = true
            progressHandler()
        }
    }

    private fun play() {
        mPlayButton.isSelected = true

        var url: String? = null

        if (mMediaPlayer == null) {
            mPlayButton.setBackgroundResource(R.drawable.ic_pause_black_24dp)
            initializeMediaPlayer()
        }

        if (mIModel is Track) {
            url = mTrack.trackFile
        } else if (mIModel is Song) {
            url = mSong.data
        }

        try {
            mMediaPlayer!!.setDataSource(url)
            mMediaPlayer!!.prepareAsync()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun initializeMediaPlayer() {
        mMediaPlayer = MediaPlayer()
        mMediaPlayer!!.setAudioStreamType(AudioManager.STREAM_MUSIC)
        mMediaPlayer!!.setOnPreparedListener(this)
        mMediaPlayer!!.setOnCompletionListener(this)
        mMediaPlayer!!.setOnErrorListener(this)
        mMediaPlayer!!.setOnBufferingUpdateListener(this)
    }

    // TODO  Cannot mount sd-card
    fun download(context: Context) {
        val mDownloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

        if (!FileUtils.isStorageCanWrite()) {
            Toast.makeText(context, "Cannot mount sd-card", Toast.LENGTH_LONG).show()
            return
        }

        //val directory = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC), "Free Music Downloader")
        val directory = File(context.getExternalFilesDir(Environment.DIRECTORY_MUSIC), "Free Music Downloader")
        if (!directory.exists()) {
            if (!directory.mkdirs()) {
                Toast.makeText(context, "Cannot create folder", Toast.LENGTH_LONG).show()
                return
            }
        }

        try {
            // song
            val fileName = mTrack.trackFile.substring(mTrack.trackFile.lastIndexOf("/") + 1)
            println("Track Url:" + mTrack.trackFile)

            val request = DownloadManager.Request(Uri.parse(mTrack.trackFile))
            request.allowScanningByMediaScanner()
            request.setVisibleInDownloadsUi(true)
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE or DownloadManager.Request.NETWORK_WIFI)
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            request.setDestinationInExternalPublicDir(
                Environment.DIRECTORY_MUSIC,
                "Free Music Downloader" + File.separator + fileName
            )
            request.setMimeType("audio/mp3")

            mDownloadManager.enqueue(request)
            Toast.makeText(context, "Download started, please wait a few seconds", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(
                context, "There was an error occurred, " +
                        "please try again or choose another song. Don't worry, sometimes it happens. " +
                        "Stay with Us.", Toast.LENGTH_LONG
            ).show()
            e.printStackTrace()
        }
    }

    override fun onPrepared(mediaPlayer: MediaPlayer) {
        mPrepared = true
        if (!mPaused) {
            mMediaPlayer!!.start()
            mSeekBar!!.isEnabled = true
            progressHandler()
        }
    }

    override fun onCompletion(mediaPlayer: MediaPlayer) {
        mSeekBar!!.isEnabled = false
        mSeekBar!!.progress = 0
        mPlayButton.isSelected = false
    }

    override fun onError(mediaPlayer: MediaPlayer, what: Int, extra: Int): Boolean {
        return false
    }

    override fun onBufferingUpdate(mediaPlayer: MediaPlayer, percent: Int) {
        mSeekBar!!.secondaryProgress = percent
    }

    override fun onAudioFocusChange(focusChange: Int) {
        when (focusChange) {
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT ->
                if (mMediaPlayer != null && mMediaPlayer!!.isPlaying && !mPaused) {
                    mMediaPlayer!!.pause()
                }

            AudioManager.AUDIOFOCUS_GAIN -> {
                if (mMediaPlayer != null) mMediaPlayer!!.setVolume(1.0f, 1.0f)

                if (mMediaPlayer != null && !mMediaPlayer!!.isPlaying && !mPaused) {
                    mMediaPlayer!!.start()
                    progressHandler()
                }
            }

            AudioManager.AUDIOFOCUS_LOSS -> {
                mPrepared = false
                mPaused = false
                if (mMediaPlayer != null) {
                    mMediaPlayer!!.release()
                    mMediaPlayer = null

                    mSeekBar!!.progress = 0
                    mSeekBar!!.secondaryProgress = 0
                    mSeekBar!!.isEnabled = false
                    mPlayButton.isSelected = false
                }
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK ->
                if (mMediaPlayer != null)
                    mMediaPlayer!!.setVolume(0.1f, 0.1f)
        }
    }

    override fun onProgressChanged(seekbar: SeekBar, progress: Int, fromUser: Boolean) {
        if (fromUser && mMediaPlayer != null && mMediaPlayer!!.isPlaying) {
            mMediaPlayer!!.seekTo(progress * mMediaPlayer!!.duration / 100)
            progressHandler()
        }
    }

    override fun onStartTrackingTouch(seekbar: SeekBar) {
        mHandler.removeCallbacks(mRunnable)
    }

    override fun onStopTrackingTouch(seekbar: SeekBar) {}

    //TODO
    override fun onDismiss(dialogInterface: DialogInterface) {
        val permissionActivity = mContext as PermissionActivity

        mHandler.removeCallbacks(mRunnable)

        mAudioManager!!.abandonAudioFocus(this)

        if (mMediaPlayer != null) {
            mMediaPlayer!!.release()
            mMediaPlayer = null
        }

        permissionActivity.createBothPlayer(null)
    }

    //TODO
    override fun onCancel(dialogInterface: DialogInterface) {
        val permissionActivity = mContext as PermissionActivity

        mHandler.removeCallbacks(mRunnable)

        mAudioManager!!.abandonAudioFocus(this)

        if (mMediaPlayer != null) {
            mMediaPlayer!!.release()
            mMediaPlayer = null
        }

        permissionActivity.createBothPlayer(null)
    }
}
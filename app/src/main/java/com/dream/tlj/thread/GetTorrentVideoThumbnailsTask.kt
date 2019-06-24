package com.dream.tlj.thread

import android.os.AsyncTask
import android.provider.MediaStore
import com.dream.tlj.listener.GetThumbnailsListener
import com.dream.tlj.mvp.e.TorrentInfoEntity
import com.dream.tlj.util.FileTools

class GetTorrentVideoThumbnailsTask(private val listener: GetThumbnailsListener) : AsyncTask<Any, Void, List<TorrentInfoEntity>>() {
    override fun doInBackground(vararg objects: Any): List<TorrentInfoEntity> {
        val list = objects[0] as List<TorrentInfoEntity>
        for (te in list) {
            if (FileTools.isVideoFile(te.getmFileName())) {
                val bitmap = FileTools.getVideoThumbnail(te.path!!, 250, 150, MediaStore.Video.Thumbnails.FULL_SCREEN_KIND)
                if (bitmap != null) {
                    te.thumbnail = bitmap
                }
            }
        }
        return list
    }

    override fun onPostExecute(torrentInfoEntities: List<TorrentInfoEntity>) {
        super.onPostExecute(torrentInfoEntities)
        listener.success(null)
    }
}

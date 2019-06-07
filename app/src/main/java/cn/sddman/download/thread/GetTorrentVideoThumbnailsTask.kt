package cn.sddman.download.thread

import android.graphics.Bitmap
import android.os.AsyncTask
import android.provider.MediaStore

import cn.sddman.download.listener.GetThumbnailsListener
import cn.sddman.download.mvp.e.TorrentInfoEntity
import cn.sddman.download.util.FileTools

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

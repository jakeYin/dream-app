package cn.sddman.download.util

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.media.ThumbnailUtils
import android.os.Environment
import android.text.TextUtils

import org.xutils.x

import java.io.Closeable
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

import cn.sddman.download.R
import cn.sddman.download.common.Const

object FileTools {
    private val TAG = "FileTools"
    val FLAG_SHORTER = 1 shl 0
    val KB_IN_BYTES: Long = 1024
    val MB_IN_BYTES = KB_IN_BYTES * 1024
    val GB_IN_BYTES = MB_IN_BYTES * 1024
    val TB_IN_BYTES = GB_IN_BYTES * 1024
    val PB_IN_BYTES = TB_IN_BYTES * 1024


    val byteShort = "B"
    val kilobyteShort = "KB"
    val megabyteShort = "MB"
    val gigabyteShort = "GB"
    val terabyteShort = "TB"
    val petabyteShort = "PB"


    /**
     * 获取SD卡路径
     *
     * @return
     */
    val sdCardPath: String
        get() = Environment.getExternalStorageDirectory().absolutePath + File.separator
    /**
     * 获取系统存储路径
     *
     * @return
     */
    val rootDirectoryPath: String
        get() = Environment.getRootDirectory().absolutePath

    /**
     * 格式化文件大小为可读字符串
     * @param fileSize long形式的文件大小
     * @param formatSort true表示只保留一位小数点
     * @return
     */
    fun getReadableFileSize(fileSize: Long, formatSort: Boolean): String {
        return if (formatSort) {
            formatShortFileSize(fileSize)
        } else {
            formatFileSize(fileSize)
        }
    }

    /**
     * 获取文件大小
     * @param file
     * @return
     * @throws Exception
     */
    fun getFileSize(file: File): Long {
        if (!file.exists()) {
            return 0
        }
        var fis: FileInputStream? = null
        try {
            fis = FileInputStream(file)
            return fis.available().toLong()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            if (fis != null) {
                closeQuietly(fis)
            }
        }

        return 0

    }

    /**
     * 获取文件最新修改时间
     * @param filePath
     * @return
     */
    fun getFileModifyTime(filePath: String?): Long {
        if (null == filePath) {
            return 0
        }
        val file = File(filePath)
        return if (file == null || !file.exists()) {
            0
        } else file.lastModified()
    }

    /**
     * 判断输入文件是否存在
     * @param path
     * @return
     */
    fun exists(path: String): Boolean {
        return if (TextUtils.isEmpty(path) || TextUtils.isEmpty(path)) {
            false
        } else File(path).exists()

    }

    /**
     * 复制文件,只能复制非目录文件，需要复制目录请使用[.copyDir]
     * @param srcFile 需要复制的文件
     * @param dstFile 复制之后的文件
     * @return 复制成功返回true，否则返回false
     * @throws IOException
     */
    @Throws(IOException::class)
    fun copyFile(srcFile: String, dstFile: String): Boolean {

        if (TextUtils.isEmpty(srcFile) || TextUtils.isEmpty(dstFile)) {
            return false
        }

        val destFile = File(dstFile)
        val sFile = File(srcFile)

        if (!sFile.exists()) {
            return false
        }

        if (destFile.exists()) {
            destFile.delete()
        }
        var inputStream: FileInputStream? = null
        var out: FileOutputStream? = null
        try {
            inputStream = FileInputStream(sFile)
            out = FileOutputStream(destFile)
        } catch (e: FileNotFoundException) {

            return false
        }

        try {
            val buffer = ByteArray(4096)
            var bytesRead = inputStream.read(buffer)
            while (bytesRead>= 0) {
                out.write(buffer, 0, bytesRead)
                bytesRead = inputStream.read(buffer)
            }
        } finally {
            out.flush()
            try {
                out.fd.sync()
            } catch (e: IOException) {

            }

            out.close()
        }
        return true
    }

    /**
     * 复制目录
     * @param srcDir 源目录
     * @param dstDir 目标目录
     * @return
     * @throws IOException
     */
    @Throws(IOException::class)
    fun copyDir(srcDir: String, dstDir: String): Boolean {
        if (TextUtils.isEmpty(srcDir) || TextUtils.isEmpty(dstDir)) {
            return false
        }

        val srcDirFile = File(srcDir)
        if (!srcDirFile.exists()) {
            return false
        }
        if (!srcDirFile.isDirectory) {
            return false
        }
        val dstDirFile = File(dstDir)
        if (!dstDirFile.exists()) {
            dstDirFile.mkdirs()
        }
        val listFiles = srcDirFile.listFiles()

        if (listFiles == null || listFiles.size == 0) {
            return false
        }
        for (child in listFiles) {
            if (child.isFile) {
                copyFile(child.absolutePath, dstDirFile.absolutePath +
                        File.separator + child.name)
            } else {
                copyDir(child.absolutePath, dstDirFile.absolutePath +
                        File.separator + child.name)
            }
        }
        return true
    }

    /**
     * 文件剪切
     * @param srcFile
     * @param dstFile dstFile必须是最后剪切之后的完整文件路径名
     * @return
     */
    fun moveFile(srcFile: String, dstFile: String): Boolean {
        if (TextUtils.isEmpty(srcFile) || TextUtils.isEmpty(dstFile)) {
            return false
        }
        val src = File(srcFile)
        val dst = File(dstFile)
        if (!src.exists()) {
            return false
        }
        val srcStorageIndex = src.absolutePath.indexOf("/", 1)
        val dstStorageIndex = dst.absolutePath.indexOf("/", 1)
        val srcStorage = srcFile.substring(0, srcStorageIndex)
        val dstStorage = dstFile.substring(0, dstStorageIndex)
        //如果是同磁盘剪切文件，直接使用rename即可，提高剪切速度，不同磁盘间使用
        //先拷贝后删除的操作
        if (srcStorage != dstStorage) {
            return src.renameTo(File(dstFile))
        } else {
            try {
                val isDir = src.isDirectory
                val success = if (isDir) copyDir(srcFile, dstFile) else copyFile(srcFile, dstFile)
                if (success) {
                    if (isDir) {
                        deleteDir(srcFile)
                    } else {
                        deleteFile(srcFile)
                    }
                }
                return success
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
        return false
    }

    /**
     * 重命名文件
     * @param oldFile
     * @param newName
     * @return
     */
    fun renameFile(oldFile: File, newName: String): Boolean {
        if (TextUtils.isEmpty(newName)) {
            return false
        }
        return if (oldFile.exists()) {
            oldFile.renameTo(File(oldFile.parent, newName))
        } else false
    }

    /**
     * 删除目标文件，该文件不能是目录，如果是目录请使用[.deleteDir]
     * @param srcFile
     * @return
     */
    fun deleteFile(srcFile: String): Boolean {
        if (TextUtils.isEmpty(srcFile) || TextUtils.isEmpty(srcFile)) {
            return false
        }
        val file = File(srcFile)
        return if (file.exists() && !file.isDirectory) {
            file.delete()
        } else false
    }

    /**
     * 删除指定路径的目录，包括该目录的子目录以及子文件
     * @param dirFile
     * @return
     */
    fun deleteDir(dirFile: String): Boolean {
        if (TextUtils.isEmpty(dirFile) || TextUtils.isEmpty(dirFile)) {
            return false
        }
        val oldFile = File(dirFile)
        val file = File(oldFile.absolutePath + System.currentTimeMillis())
        oldFile.renameTo(file)
        if (file == null || !file.exists()) {
            return false
        }
        if (file.isDirectory) {
            val list = file.listFiles()
            for (i in list.indices) {
                if (list[i].isDirectory) {
                    deleteDir(list[i].absolutePath)
                } else {
                    list[i].delete()
                }
            }
        }

        file.delete()
        return true
    }

    /**
     * 根据指定文件名创建文件
     * @param file
     * @return
     */
    fun makeNewFile(file: String): Boolean {
        if (TextUtils.isEmpty(file)) {
            return false
        }
        val newFile = File(file)
        var success = false
        if (newFile.exists()) {
            return true
        } else {
            try {
                success = newFile.createNewFile()
            } catch (e: IOException) {
                e.printStackTrace()
                success = false
            }

        }
        return success
    }

    fun mkdirs(path: String): Boolean {
        if (!exists(path)) {
            if (!File(path).mkdirs()) {
                return false
            }
        }
        return true
    }

    /**
     * 在指定目录中创建一些子目录
     * @param parentPath 自定父目录
     * @param dirNames 需要创建的子目录数组
     * @return
     */
    fun mkdirsInParent(parentPath: String, vararg dirNames: String): Boolean {

        if (!exists(parentPath)) {
            if (!File(parentPath).mkdirs()) {
                return false
            }
        }
        var childDirFile: File? = null
        var success = false

        for (child in dirNames) {
            childDirFile = File(parentPath, child)
            success = childDirFile.mkdirs()
            if (!success) {
            }
        }

        return success
    }


    /**
     * 批量创建文件目录，需要指定目录完整路径：
     * 例如：
     * `sdcard/Android/TestDirName`
     * @param dirNames 需要创建的目录路径集合
     * @return
     */
    fun mkdirs(vararg dirNames: String): Boolean {
        var childDirFile: File? = null
        var success = false
        for (dirPath in dirNames) {
            childDirFile = File(dirPath)
            success = childDirFile.mkdirs()
            if (!success) {
            }
        }
        return success
    }

    /**
     * 判断sdcard是否已经挂载
     * @param context
     * @return
     */
    fun hasSdcard(context: Context): Boolean {
        return Environment.getExternalStorageState() === Environment.MEDIA_MOUNTED
    }

    /**
     * 检查目标文件是否有读取权限
     * @param targetFile
     * @return
     */
    fun checkFileReadPermission(targetFile: String): Boolean {
        if (TextUtils.isEmpty(targetFile)) {
            return false
        }
        val file = File(targetFile)
        return if (!file.exists()) {
            false
        } else file.canRead()
    }

    /**
     * 检查目标文件是否有写权限
     * @param targetFile
     * @return
     */
    fun checkFileWritePermission(targetFile: String): Boolean {
        if (TextUtils.isEmpty(targetFile)) {
            return false
        }
        val file = File(targetFile)
        return if (!file.exists()) {
            false
        } else file.canWrite()
    }


    /**
     * 关闭可关闭的数据
     * @param closeable
     */
    fun closeQuietly(closeable: Closeable?) {
        if (closeable != null) {
            try {
                closeable.close()
            } catch (e: RuntimeException) {
                throw e
            } catch (e: Exception) {
                // Ignore.
            }

        }
    }


    internal fun formatFileSize(sizeBytes: Long): String {
        return formatBytes(sizeBytes, 0)
    }

    internal fun formatShortFileSize(sizeBytes: Long): String {
        return formatBytes(sizeBytes, FLAG_SHORTER)
    }

    internal fun formatBytes(sizeBytes: Long, flags: Int): String {
        val isNegative = sizeBytes < 0
        var result = (if (isNegative) -sizeBytes else sizeBytes).toFloat()
        var suffix = byteShort
        var mult: Long = 1
        if (result > 900) {
            suffix = kilobyteShort
            mult = KB_IN_BYTES
            result = result / 1024
        }
        if (result > 900) {
            suffix = megabyteShort
            mult = MB_IN_BYTES
            result = result / 1024
        }
        if (result > 900) {
            suffix = gigabyteShort
            mult = GB_IN_BYTES
            result = result / 1024
        }
        if (result > 900) {
            suffix = terabyteShort
            mult = TB_IN_BYTES
            result = result / 1024
        }
        if (result > 900) {
            suffix = petabyteShort
            mult = PB_IN_BYTES
            result = result / 1024
        }
        // Note we calculate the rounded long by ourselves, but still let String.format()
        // compute the rounded value. String.format("%f", 0.1) might not return "0.1" due to
        // floating point errors.
        val roundFactor: Int
        val roundFormat: String
        if (mult == 1L || result >= 100) {
            roundFactor = 1
            roundFormat = "%.0f"
        } else if (result < 1) {
            roundFactor = 100
            roundFormat = "%.2f"
        } else if (result < 10) {
            if (flags and FLAG_SHORTER != 0) {
                roundFactor = 10
                roundFormat = "%.1f"
            } else {
                roundFactor = 100
                roundFormat = "%.2f"
            }
        } else { // 10 <= result < 100
            if (flags and FLAG_SHORTER != 0) {
                roundFactor = 1
                roundFormat = "%.0f"
            } else {
                roundFactor = 100
                roundFormat = "%.2f"
            }
        }

        if (isNegative) {
            result = -result
        }
        val roundedString = String.format(roundFormat, result)

        return roundedString + suffix
    }

    fun convertFileSize(size: Long): String {
        val kb: Long = 1024
        val mb = kb * 1024
        val gb = mb * 1024

        if (size >= gb) {
            return String.format("%.1f GB", size.toFloat() / gb)
        } else if (size >= mb) {
            val f = size.toFloat() / mb
            return String.format(if (f > 100) "%.0f M" else "%.1f M", f)
        } else if (size >= kb) {
            val f = size.toFloat() / kb
            return String.format(if (f > 100) "%.0f K" else "%.1f K", f)
        } else
            return String.format("%d B", size)
    }

    fun getFileNameWithoutSuffix(file: File): String {
        if (file.isDirectory) {
            return file.name
        }
        val file_name = file.name
        return file_name.substring(0, file_name.lastIndexOf("."))
    }

    fun getFileNameWithoutSuffix(path: String): String {
        return getFileNameWithoutSuffix(File(path))
    }

    fun getVideoThumbnail(videoPath: String, width: Int, height: Int, kind: Int): Bitmap? {
        var bitmap: Bitmap? = null
        // 获取视频的缩略图
        bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind)
        if (bitmap != null) {
            bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
                    ThumbnailUtils.OPTIONS_RECYCLE_INPUT)
        }
        return bitmap
    }

    //根据路径得到视频缩略图
    fun getVideoThumbnail(videoPath: String): Bitmap? {
        if (!File(videoPath).exists()) return null
        val media = MediaMetadataRetriever()
        media.setDataSource(videoPath)
        return media.frameAtTime
    }

    //获取视频总时长
    fun getVideoDuration(path: String): Int {
        val media = MediaMetadataRetriever()
        media.setDataSource(path)
        val duration = media.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION) //
        return Integer.parseInt(duration)
    }

    fun getFileName(path: String): String {
        return path.substring(path.lastIndexOf("/") + 1)
    }

    fun isVideoFile(fileName: String?): Boolean {
        if (null == fileName) return false
        val pos = fileName.lastIndexOf('.')
        val extensionName = if (pos >= 0) fileName.substring(pos + 1) else null
        return null != extensionName && VideoFileHelper.isSupportedVideoFileExtension(extensionName!!)
    }

    fun saveBitmap(bm: Bitmap, picName: String): String? {


        try {
            val file = File(Const.VIDEO_PIC_PATH)
            if (!file.exists()) file.mkdirs()
            val f = File(Const.VIDEO_PIC_PATH, picName)
            if (!f.exists()) {
                f.createNewFile()
            }
            val out = FileOutputStream(f)
            bm.compress(Bitmap.CompressFormat.PNG, 90, out)
            out.flush()
            out.close()
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }

        return Const.VIDEO_PIC_PATH + File.separator + picName
    }

    fun getFileIcon(fileName: String?): Int {
        if (null == fileName) {
            return R.drawable.ic_unknow
        }
        val suffix = fileName.substring(fileName.lastIndexOf(".") + 1).toUpperCase()
        if (fileName.indexOf(".") < 0) {
            return R.drawable.ic_floder
        } else if (isVideoFile(fileName)) {
            return R.drawable.ic_video
        } else if ("DOC" == suffix || "DOCX" == suffix) {
            return R.drawable.ic_doc
        } else if ("XLS" == suffix || "XLSX" == suffix) {
            return R.drawable.ic_excel
        } else if ("PPT" == suffix || "PPTX" == suffix) {
            return R.drawable.ic_ppt
        } else if (".RAR.ZIP.7Z.ISO".indexOf(suffix.toUpperCase()) > -1) {
            return R.drawable.ic_rar
        } else if (".MP3.WMA.FLAC.AAC.MMF.AMR.M4A.M4R.OGG.MP2.WAV.WV".indexOf(suffix.toUpperCase()) > -1) {
            return R.drawable.ic_music
        } else if (".BMP.JPEG.GIF.PSD.PNG.TIFF.TGA.EPS,JPG".indexOf(suffix.toUpperCase()) > -1) {
            return R.drawable.ic_pic
        } else if (".HTML.PHP.JSP.MHT.HTM".indexOf(suffix.toUpperCase()) > -1) {
            return R.drawable.ic_web
        } else if ("EXE" == suffix) {
            return R.drawable.ic_exe
        } else if ("TXT" == suffix) {
            return R.drawable.ic_txt
        } else if ("EXE" == suffix) {
            return R.drawable.ic_exe
        } else if ("APK" == suffix) {
            return R.drawable.ic_apk
        } else if ("TORRENT" == suffix) {
            return R.drawable.ic_bticon
        } else if ("URL" == suffix) {
            return R.drawable.ic_urlicon
        }
        return R.drawable.ic_unknow
    }
}

package cn.sddman.download.listener

interface HttpResultListener {
    fun onSuccess(result: String)
    fun onError(ex: Throwable)
}

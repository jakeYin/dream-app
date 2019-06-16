package cn.sddman.download.mvp.p

interface UrlDownLoadPresenter {
    fun startTask(url: String,source:String?,ruleId:String)
    fun startTask(url: String)
}

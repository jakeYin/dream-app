package cn.sddman.download.common

class MessageEvent(message: Msg) {
    var message: Msg
        internal set

    init {
        this.message = message
    }
}

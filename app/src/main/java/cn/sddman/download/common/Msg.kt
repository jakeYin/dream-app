package cn.sddman.download.common

class Msg {
    var type: Int = 0
    var obj: Any? = null

    constructor() {}
    constructor(type: Int, obj: Any) {
        this.type = type
        this.obj = obj
    }
}

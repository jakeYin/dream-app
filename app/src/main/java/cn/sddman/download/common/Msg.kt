package cn.sddman.download.common

class Msg {
    var type: Int = 0
    var obj: Any? = null

    constructor(type:Int){
        this.type = type
    }
    constructor(type: Int, obj: Any) {
        this.type = type
        this.obj = obj
    }
}

package cn.sddman.download.mvp.e

import java.io.Serializable
data class MagnetInfo(var name: String?,
                      var magnet: String?,
                      var detailUrl: String?,
                      var desc: String?){
}

data class MagnetDetail(var link:List<String>?
){
}

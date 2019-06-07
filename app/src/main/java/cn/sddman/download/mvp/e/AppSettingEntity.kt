package cn.sddman.download.mvp.e

import org.xutils.db.annotation.Column
import org.xutils.db.annotation.Table

@Table(name = "AppSetting")
class AppSettingEntity {
    @Column(name = "id", isId = true, autoGen = true)
    var id: Int = 0
    @Column(name = "key")
    var key: String? = null
    @Column(name = "value")
    var value: String? = null
}

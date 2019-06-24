package com.dream.tlj.mvp.e

data class MagnetDetail(var name:String,var check:Boolean
){
    constructor(name: String) : this(name,false)
}
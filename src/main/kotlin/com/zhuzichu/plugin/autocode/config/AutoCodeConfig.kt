package com.zhuzichu.plugin.autocode.config

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil


@State(name = "AutoCodeConfig", storages = [Storage(file = "autocode/setting.xml")])
class AutoCodeConfig : PersistentStateComponent<AutoCodeConfig> {

    var packageName = "com.tajer.android"

    override fun getState(): AutoCodeConfig? {
        return this
    }

    override fun loadState(state: AutoCodeConfig) {
        XmlSerializerUtil.copyBean(state, this)
    }

}
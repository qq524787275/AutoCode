package com.zhuzichu.plugin.autocode.config

import com.intellij.openapi.components.ServiceManager


object ConfigManager {
    @JvmField
    val autoCodeConfig = ServiceManager.getService(AutoCodeConfig::class.java)!!

}
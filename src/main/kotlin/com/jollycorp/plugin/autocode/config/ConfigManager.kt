package com.jollycorp.plugin.autocode.config

import com.intellij.openapi.components.ServiceManager


object ConfigManager {
    @JvmField
    val autoCodeConfig = ServiceManager.getService(AutoCodeConfig::class.java)!!

}
package com.jollycorp.plugin.autocode.ext

import com.intellij.openapi.diagnostic.Logger

val LOG = Logger.getInstance("zzc")

fun Any.logi() {
    LOG.info(this.toString())
}
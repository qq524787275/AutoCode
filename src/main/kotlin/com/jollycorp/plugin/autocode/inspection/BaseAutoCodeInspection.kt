package com.jollycorp.plugin.autocode.inspection

import com.intellij.codeHighlighting.HighlightDisplayLevel
import org.jetbrains.kotlin.idea.inspections.AbstractKotlinInspection

abstract class BaseAutoCodeInspection : AbstractKotlinInspection() {

    companion object {
        const val GROUP_NAME = "Auto-Code"
    }

    override fun getGroupDisplayName(): String {
        return GROUP_NAME
    }

    override fun isEnabledByDefault(): Boolean {
        return true
    }

    override fun getDefaultLevel(): HighlightDisplayLevel {
        return HighlightDisplayLevel.WARNING
    }

}
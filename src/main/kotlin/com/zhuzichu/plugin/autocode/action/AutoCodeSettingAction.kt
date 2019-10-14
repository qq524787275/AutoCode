package com.zhuzichu.plugin.autocode.action

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.zhuzichu.plugin.autocode.panel.SettingDialog


class AutoCodeSettingAction : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val dialog = SettingDialog()
        dialog.pack()
        dialog.isVisible = true
        dialog.setSize(400, 200)
    }
}
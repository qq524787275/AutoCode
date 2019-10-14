package com.zhuzichu.plugin.autocode

interface IAutoCode {

    companion object {
        const val TYPE_ACTIVITY = "Activity"
        const val TYPE_FRAGMENT = "Fragment"
        const val TYPE_FRAGMENT_DIALOG = "FragmentDialog"
        const val TYPE_PRESENTER = "Presenter"
        const val TYPE_CONTRACT="Contract"
    }

    fun getType(): String

}
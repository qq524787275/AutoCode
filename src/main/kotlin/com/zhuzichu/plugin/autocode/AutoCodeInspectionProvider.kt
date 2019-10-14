package com.zhuzichu.plugin.autocode

import com.google.common.collect.Lists
import com.intellij.codeInspection.InspectionToolProvider
import com.zhuzichu.plugin.autocode.inspection.*

class AutoCodeInspectionProvider : InspectionToolProvider {

    companion object {
        private val CLASS_LIST = Lists.newArrayList<Class<*>>().apply {
            add(AutoCodeActivityInspection::class.java)
            add(AutoCodeFragmentInspection::class.java)
            add(AutoCodeFragmentDailogInspection::class.java)
            add(AutoCodePresenterInspection::class.java)
            add(AutoCodeContractInspection::class.java)
        }
    }

    override fun getInspectionClasses(): Array<Class<*>> {
        return CLASS_LIST.toTypedArray()
    }

}
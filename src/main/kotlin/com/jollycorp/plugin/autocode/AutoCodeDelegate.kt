package com.jollycorp.plugin.autocode

import com.intellij.psi.PsiElement
import com.jollycorp.plugin.autocode.config.ConfigManager
import com.jollycorp.plugin.autocode.ext.hump2Underline
import org.jetbrains.kotlin.idea.core.getOrCreateCompanionObject
import org.jetbrains.kotlin.idea.util.application.runWriteAction
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtPsiFactory
import org.jetbrains.kotlin.psi.getOrCreateBody
import org.jetbrains.kotlin.resolve.ImportPath

class AutoCodeDelegate(private val type: String) {

    fun checkClass(klass: KtClass): Boolean {
        val className = klass.name ?: ""
        var isPass = false
        if (className.startsWith(type)) {
            val name = klass.getSuperTypeList()?.entries?.firstOrNull()?.typeAsUserType?.referencedName ?: ""
            if (name != type.plus("AnalyticsBase")) {
                isPass = true
            }
        }
        return isPass
    }

    fun createCode(
            factory: KtPsiFactory,
            klass: KtClass,
            type: String
    ) {
        val className = klass.name!!
        val word = className.substring(type.length)

        /**
         * 生成路由注解
         */
        val route = factory.createAnnotationEntry(
                """
                |@Route(path = $className.TARGET)
                """.trimMargin()
        )

        /**
         * 生成伴生对象 以及 TAGE
         */
        val target = factory.createProperty(
                """
                |const val TARGET = ViewPathConst.${className.hump2Underline().toUpperCase().drop(1)}
                """.trimMargin()
        )

        /**
         * 生成父类继承
         */
        val superType = factory.createSuperTypeCallEntry(
                """
                |${type}AnalyticsBase<DefaultViewParamsModel, Contract$word.SubPresenter, Contract$word.SubView>()
                """.trimMargin()
        )

        /**
         * 生成实现接口
         */
        val superType1 = factory.createSuperTypeEntry(
                """
                |Contract$word.SubView
                """.trimMargin()
        )

        /**
         * 生成getContentViewResId方法
         */
        val getContentViewResId = factory.createFunction(
                """
                |override fun getContentViewResId() = R.layout.${className.hump2Underline().drop(1)}
                """.trimMargin()
        ).apply {
            add(factory.createNewLine())
        }

        /**
         * 生成createPresenter方法
         */
        val createPresenter = factory.createFunction(
                """
                |override fun createPresenter() = Presenter$word(this)
                """.trimMargin()
        ).apply {
            add(factory.createNewLine())
        }

        /**
         * 生成getSub方法
         */
        val getSub = factory.createFunction(
                """
                |override fun getSub() = this
                """.trimMargin()
        ).apply {
            add(factory.createNewLine())
        }

        /**
         * 生成getViewCode方法
         */
        val getViewCode = factory.createFunction(
                """
                |@ViewCode
                |override fun getViewCode() = ViewCodeConst.${className.hump2Underline().toUpperCase().drop(1)}
                """.trimMargin()
        ).apply {
            add(factory.createNewLine())
        }

        /**
         * 生成getTagGAScreenName方法
         */
        val getTagGAScreenName = factory.createFunction(
                """
                |override fun getTagGAScreenName() = "$word"
                """.trimMargin()
        ).apply {
            add(factory.createNewLine())
        }

        val packageName= ConfigManager.autoCodeConfig.packageName

        runWriteAction {

            //--------------------------------------------  start 生成improt包  --------------------------------------------

            klass.parent.addAfter(getImportText(packageName.plus(".base.base.entity.model.params.DefaultViewParamsModel"), factory), klass.parent.firstChild)

            klass.parent.addAfter(getImportText(packageName.plus(".common.consts.ViewPathConst"), factory), klass.parent.firstChild)

            klass.parent.addAfter(getImportText(packageName.plus(".common.consts.ViewCodeConst"), factory), klass.parent.firstChild)

            klass.parent.addAfter(getImportText("com.alibaba.android.arouter.facade.annotation.Route", factory), klass.parent.firstChild)

            klass.parent.addAfter(getImportText("com.jollycorp.android.libs.view.code.annotation.ViewCode", factory), klass.parent.firstChild)

            //--------------------------------------------  end 生成improt包  --------------------------------------------

            klass.addAnnotationEntry(route)

            klass.addSuperTypeListEntry(superType)

            klass.addSuperTypeListEntry(superType1)

            klass.getOrCreateCompanionObject().getOrCreateBody()
                    .addAfter(target, klass.getOrCreateCompanionObject().getOrCreateBody().lBrace)

            klass.getOrCreateBody()
                    .addAfter(getContentViewResId, klass.getOrCreateBody().children.last())

            klass.getOrCreateBody()
                    .addAfter(factory.createNewLine(), klass.getOrCreateBody().children.last())

            klass.getOrCreateBody()
                    .addAfter(createPresenter, klass.getOrCreateBody().children.last())

            klass.getOrCreateBody()
                    .addAfter(getSub, klass.getOrCreateBody().children.last())

            klass.getOrCreateBody()
                    .addAfter(getViewCode, klass.getOrCreateBody().children.last())

            klass.getOrCreateBody()
                    .addAfter(getTagGAScreenName, klass.getOrCreateBody().children.last())

        }
    }

    private fun getImportText(text: String, factory: KtPsiFactory): PsiElement {
        return factory.createImportDirective(
                ImportPath.fromString(
                        """
                |$text
                """.trimMargin()
                )
        ).apply {
            add(factory.createNewLine())
        }
    }

}
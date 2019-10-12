package com.jollycorp.plugin.autocode.inspection

import com.intellij.codeInspection.LocalInspectionToolSession
import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementVisitor
import com.jollycorp.plugin.autocode.IAutoCode
import org.jetbrains.kotlin.idea.core.addTypeParameter
import org.jetbrains.kotlin.idea.util.application.runWriteAction
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtPsiFactory
import org.jetbrains.kotlin.psi.KtVisitorVoid
import org.jetbrains.kotlin.psi.getOrCreateBody

class AutoCodePresenterInspection : BaseAutoCodeInspection(), IAutoCode {

    override fun getType(): String = IAutoCode.TYPE_PRESENTER

    override fun getDisplayName(): String {
        return "自动生成Presenter模板"
    }

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean, session: LocalInspectionToolSession): PsiElementVisitor {
        return object : KtVisitorVoid() {
            override fun visitClass(klass: KtClass) {
                if (checkClass(klass)) {
                    holder.registerProblem(
                            klass.nameIdentifier as PsiElement,
                            "请继承 PresenterBase",
                            GenerateMethod()
                    )
                }
            }
        }
    }

    inner class GenerateMethod() : LocalQuickFix {

        override fun getName(): String = "生成 Presenter 模板"

        override fun getFamilyName(): String = "PresenterBase"

        override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
            val factory = KtPsiFactory(project)
            val klass = descriptor.psiElement.parent as KtClass
            createCode(factory, klass, getType())
        }
    }

    private fun createCode(factory: KtPsiFactory, klass: KtClass, type: String) {

        val className = klass.name!!

        val word = className.substring(type.length)

        val constructor = factory.createPrimaryConstructor(
                """
                |(baseView: IBaseView<DefaultViewParamsModel, Contract$word.SubPresenter, Contract$word.SubView>)
                """.trimMargin()
        ).apply {
            add(factory.createNewLine())
        }

        val superType = factory.createSuperTypeCallEntry(
                """
                |${type}Base<DefaultViewParamsModel, Contract$word.SubPresenter, Contract$word.SubView>(baseView)
                """.trimMargin()
        )

        val superType1 = factory.createSuperTypeEntry(
                """
                |Contract$word.SubPresenter
                """.trimMargin()
        )

        val getSub = factory.createFunction(
                """
                |override fun getSub() = this
                """.trimMargin()
        ).apply {
            add(factory.createNewLine())
        }

        runWriteAction {
            klass.add(constructor)
            klass.addSuperTypeListEntry(superType)
            klass.addSuperTypeListEntry(superType1)

            klass.getOrCreateBody()
                    .addAfter(getSub, klass.getOrCreateBody().lBrace)
        }

    }

    private fun checkClass(klass: KtClass): Boolean {
        val className = klass.name ?: ""
        var isPass = false
        if (className.startsWith(getType())) {
            val name = klass.getSuperTypeList()?.entries?.firstOrNull()?.typeAsUserType?.referencedName ?: ""
            if (name != getType().plus("Base")) {
                isPass = true
            }
        }
        return isPass
    }

}
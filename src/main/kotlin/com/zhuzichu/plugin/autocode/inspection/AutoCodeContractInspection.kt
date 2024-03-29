package com.zhuzichu.plugin.autocode.inspection

import com.intellij.codeInspection.LocalInspectionToolSession
import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementVisitor
import com.zhuzichu.plugin.autocode.IAutoCode
import org.jetbrains.kotlin.idea.util.application.runWriteAction
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtPsiFactory
import org.jetbrains.kotlin.psi.KtVisitorVoid
import org.jetbrains.kotlin.psi.getOrCreateBody

class AutoCodeContractInspection : BaseAutoCodeInspection(), IAutoCode {

    override fun getType(): String = IAutoCode.TYPE_CONTRACT

    override fun getDisplayName(): String {
        return "自动生成Contract模板"
    }

    override fun buildVisitor(
            holder: ProblemsHolder,
            isOnTheFly: Boolean,
            session: LocalInspectionToolSession
    ): PsiElementVisitor {
        return object : KtVisitorVoid() {
            override fun visitClass(klass: KtClass) {
                super.visitClass(klass)
                var hasInterface = false
                val className = klass.name
                if (className.isNullOrBlank())
                    return
                if (!klass.isInterface() || !className.startsWith("Contract"))
                    return
                var count = 0
                klass.body?.children?.forEach {
                    if (it is KtClass && it.isInterface() && (it.name == "SubView" || it.name == "SubPresenter")) {
                        count++
                    }
                }
                if (count == 2) {
                    hasInterface = true
                }
                if (!hasInterface) {
                    holder.registerProblem(
                            klass.nameIdentifier as PsiElement,
                            "请添加 SubView SubPresenter 接口",
                            GenerateMethod(className)
                    )
                }
            }
        }
    }

    inner class GenerateMethod(private val name: String) : LocalQuickFix {
        override fun getName(): String = "生成 SubView SubPresenter 接口"

        override fun getFamilyName(): String = name

        override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
            val factory = KtPsiFactory(project)
            val klass = descriptor.psiElement.parent as KtClass

            val subView = factory.createClass(
                    """
                |interface SubView : IBaseView.ISubView {
                |
                |}
                """.trimMargin()
            )

            val subPresenter = factory.createClass(
                    """
                |interface SubPresenter : IBasePresenter.ISubPresenter{
                |
                |}
                """.trimMargin()
            )

            runWriteAction {
                klass.getOrCreateBody().addAfter(subView, klass.getOrCreateBody().lBrace)
                klass.getOrCreateBody().addAfter(subPresenter, klass.getOrCreateBody().lBrace)
            }
        }
    }
}
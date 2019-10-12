package com.jollycorp.plugin.autocode.inspection

import com.intellij.codeInspection.LocalInspectionToolSession
import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementVisitor
import com.jollycorp.plugin.autocode.AutoCodeDelegate
import com.jollycorp.plugin.autocode.IAutoCode
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtPsiFactory
import org.jetbrains.kotlin.psi.KtVisitorVoid
import org.jetbrains.kotlin.psi.psiUtil.createSmartPointer
import javax.swing.JComponent

class AutoCodeFragmentDailogInspection : BaseAutoCodeInspection(), IAutoCode {

    private val autoCodeDelegate by lazy { AutoCodeDelegate(getType()) }

    override fun getType(): String = IAutoCode.TYPE_FRAGMENT_DIALOG

    override fun getDisplayName(): String {
        return "自动生成FragmentDialog模板"
    }

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean, session: LocalInspectionToolSession): PsiElementVisitor {
        return object : KtVisitorVoid() {
            override fun visitClass(klass: KtClass) {
                if (autoCodeDelegate.checkClass(klass)) {
                    klass.createSmartPointer()
                    holder.registerProblem(
                            klass.nameIdentifier as PsiElement,
                            "请继承 FragmengDialogAnalyticsBase",
                            GenerateMethod()
                    )
                }
            }
        }
    }

    inner class GenerateMethod() : LocalQuickFix {

        override fun getName(): String = "生成  FragmengDialog 模板"

        override fun getFamilyName(): String = "FragmengDialogAnalyticsBase"

        override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
            val factory = KtPsiFactory(project)
            val klass = descriptor.psiElement.parent as KtClass
            autoCodeDelegate.createCode(factory, klass, getType())
        }
    }

    override fun createOptionsPanel(): JComponent? {
        return super.createOptionsPanel()
    }

}
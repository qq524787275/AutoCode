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

class AutoCodeFragmentInspection : BaseAutoCodeInspection(), IAutoCode {

    private val autoCodeDelegate by lazy { AutoCodeDelegate(getType()) }

    override fun getType(): String = IAutoCode.TYPE_FRAGMENT

    override fun getDisplayName(): String {
        return "自动生成Fragment模板"
    }

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean, session: LocalInspectionToolSession): PsiElementVisitor {
        return object : KtVisitorVoid() {
            override fun visitClass(klass: KtClass) {
                if (autoCodeDelegate.checkClass(klass)) {
                    holder.registerProblem(
                            klass.nameIdentifier as PsiElement,
                            "请继承 FragmentAnalyticsBase",
                            GenerateMethod()
                    )
                }
            }
        }
    }

    inner class GenerateMethod() : LocalQuickFix {

        override fun getName(): String = "生成 Fragment 模板"

        override fun getFamilyName(): String = "FragmentAnalyticsBase"

        override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
            val factory = KtPsiFactory(project)
            val klass = descriptor.psiElement.parent as KtClass
            autoCodeDelegate.createCode(factory, klass, getType())
        }
    }


    override fun getStaticDescription(): String? {
        return """
               |自动生成代码示例
               |
               |@Route(path = FragmentMessage.TARGET)
               |class FragmentMessage : FragmentAnalyticsBase<DefaultViewParamsModel, ContractMessage.SubPresenter, ContractMessage.SubView>(), ContractMessage.SubView {
               |companion object {
               |const val TARGET = ViewPathConst.FRAGMENT_MESSAGE
               |} 
               |
               |override fun getContentViewResId() = R.layout.fragment_message
               |
               |override fun createPresenter() = PresenterMessage(this)
               |
               |override fun getSub() = this
               |
               |@ViewCode
               |override fun getViewCode() = ViewCodeConst.FRAGMENT_MESSAGE
               |
               |override fun getTagGAScreenName() = "Message"
               |}
               """.trimMargin()
    }
}
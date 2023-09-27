package com.watermelon.context.hover

import com.intellij.lang.documentation.DocumentationProvider
import com.intellij.psi.PsiElement

class Hover : DocumentationProvider {

    override fun getQuickNavigateInfo(element: PsiElement?, originalElement: PsiElement?): String? {
        println("element $element")
        if (element == null) return null
        return "Hover information for ${element.text}"
    }

    override fun generateDoc(element: PsiElement, originalElement: PsiElement?): String? {
        println("generated")
        return "Generated doc aaaaaaaa for ${element.text}"
    }
}

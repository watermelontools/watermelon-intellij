package com.github.baristageek.watermelonintellij.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent

class HelloWorldAction : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        println("Hello World!")
    }

}
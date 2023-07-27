package com.github.baristageek.watermelonintellij.services

import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import com.github.baristageek.watermelonintellij.MyBundle
import com.github.baristageek.watermelonintellij.listeners.MyApplicationActivationListener
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.fileTypes.PlainTextFileType
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.editor.Document
@Service(Service.Level.PROJECT)
class MyProjectService(project: Project) {

    init {
        println("project service init")
    }

    private val document: Document = EditorFactory.getInstance().createDocument("")

    private val editor: Editor = EditorFactory.getInstance().createEditor(document)
    private val activationListener = MyApplicationActivationListener(editor)
//    fun getRandomNumber() = (1..100).random()
}

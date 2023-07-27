package com.github.baristageek.watermelonintellij.listeners

import com.intellij.codeInsight.codeVision.ui.mousePressed
import com.intellij.openapi.application.ApplicationActivationListener
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.wm.IdeFrame

import com.intellij.codeInsight.hint.HintManager
import com.intellij.openapi.editor.event.EditorMouseEvent
import com.intellij.openapi.editor.event.EditorMouseEventArea
import com.intellij.openapi.editor.event.EditorMouseMotionListener
import com.intellij.ui.HintHint
import javax.swing.JLabel
import com.intellij.ui.awt.RelativePoint
import com.intellij.openapi.editor.Editor

//internal class MyApplicationActivationListener : ApplicationActivationListener {
//
//    override fun applicationActivated(ideFrame: IdeFrame) {
//        thisLogger().warn("Don't forget to remove all non-needed sample code files with their corresponding registration entries in `plugin.xml`.")
//    }
//}

class MyApplicationActivationListener(val editor: Editor) : EditorMouseMotionListener {
    init {
        editor.addEditorMouseMotionListener(this)
        println("MyApplicationActivationListener init")
    }


    override fun mouseMoved(e: EditorMouseEvent) {
        println("mose mve")
    }

//    override fun mouseMoved(e: EditorMouseEvent) {
//        val line = editor.xyToLogicalPosition(e.mouseEvent.point).line
//
//        println("l27 line: $line")
//        println("mouse moved:")
//
//        if (e.area == EditorMouseEventArea.LINE_MARKERS_AREA) {
//            println("line markers area")
//        }
//        if(e.area == EditorMouseEventArea.LINE_NUMBERS_AREA) {
//            println("e.area equals")
//            val line = e.editor.caretModel.logicalPosition.line
//            if(shouldShowTooltip(line)) {
//                val tooltip = createTooltip(line)
////                HintManager.getInstance().showHint(e.editor, tooltip, HintManager.UNDER, HintManager.HIDE_BY_ANY_KEY, 0, false
//                val tooltipComponent = JLabel("My Tooltip")
//                val point = RelativePoint(e.mouseEvent.locationOnScreen)
//
//                HintManager.getInstance().showHint(
//                    tooltipComponent,
//                    point,
//                    HintManager.UNDER as Int,
//                    HintManager.HIDE_BY_ANY_KEY
//                )
//
//            }
//        }
//    }

//    private fun shouldShowTooltip(line: Int): Boolean {
//        // Logic to check if tooltip should show on line
//        return true
//    }

//    private fun createTooltip(line: Int) {
//        println("hoveredLine index: $line");
//    }

}
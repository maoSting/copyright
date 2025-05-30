package com.butterfly.copyright

import com.intellij.icons.ExpUiIcons.Toolwindow.Messages
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.ui.Messages

class ExportAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: kotlin.run {
            println("no project DQ")
            return
        }
        val projectPath = project.basePath
        println("projectPath: $projectPath")
        val generator = CodeDocxGenerator()
        var list = mutableListOf<String>()
        if (projectPath != null) {
            list.add(projectPath)
        }
        // 配置设置
        val settings = SettingsConfigurable.getInstance()
        list.add(settings.softwareName)
        list.add(settings.softwareVersion)
        list.add(settings.writeWay.toString() )
        list.add(settings.softwareFiles)
        val ignoreDirs: List<String> =  settings.ignoreDirs.split(",")
        generator.start(list, ignoreDirs)
    }
}
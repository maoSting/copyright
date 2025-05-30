package com.butterfly.copyright

import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import groovyjarjarantlr.CodeGenerator

class ParamsWindows {
    fun star() {
        println("GO")
        val generator = CodeDocxGenerator()
        val path = ProjectManager.getInstance().getDefaultProject().basePath
        var list = mutableListOf<String>()
        if (path != null) {
            list.add(path)
        }
        println(list)
        generator.start(list, emptyList())
    }
}
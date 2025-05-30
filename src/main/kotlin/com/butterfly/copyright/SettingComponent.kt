package com.butterfly.copyright

import com.intellij.openapi.options.Configurable
import com.intellij.openapi.ui.DialogPanel
import com.intellij.ui.dsl.builder.*
import javax.swing.ButtonGroup
import javax.swing.JComponent

class SettingComponent : Configurable {
    private var panel: DialogPanel? = null
    private var softwareName: String = ""
    private var softwareVersion: String = ""
    private var softwareFiles: String = ""
    private val writeList = listOf("顺序写入60页", "前后各30页")
    private var writeWay: Int = 1
    private var ignoreDirs: String = ""


    override fun getDisplayName() = "My Plugin Settings"

    override fun createComponent(): JComponent {
        // 从持久化存储加载初始值
        val settings = SettingsConfigurable.getInstance()
        softwareName = settings.softwareName
        softwareVersion = settings.softwareVersion
        softwareFiles = settings.softwareFiles
        writeWay = settings.writeWay
        ignoreDirs = settings.ignoreDirs

        panel = panel {
            group("设置") {
                row("软件名称：") {
                    textField()
                        .bindText(::softwareName)
                        .onChanged { textField ->
                            softwareName = textField.text
                        }
                        .comment("Place typing software name")
                }
                row("软件版本：") {
                    textField()
                        .bindText(::softwareVersion)
                        .onChanged { textField ->
                            softwareVersion = textField.text
                        }
                        .comment("Place typing software version")
                }
                row("文件类型：") {
                    textField()
                        .bindText(::softwareFiles)
                        .onChanged { textField ->
                            softwareFiles = textField.text
                        }
                        .comment("请输入文件后缀名，并用空格分割。.java .js")

//                    contextHelp(".java .xml")
                }
                buttonsGroup ("写入顺序：") {
                    row {
                        radioButton("顺序写入", 0)
                    }
                    row {
                        radioButton("前后各30页", 1)
                    }
                }.bind({ writeWay }, { writeWay = it })

//
//
//                val buttonGroup = ButtonGroup()
//
//                group("Theme Settings") {
//                    writeList.forEachIndexed { index, theme ->
//                        row {
//                            radioButton(theme, buttonGroup)
//                                .selected(index == writeWay)
//                                .onChanged { radio ->
//                                    if (radio.isSelected) writeWay = index
//                                }
//                        }
//                    }
//                }

                row("忽略目录：") {
                    textField()
                        .bindText(::ignoreDirs)
                        .onChanged { textField ->
                            ignoreDirs = textField.text
                        }
                        .comment("Place typing include dirs")
                }
            }
        }
        return panel!!
    }

    override fun isModified(): Boolean {
        val settings = SettingsConfigurable.getInstance()
        return softwareName != settings.softwareName ||
                softwareVersion != settings.softwareVersion ||
                softwareFiles != settings.softwareFiles ||
                writeWay != settings.writeWay ||
                ignoreDirs != settings.ignoreDirs
    }

    override fun apply() {
        val settings = SettingsConfigurable.getInstance()
        settings.softwareName = softwareName
        settings.softwareVersion = softwareVersion
        settings.softwareFiles = softwareFiles
        settings.writeWay = writeWay
        settings.ignoreDirs = ignoreDirs
    }

    override fun reset() {
        val settings = SettingsConfigurable.getInstance()
        softwareName = settings.softwareName
        softwareVersion = settings.softwareVersion
        softwareFiles = settings.softwareFiles
        writeWay = settings.writeWay
        ignoreDirs = settings.ignoreDirs
    }
}
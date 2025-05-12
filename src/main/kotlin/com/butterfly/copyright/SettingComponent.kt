package com.butterfly.copyright

import com.intellij.openapi.options.Configurable
import com.intellij.openapi.ui.DialogPanel
import com.intellij.ui.components.JBTextField
import com.intellij.ui.dsl.builder.*
import javax.swing.JComponent

class SettingComponent : Configurable {
    private var panel: DialogPanel? = null
    private var setting1: String = ""
    private var setting2: Boolean = false
    private var isKTSetting: Boolean = false
    private var setting3: Int = 0

    override fun getDisplayName() = "My Plugin Settings"

    override fun createComponent(): JComponent {
        // 从持久化存储加载初始值
        val settings = SettingsConfigurable.getInstance()
        setting1 = settings.configValue
        setting2 = settings.isFeatureEnabled
        isKTSetting = settings.isKt
        setting3 = settings.numberOption

        panel = panel {
            group("Basic Settings") {
                row("Configuration Value:") {
                    textField()
                        .bindText(::setting1)
                        .onChanged { textField ->
                            setting1 = textField.text
                            println("setting2: $setting1")
                        }
                        .comment("Enter your configuration value here")
                }

                row {
                    checkBox("Enable")
                        .bindSelected(::setting2)
                        .onChanged { checkBox ->
                            setting2 = checkBox.isSelected
                            println("setting2: $setting2")
                        }
                        .gap(RightGap.SMALL)

                    checkBox("Is KT")
                        .bindSelected(::isKTSetting)
                        .onChanged { checkBox ->
                            isKTSetting = checkBox.isSelected
                            println("setting2: $isKTSetting")
                        }
                        .gap(RightGap.SMALL)

                    contextHelp("When enabled, activates advanced functionality")
                }

                row("Number Option:") {
                    intTextField(1..100)
                        .bindIntText(::setting3)
                        .onChanged { field ->
                            setting3 = field.text.toIntOrNull()!!
                            println("setting3: $setting3")
                        }
                }
            }
        }
        return panel!!
    }

    override fun isModified(): Boolean {
        val settings = SettingsConfigurable.getInstance()
        return setting1 != settings.configValue ||
                setting2 != settings.isFeatureEnabled ||
                isKTSetting != settings.isKt ||
                setting3 != settings.numberOption
    }

    override fun apply() {
        val settings = SettingsConfigurable.getInstance()
        settings.configValue = setting1
        settings.isFeatureEnabled = setting2
        settings.isKt = isKTSetting
        settings.numberOption = setting3
    }

    override fun reset() {
        val settings = SettingsConfigurable.getInstance()
        setting1 = settings.configValue
        setting2 = settings.isFeatureEnabled
        isKTSetting = settings.isKt
        setting3 = settings.numberOption
    }
}
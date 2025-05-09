package com.butterfly.copyright

import com.intellij.openapi.options.Configurable
import com.intellij.ui.dsl.builder.*
import javax.swing.Box
import javax.swing.BoxLayout
import javax.swing.JCheckBox
import javax.swing.JComponent
import javax.swing.JPanel

class SettingComponent : Configurable {
    private var panel: Panel? = null
    private var setting1: String = ""
    private var setting2: Boolean = false
    private var setting3: Int = 0

    override fun getDisplayName() = "My Plugin Settings"

    override fun createComponent(): JComponent {
        // 从持久化存储加载初始值
        val settings = SettingsConfigurable.getInstance()
        setting1 = settings.configValue
        setting2 = settings.isFeatureEnabled
        setting3 = settings.numberOption

        return panel {
            group("Basic Settings") {
                row("Configuration Value:") {
                    textField()
                        .bindText(::setting1)
                        .comment("Enter your configuration value here")
                }

                row {
                    checkBox("Enable")
                        .bindSelected(::setting2)
                        .gap(RightGap.SMALL)

                    contextHelp("When enabled, activates advanced functionality")
                }

                row("Number Option:") {
                    intTextField(1..100)
                        .bindIntText(::setting3)
                }
            }
        }
    }

    override fun isModified(): Boolean {
        val settings = SettingsConfigurable.getInstance()
        return setting1 != settings.configValue ||
                setting2 != settings.isFeatureEnabled ||
                setting3 != settings.numberOption
    }

    override fun apply() {
        val settings = SettingsConfigurable.getInstance()
        settings.configValue = setting1
        settings.isFeatureEnabled = setting2
        settings.numberOption = setting3
    }

    override fun reset() {
        val settings = SettingsConfigurable.getInstance()
        setting1 = settings.configValue
        setting2 = settings.isFeatureEnabled
        setting3 = settings.numberOption
    }
}
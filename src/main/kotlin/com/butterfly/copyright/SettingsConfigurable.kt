package com.butterfly.copyright


import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.*
import com.intellij.util.xmlb.XmlSerializerUtil
import com.intellij.util.xmlb.annotations.Attribute


@Service(Service.Level.APP)
@State(name = "CopyrightSettings", storages = [Storage("copyright-settings.xml")])
class SettingsConfigurable: PersistentStateComponent<SettingsConfigurable> {
    @Attribute
    var configValue: String = "default"

    @Attribute
    var isFeatureEnabled: Boolean = false

    @Attribute
    var isKt: Boolean = false

    @Attribute
    var numberOption: Int = 10

    companion object {
        fun getInstance(): SettingsConfigurable = service()
    }

    override fun getState(): SettingsConfigurable = this

    override fun loadState(state: SettingsConfigurable) {
        XmlSerializerUtil.copyBean(state, this)
    }

}
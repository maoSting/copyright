package com.butterfly.copyright


import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.*
import com.intellij.util.xmlb.XmlSerializerUtil
import com.intellij.util.xmlb.annotations.Attribute


@Service(Service.Level.APP)
@State(name = "CopyrightSettings", storages = [Storage("copyright-settings.xml")])
class SettingsConfigurable: PersistentStateComponent<SettingsConfigurable> {
    @Attribute
    var softwareName: String = ""

    @Attribute
    var softwareVersion: String = ""

    @Attribute
    var softwareFiles: String = ""

    @Attribute
    var writeWay: Int = 1

    @Attribute
    var ignoreDirs: String = ""


    companion object {
        fun getInstance(): SettingsConfigurable = service()
    }

    override fun getState(): SettingsConfigurable = this

    override fun loadState(state: SettingsConfigurable) {
        XmlSerializerUtil.copyBean(state, this)
    }

}
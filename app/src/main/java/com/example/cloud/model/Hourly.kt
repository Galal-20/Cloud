// YApi QuickType插件生成，具体参考文档:https://plugins.jetbrains.com/plugin/18847-yapi-quicktype/documentation

package com.example.cloud.model

data class Hourly(
    val city: City,
    val list: List<HourlyListElement>
)

data class HourlyListElement(
    val dt: Long,
    val main: Main,
    val weather: List<Weather>,
    val wind: Wind
)


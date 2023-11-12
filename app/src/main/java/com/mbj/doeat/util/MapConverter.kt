package com.mbj.doeat.util

import com.naver.maps.geometry.LatLng

object MapConverter {

    fun formatLatLng(x: Int?, y: Int?): LatLng {
        val xDouble = (x ?: 0).toDouble() / 10e6
        val yDouble = (y ?: 0).toDouble() / 10e6
        return LatLng(xDouble, yDouble)
    }

    fun removeHtmlTags(input: String): String {
        val result = input.replace(Regex("<[/]?b>"), "")
        return result
    }
}

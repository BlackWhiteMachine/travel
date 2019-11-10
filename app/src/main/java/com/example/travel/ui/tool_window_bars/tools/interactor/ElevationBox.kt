package com.example.travel.ui.tool_window_bars.tools.interactor

data class ElevationBox(val latitudeNorth: Double,
                        val latitudeStep: Double,
                        val longitudeWest: Double,
                        val longitudeStep: Double,
                        val rows: Int,
                        val columns: Int) {
    companion object {
        const val ElevationStep = 25
        const val Size = 32
    }
}
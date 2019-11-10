package com.example.travel.data.preferences

import android.content.Context
import android.util.Log
import com.example.travel.di.ApplicationContext
import com.example.travel.ui.tool_window_bars.tools.interactor.ElevationBox
import javax.inject.Inject

class RectangleParametersStorageSharedPreferences @Inject
constructor(@param:ApplicationContext private val context: Context) : RectangleParametersStorage {

    companion object {
        private val TAG = RectangleParametersStorageSharedPreferences::class.java.simpleName

        private const val ELEVATION_CELLS = "elevation_cells"

        private const val LATITUDE_NORTH = "latitude_north"
        private const val LATITUDE_STEP = "latitude_step"
        private const val LONGITUDE_WEST = "longitude_west"
        private const val LONGITUDE_STEP = "longitude_step"
        private const val ROWS = "rows"
        private const val COLUMNS = "columns"
    }

    override var elevationBox: ElevationBox
        get() = restore()
        set(value) { store(value) }

    private fun restore(): ElevationBox {
        val sharedPreferences = context.getSharedPreferences(ELEVATION_CELLS,
                Context.MODE_PRIVATE)

        val box = ElevationBox(
                sharedPreferences.getInt(LATITUDE_NORTH, 0).toDouble() / 1E7,
                sharedPreferences.getInt(LATITUDE_STEP, 0).toDouble() / 1E7,
                sharedPreferences.getInt(LONGITUDE_WEST, 0).toDouble() / 1E7,
                sharedPreferences.getInt(LONGITUDE_STEP, 0).toDouble() / 1E7,
                sharedPreferences.getInt(ROWS, 0),
                sharedPreferences.getInt(COLUMNS, 0))

        Log.i(TAG, box.toString())

        return box
    }

    private fun store(box: ElevationBox) {
        val sharedPreferences = context.getSharedPreferences(ELEVATION_CELLS,
                Context.MODE_PRIVATE)

        val editor = sharedPreferences.edit()

        editor.putInt(LATITUDE_NORTH, (box.latitudeNorth * 1E7).toInt())
        editor.putInt(LATITUDE_STEP, (box.latitudeStep * 1E7).toInt())
        editor.putInt(LONGITUDE_WEST, (box.longitudeWest * 1E7).toInt())
        editor.putInt(LONGITUDE_STEP, (box.longitudeStep * 1E7).toInt())
        editor.putInt(COLUMNS, box.columns)
        editor.putInt(ROWS, box.rows)

        editor.apply()
    }
}

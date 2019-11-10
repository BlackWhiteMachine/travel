package com.example.travel.data.network

import android.content.Context
import android.location.Location
import android.util.Log
import com.example.travel.data.network.model.Elevations
import com.example.travel.di.ApplicationContext
import com.example.travel.ui.tool_window_bars.tools.interactor.ElevationBox
import com.example.travel.util.AppConstants.BING_ELEVATION_HEIGHTS
import com.example.travel.util.AppConstants.BING_ELEVATION_PATH
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import org.osmdroid.tileprovider.util.ManifestUtil
import retrofit2.Retrofit
import java.util.*
import javax.inject.Inject
import kotlin.math.abs

class BingElevationDataSource @Inject constructor(@ApplicationContext context: Context, retrofit: Retrofit) : ElevationDataSource {

    companion object {
        private val TAG = BingElevationDataSource::class.java.simpleName

        /**
         * the meta com.example.groundcontrolstation.data key in the manifest
         */
        private const val BING_KEY = "BING_KEY"

        /**
         * Bing Map key set by user.
         *
         * @see [http://msdn.microsoft.com/en-us/library/ff428642.aspx](http://msdn.microsoft.com/en-us/library/ff428642.aspx)
         */
        private var bingMapKey = ""
    }

    private val messagesApi: MessagesApi = retrofit.create(MessagesApi::class.java)

    private val downloadBoxSubject: PublishSubject<Array<Double>> = PublishSubject.create()

    init {
        bingMapKey = ManifestUtil.retrieveKey(context, BING_KEY)
    }

    override fun performRequest(latSouth: Double, lonWest: Double, latNorth: Double, lonEast: Double): Observable<List<Int>> {
        val messages = messagesApi.elevations(BING_ELEVATION_PATH,
                String.format(Locale.ENGLISH, "%.7f,%.7f,%.7f,%.7f", latSouth, lonWest, latNorth, lonEast),
                ElevationBox.Size.toString(),
                ElevationBox.Size.toString(),
                BING_ELEVATION_HEIGHTS,
                bingMapKey)

        return messages.flatMap{ elevations: Elevations ->
            var result: Observable<List<Int>> = Observable.empty()

            for (resourceSet in elevations.resourceSets!!) {
                for (resource in resourceSet.resources!!) {
                    result = Observable.just(resource.elevations!!)
                }
            }

            result
        }
    }

    override fun addDownloadArea(latSouth: Double, lonWest: Double, latNorth: Double, lonEast: Double) {
        downloadBoxSubject.onNext(arrayOf(latSouth, lonWest, latNorth, lonEast))
    }

    override fun getDownloadQueue(): Observable<ElevationBox> {
        return downloadBoxSubject.flatMap {
            val latSouth = it[0]
            val lonWest = it[1]
            val latNorth = it[2]
            val lonEast = it[3]

            val distanceY = FloatArray(1)
            Location.distanceBetween(latNorth, lonWest,
                    latSouth, lonWest, distanceY)

            val distanceX = FloatArray(1)
            Location.distanceBetween(latSouth, lonWest,
                    latSouth, lonEast, distanceX)

            var rows = distanceY[0].toInt() / ElevationBox.ElevationStep + 1
            var columns = distanceX[0].toInt() / ElevationBox.ElevationStep + 1

            val latitudeStep = abs(latNorth - latSouth) / rows
            val longitudeStep = abs(lonWest - lonEast) / columns

            val squareXFinal = if (columns % ElevationBox.Size == 0) columns / ElevationBox.Size else 1 + columns / ElevationBox.Size
            val squareYFinal = if (rows % ElevationBox.Size == 0) rows / ElevationBox.Size else 1 + rows / ElevationBox.Size

            columns = squareXFinal * ElevationBox.Size
            rows = squareYFinal * ElevationBox.Size

            val elevationBox = ElevationBox(latNorth, latitudeStep, lonWest, longitudeStep, rows, columns)

            Log.i(TAG, "elevationBox: $elevationBox")

            Observable.just(elevationBox)
        }
    }
}

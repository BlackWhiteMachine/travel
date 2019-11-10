package com.example.travel.util

import org.osmdroid.util.GeoPoint
import java.lang.Math.toDegrees
import java.lang.Math.toRadians

/**
 * The earth's radius, in meters.
 * Mean radius as defined by IUGG.
 */
const val EARTH_RADIUS = 6371009.0

fun computeOffset(from: GeoPoint, _distance: Double, _heading: Double): GeoPoint {
    var distance = _distance
    var heading = _heading
    distance /= EARTH_RADIUS
    heading = toRadians(heading)
    // http://williams.best.vwh.net/avform.htm#LL
    val fromLat = toRadians(from.latitude)
    val fromLng = toRadians(from.longitude)
    val cosDistance = kotlin.math.cos(distance)
    val sinDistance = kotlin.math.sin(distance)
    val sinFromLat = kotlin.math.sin(fromLat)
    val cosFromLat = kotlin.math.cos(fromLat)
    val sinLat = cosDistance * sinFromLat + sinDistance * cosFromLat * kotlin.math.cos(heading)
    val dLng = kotlin.math.atan2(
            sinDistance * cosFromLat * kotlin.math.sin(heading),
            cosDistance - sinFromLat * sinLat)
    return GeoPoint(toDegrees(kotlin.math.asin(sinLat)), toDegrees(fromLng + dLng))
}

package com.example.travel.ui.map.view

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.travel.R
import com.example.travel.ui.base.view.BaseFragment
import com.example.travel.ui.map.presenter.MapMvpPresenter
import com.example.travel.util.isPermissionGranted
import com.example.travel.util.requestPermission
import kotlinx.android.synthetic.main.fragment_map.*
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.events.MapListener
import org.osmdroid.events.ScrollEvent
import org.osmdroid.events.ZoomEvent
import org.osmdroid.tileprovider.cachemanager.CacheManager
import org.osmdroid.tileprovider.tilesource.TileSourcePolicyException
import org.osmdroid.tileprovider.tilesource.bing.BingMapTileSource
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.OverlayWithIW
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.views.overlay.infowindow.InfoWindow
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import java.util.*
import javax.inject.Inject

class MapFragment : BaseFragment(), MapMvpView {

    companion object {
        private val TAG = MapFragment::class.java.simpleName

        const val LOCATION_PERMISSION_REQUEST_CODE = 1

        fun newInstance(): MapFragment {
            val args = Bundle()
            val fragment = MapFragment()
            fragment.arguments = args
            return fragment
        }
    }

    @Inject internal lateinit var presenter: MapMvpPresenter<MapMvpView>

    private lateinit var cacheManager: CacheManager
    private lateinit var onMarkerClickListener: Marker.OnMarkerClickListener
    private lateinit var locationOverlay: MyLocationNewOverlay
    private lateinit var overlayWithIWMap: MutableMap<Long, OverlayWithIW>

    private var permissionDenied = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_map, container, false)
        presenter.onAttach(this)
        return view
    }

    override fun onResume() {
        super.onResume()

        mapView.onResume()
    }

    override fun onPause() {
        mapView.onPause()

        super.onPause()
    }

    override fun onDestroyView() {
        presenter.onDetach()
        super.onDestroyView()
    }

    override fun setUp(view: View) {
        overlayWithIWMap = TreeMap()

        locationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(context), mapView)
        locationOverlay.enableMyLocation()
        locationOverlay.isDrawAccuracyEnabled = true
        mapView.overlays.add(locationOverlay)

        mapView.zoomController.setVisibility(CustomZoomButtonsController.Visibility.ALWAYS)

        BingMapTileSource.retrieveBingKey(context)
        val bingMapTileSource = BingMapTileSource(null)
        bingMapTileSource.style = BingMapTileSource.IMAGERYSET_AERIALWITHLABELS

        mapView.setTileSource(bingMapTileSource)

        try {
            cacheManager = CacheManager(bingMapTileSource, mapView.tileProvider.tileWriter, 1, 18)
        } catch (e: TileSourcePolicyException) {
            e.printStackTrace()
        }

        mapView.setMultiTouchControls(true)

        mapView.controller.setZoom(15.0)

        val receive = object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(p: GeoPoint): Boolean {
                presenter.onMapClick(p)
                return false
            }

            override fun longPressHelper(p: GeoPoint ): Boolean {
                return false
            }
        }

        val overlayEvents = MapEventsOverlay(receive)
        mapView.overlays.add(overlayEvents)

        mapView.addMapListener(object : MapListener {
            override fun onScroll(event: ScrollEvent): Boolean {
                presenter.onBoundingBoxChanged(mapView.boundingBox)
                return false
            }

            override fun onZoom(event: ZoomEvent): Boolean {
                presenter.onBoundingBoxChanged(mapView.boundingBox)
                return false
            }
        })

        onMarkerClickListener = Marker.OnMarkerClickListener { marker, _ ->
            marker.showInfoWindow()
            presenter.onMarkerClick(java.lang.Long.parseLong(marker.getId()))
            false
        }

        enableMyLocation()
        myLocationImageButton.setOnClickListener{ presenter.onMyLocationButtonClick() }
        presenter.onViewInitialized()
    }

    override fun getBoundingBox(): BoundingBox = mapView.boundingBox

    override fun addMarker(id: Long, geoPoint: GeoPoint) {
        val marker = Marker(mapView)
        marker.position = geoPoint
        marker.setOnMarkerClickListener(onMarkerClickListener)

        mapView.overlays.add(marker)

        overlayWithIWMap[id] = marker

        marker.id = id.toString()
    }

    override fun showInfoWindowMarker(id: Long) {
        val marker = overlayWithIWMap[id] as Marker?

        marker?.showInfoWindow()
    }

    override fun setMarkerDraggable(id: Long, isDraggable: Boolean) {
        val marker = overlayWithIWMap[id] as Marker?

        marker?.isDraggable = isDraggable
    }

    override fun setMarkerDragListener(id: Long, listener: Marker.OnMarkerDragListener?) {
        val marker = overlayWithIWMap[id] as Marker?

        marker?.setOnMarkerDragListener(listener)
    }

    override fun updateMarkerTitle(id: Long, title: String) {
        val marker = overlayWithIWMap[id] as Marker?

        marker?.let {
            marker.title = title
            marker.showInfoWindow()
        }
    }

    override fun removeMarker(id: Long) {
        val marker = overlayWithIWMap.remove(id) as Marker?

        if (marker != null) {
            if (marker.isInfoWindowShown) {
                InfoWindow.closeAllInfoWindowsOn(mapView)
            }

            mapView.overlays.remove(marker)
        }
    }

    override fun addLine(id: Long, color: Int, width: Float) {
        val line = Polyline()

        line.width = width
        line.color = context!!.getColor(color)

        mapView.overlays.add(line)

        overlayWithIWMap[id] = line

        line.id = id.toString()
    }

    override fun updateLinePoints(id: Long, mapPointsList: List<GeoPoint>) {
        val line = overlayWithIWMap[id] as Polyline?

        line?.setPoints(mapPointsList)
    }

    override fun removeLine(id: Long) {
        val polyline = overlayWithIWMap.remove(id) as Polyline?

        mapView.overlays.remove(polyline)
    }

    override fun downloadVisibleTiles(boundingBox: BoundingBox, startZoomLevelValue: Int, finishZoomLevelValue: Int) {
        cacheManager.downloadAreaAsync(context, boundingBox, startZoomLevelValue, finishZoomLevelValue)
    }

    override fun invalidate() {
        mapView.invalidate()
    }

    private fun enableMyLocation() {
        if (ContextCompat.checkSelfPermission(context!!, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            requestPermission(baseActivity!!, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true)
        }
    }

    override fun setFocusOnMyLocation() {
        if (ActivityCompat.checkSelfPermission(context!!, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context!!, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }

        mapView.controller.animateTo(locationOverlay.myLocation)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return
        }

        if (isPermissionGranted(permissions, grantResults,
                        Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation()
        } else {
            // Display the missing permission error dialog when the fragments resume.
            permissionDenied = true
        }
    }
}

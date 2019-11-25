package com.example.travel.ui.tool_window_bars.tools.view

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.travel.R
import com.example.travel.ui.base.view.BaseFragment
import com.example.travel.ui.dialogs.download.view.DownloadTilesDialog
import com.example.travel.ui.tool_window_bars.tools.presenter.ToolsMvpPresenter
import com.example.travel.ui.tool_window_bars.tools.presenter.ToolsPresenter
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.DataPointInterface
import com.jjoe64.graphview.series.LineGraphSeries
import kotlinx.android.synthetic.main.fragment_tools.*
import javax.inject.Inject

class ToolsFragment : BaseFragment(), ToolsMvpView {

    companion object {
        private val TAG = ToolsFragment::class.java.simpleName

        fun newInstance(): ToolsFragment {
            val args = Bundle()
            val fragment = ToolsFragment()
            fragment.arguments = args
            return fragment
        }
    }

    @Inject
    internal lateinit var presenter: ToolsMvpPresenter<ToolsMvpView>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_tools, container, false)
        presenter.onAttach(this)
        return view
    }

    override fun onResume() {
        super.onResume()

        presenter.resume()
    }

    override fun onPause() {
        presenter.pause()

        super.onPause()
    }

    override fun setUp(view: View) {
        buttonAdd.setBackgroundColor(Color.TRANSPARENT)
        buttonClean.setBackgroundColor(Color.TRANSPARENT)

        // activate horizontal zooming and scrolling
        elevationProfile.viewport.isScalable = true
        elevationProfile.viewport.setScalableY(true)
        elevationProfile.viewport.calcCompleteRange()
        //   graph.

        downloadElevationsProgressBar.visibility = View.GONE
        downloadElevationsTextView.visibility = View.GONE

        buttonAdd.setOnClickListener{ presenter.onAddButtonClick() }
        buttonClean.setOnClickListener{ presenter.onClearButtonClick() }
        downloadTilesButton.setOnClickListener{ presenter.buttonDownloadClick() }

        presenter.onViewInitialized()
    }

    override fun setAddButtonColor(color: Int) {
        buttonAdd.setBackgroundColor(color)
    }

    override fun updateDistance(value: String) {
        distanceTextView.text = value
    }

    override fun changeProfileElevation(ways: List<ToolsPresenter.WayInfo>) {
        fun <T : DataPointInterface> drawSeparator(data: Array<T>){
            val seriesSeparator = LineGraphSeries(data)
            seriesSeparator.color = -0x666667
            seriesSeparator.thickness = 3

            elevationProfile.addSeries(seriesSeparator)
        }

        var x = 0.0

        for (way in ways) {

            if (way.elevations != null && way.elevations!!.isNotEmpty()) {
                val points = arrayOfNulls<DataPoint>(way.elevations!!.size)

                val step = way.distance / way.elevations!!.size

                for (iter in way.elevations!!.indices) {
                    points[iter] = DataPoint(x, way.elevations!![iter].toDouble())

                    x += step
                }

                x -= step

                val series = LineGraphSeries<DataPoint>(points)
                series.color = -0x666667
                series.thickness = 2
                elevationProfile.addSeries(series)

                val elevation = way.elevations!!.last()

                drawSeparator(arrayOf(
                        DataPoint(x, (elevation - 5).toDouble()),
                        DataPoint(x, (elevation + 10).toDouble())))
            } else {
                x += way.distance

                drawSeparator(arrayOf(
                        DataPoint(x, elevationProfile.viewport.getMinY(true)),
                        DataPoint(x, elevationProfile.viewport.getMaxY(false))))
            }

            elevationProfile.computeScroll()
        }

        if (ways.isNotEmpty()) {
            val way = ways.first()

            if (way.elevations != null && way.elevations!!.isNotEmpty()) {
                val elevation = way.elevations!!.last()

                drawSeparator(arrayOf(
                        DataPoint(0.0, (elevation - 5).toDouble()),
                        DataPoint(0.0, (elevation + 10).toDouble())))
            } else {
                drawSeparator(arrayOf(
                        DataPoint(0.0, elevationProfile.viewport.getMinY(true)),
                        DataPoint(0.0, elevationProfile.viewport.getMaxY(true))))
            }
        }

        elevationProfile.viewport.apply {
            setMaxX(x)
            setMinY(getMinY(true) * 0.9)
            setMaxY(getMaxY(true) * 1.1)
        }
    }

    override fun clearProfileElevation() {
        elevationProfile.viewport.setMaxX(0.0)
        elevationProfile.removeAllSeries()
    }

    override fun showSelectLevelsDialog() {
        DownloadTilesDialog.newInstance().show(fragmentManager!!)
    }

    override fun showCancelDownloadDialog() {

    }

    override fun showProgressDownload() {
        downloadElevationsProgressBar.visibility = View.VISIBLE
        downloadElevationsTextView.visibility = View.VISIBLE

        downloadTilesButton.setText(R.string.tools_tab_cancel)
    }

    override fun updateProgressDownload(progressStr: String, squareNumberCurrent: Int, squareNumberFinal: Int) {
        if (downloadElevationsProgressBar.max != squareNumberFinal) {
            downloadElevationsProgressBar.max = squareNumberFinal
        }

        downloadElevationsTextView.text = progressStr
        downloadElevationsProgressBar.progress = squareNumberCurrent
    }

    override fun changeProgressDownload(messageId: Int) {
        downloadElevationsTextView.setText(messageId)
        downloadTilesButton.setText(R.string.tools_tab_download)

        downloadElevationsProgressBar.postDelayed({ downloadElevationsProgressBar.visibility = View.GONE }, 5000)

        downloadElevationsTextView.postDelayed({ downloadElevationsTextView.visibility = View.GONE }, 5000)
    }

    override fun onDestroyView() {
        presenter.onDetach()
        super.onDestroyView()
    }
}

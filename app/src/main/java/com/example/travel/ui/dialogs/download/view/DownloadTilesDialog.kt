package com.example.travel.ui.dialogs.download.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.example.travel.R
import com.example.travel.ui.base.view.BaseDialog
import com.example.travel.ui.dialogs.download.presenter.DownloadTilesMvpPresenter
import kotlinx.android.synthetic.main.dialog_download_tiles.*
import javax.inject.Inject

class DownloadTilesDialog : BaseDialog(), DownloadTilesMvpView {

    companion object {

        private val TAG = DownloadTilesDialog::class.java.simpleName

        fun newInstance(): DownloadTilesDialog {
            val fragment = DownloadTilesDialog()
            val bundle = Bundle()
            fragment.arguments = bundle
            return fragment
        }
    }

    @Inject
    internal lateinit var presenter: DownloadTilesMvpPresenter<DownloadTilesMvpView>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.dialog_download_tiles, container, false)
        presenter.onAttach(this)
        return view
    }

    override fun setUp(view: View) {
        startZoomLevel.apply {
            minValue = 1
            maxValue = 19
            value = 4
        }

        finishZoomLevel.apply {
            minValue = 1
            maxValue = 19
            value = 15
        }

        okButton.setOnClickListener{ presenter.onOkButtonClick(startZoomLevel.value, finishZoomLevel.value) }
        cancelButton.setOnClickListener{ presenter.onCancelButtonClick() }
    }

    fun show(fragmentManager: FragmentManager) {
        super.show(fragmentManager, TAG)
    }

    override fun dismissDialog() {
        super.dismissDialog(TAG)
    }
}

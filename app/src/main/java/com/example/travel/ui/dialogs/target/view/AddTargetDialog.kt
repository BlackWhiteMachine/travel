package com.example.travel.ui.dialogs.target.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.travel.R
import com.example.travel.ui.base.view.BaseDialog
import com.example.travel.ui.dialogs.target.presenter.AddTargetMvpPresenter
import kotlinx.android.synthetic.main.dialog_add_target.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class AddTargetDialog : BaseDialog(), AddTargetMvpView {

    companion object {

        private val TAG = AddTargetDialog::class.java.simpleName

        private const val TARGET_LATITUDE = "target_latitude"
        private const val TARGET_LONGITUDE = "target_longitude"

        fun newInstance(latitude: Double, longitude: Double): AddTargetDialog {
            val fragment = AddTargetDialog()
            val bundle = Bundle()
            bundle.putDouble(TARGET_LATITUDE, latitude)
            bundle.putDouble(TARGET_LONGITUDE, longitude)
            fragment.arguments = bundle
            return fragment
        }
    }

    @Inject
    internal lateinit var presenter: AddTargetMvpPresenter<AddTargetMvpView>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.dialog_add_target, container, false)
        presenter.onAttach(this)
        return view
    }

    override fun setUp(view: View) {
        val date = Date()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.US)

        presenter.setTargetName(dateFormat.format(date))

        val latitude = arguments!!.getDouble(TARGET_LATITUDE)
        val longitude = arguments!!.getDouble(TARGET_LONGITUDE)

        presenter.setTargetPosition(latitude, longitude)
        presenter.onViewInitialized()
    }

    override fun setName(name: String) {
        targetNameEditText.setText(name)
    }

    override fun setLatitude(latitude: String) {
        latitudeEditText.setText(latitude)
    }

    override fun setLongitude(longitude: String) {
        longitudeEditText.setText(longitude)
    }
}

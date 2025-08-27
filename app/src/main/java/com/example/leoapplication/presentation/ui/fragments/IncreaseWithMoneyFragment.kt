//package com.example.leoapplication.presentation.ui.fragments
//
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.fragment.app.Fragment
//import com.example.leoapplication.R
//import com.google.android.gms.maps.CameraUpdateFactory
//import com.google.android.gms.maps.GoogleMap
//import com.google.android.gms.maps.OnMapReadyCallback
//import com.google.android.gms.maps.SupportMapFragment
//import com.google.android.gms.maps.model.LatLng
//import com.google.android.gms.maps.model.MarkerOptions
//import dagger.hilt.android.AndroidEntryPoint
//
//@AndroidEntryPoint
//class IncreaseWithMoneyFragment : Fragment(), OnMapReadyCallback {
//
//    private lateinit var mMap: GoogleMap
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        val view = inflater.inflate(R.layout.fragment_increase_with_money, container, false)
//
//        val mapFragment = childFragmentManager
//            .findFragmentById(R.id.map) as SupportMapFragment
//        mapFragment.getMapAsync(this)
//
//        return view
//    }
//
//    override fun onMapReady(googleMap: GoogleMap) {
//        mMap = googleMap
//
//        // Mərkəz: Bakı
//        val baku = LatLng(40.4093, 49.8671)
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(baku, 13f))
//
//        // Statik ATM markerləri
//        val atm1 = LatLng(40.4150, 49.8560)
//        val atm2 = LatLng(40.4040, 49.8740)
//        val atm3 = LatLng(40.4120, 49.8600)
//
//        mMap.addMarker(MarkerOptions().position(atm1).title("ATM 1"))
//        mMap.addMarker(MarkerOptions().position(atm2).title("ATM 2"))
//        mMap.addMarker(MarkerOptions().position(atm3).title("ATM 3"))
//    }
//}

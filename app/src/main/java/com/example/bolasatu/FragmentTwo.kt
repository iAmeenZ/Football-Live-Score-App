package com.example.bolasatu

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.bolasatu.databinding.FragmentTwoBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.match_box.view.*
import kotlinx.android.synthetic.main.match_box_row.view.*
import java.text.SimpleDateFormat
import java.util.*


class FragmentTwo : Fragment() {

    @SuppressLint("UseCompatLoadingForDrawables", "SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentTwoBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_two, container, false)
        val linear: LinearLayout = binding.layoutList

        matchBoxList(inflater, binding, linear)

        binding.swipeRefresh.setOnRefreshListener {
            linear.removeAllViews()
            matchBoxList(inflater, binding, linear)
        }

        return binding.root
    }

    private fun matchBoxList(inflater: LayoutInflater, binding: FragmentTwoBinding, linear: LinearLayout) {
        binding.dateList


        val database = FirebaseDatabase.getInstance().getReference("Keputusan")
        val getData = object : ValueEventListener {
            @SuppressLint("InflateParams", "InlinedApi", "SimpleDateFormat", "SetTextI18n")
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onDataChange(snapshot: DataSnapshot) {

                val timeZone: TimeZone = TimeZone.getTimeZone("GMT+08:00")
                val calendar: Calendar = Calendar.getInstance()
                val sdf = SimpleDateFormat("yyyyMMdd HH:mm")
                sdf.timeZone = timeZone
                val currentDateTime = sdf.format(calendar.time)

                var viewer: View

                if (snapshot.hasChildren()) {

                    snapshot.children.forEach { dataD ->

                        dataD.children.forEach { dataP ->

                            viewer = inflater.inflate(R.layout.match_box, null)

                            val bulan = when (dataD.key?.substring(10, 12)) {
                                "01" -> "Januari"
                                "02" -> "Februari"
                                "03" -> "Mac"
                                "04" -> "April"
                                "05" -> "Mei"
                                "06" -> "Jun"
                                "07" -> "Julai"
                                "08" -> "Ogos"
                                "09" -> "September"
                                "10" -> "Oktober"
                                "11" -> "November"
                                else -> "Disember"
                            }

                            when {
                                currentDateTime.substring(0, 8) == dataD.key?.substring(6, 14) -> {
                                    viewer.dateView.text = "Hari Ini"
                                    viewer.dateView.setTextColor(Color.parseColor("#177E01"))
                                }
                                currentDateTime.substring(0, 8).toInt() - 1 == dataD.key?.substring(6, 14).toString().toInt() -> {
                                    viewer.dateView.text = "Semalam"
                                    viewer.dateView.setTextColor(Color.parseColor("#105900"))
                                }
                                else -> viewer.dateView.text = "${dataD.key?.substring(14)}, ${dataD.key?.substring(12, 14)} $bulan"
                            }

                            val drawablePiala: Drawable? = if (dataP.key == "aLiga Super") {
                                context?.let { ContextCompat.getDrawable(it, R.drawable.ligasuper) }
                            } else {
                                context?.let { ContextCompat.getDrawable(it, R.drawable.ligaperdana) }
                            }
                            viewer.pialaImageView.setImageDrawable(drawablePiala)
                            viewer.pialaTextView.text = "${dataP.key?.substring(1)}"

                            dataP.children.forEach { dataT ->

                                dataT.children.forEach { data ->

                                    val viewerRow = inflater.inflate(R.layout.match_box_row, null)
                                    val linearRow: LinearLayout = viewer.matchBoxRow

                                    val ringkas1 = when (data.child("team1").value) {
                                        "Johor Darul Ta'zim" -> "JDT"
                                        "Johor Darul Ta'zim II" -> "JDT II"
                                        "Negeri Sembilan" -> "N. Sembilan"
                                        else -> data.child("team1").value
                                    }
                                    val ringkas2 = when (data.child("team2").value) {
                                        "Johor Darul Ta'zim" -> "JDT"
                                        "Johor Darul Ta'zim II" -> "JDT II"
                                        "Negeri Sembilan" -> "N. Sembilan"
                                        else -> data.child("team2").value
                                    }
                                    viewerRow.firstView.text = "$ringkas1"
                                    viewerRow.secondView.text = "$ringkas2"
                                    viewerRow.timeView.text = "${data.child("skor1").value} - ${data.child("skor2").value}"

                                    var drawable1: Drawable? = null
                                    var drawable2: Drawable? = null

                                    when (data.child("team1").value) {
                                        "PDRM" -> drawable1 = context?.let { ContextCompat.getDrawable(it, R.drawable.pdrm)}
                                        "Selangor" -> drawable1 = context?.let { ContextCompat.getDrawable(it, R.drawable.selangor)}
                                        "Felda United" -> drawable1 = context?.let { ContextCompat.getDrawable(it, R.drawable.felda)}
                                        "Johor Darul Ta'zim" -> drawable1 = context?.let { ContextCompat.getDrawable(it, R.drawable.jdt)}
                                        "Melaka United" -> drawable1 = context?.let { ContextCompat.getDrawable(it, R.drawable.melaka)}
                                        "Pahang" -> drawable1 = context?.let { ContextCompat.getDrawable(it, R.drawable.pahang)}
                                        "Kedah" -> drawable1 = context?.let { ContextCompat.getDrawable(it, R.drawable.kedah)}
                                        "Perak" -> drawable1 = context?.let { ContextCompat.getDrawable(it, R.drawable.perak)}
                                        "PJ City FC" -> drawable1 = context?.let { ContextCompat.getDrawable(it, R.drawable.petalingjaya)}
                                        "Sabah FA" -> drawable1 = context?.let { ContextCompat.getDrawable(it, R.drawable.sabah)}
                                        "Terengganu" -> drawable1 = context?.let { ContextCompat.getDrawable(it, R.drawable.terengganu) }
                                        "UiTM" -> drawable1 = context?.let { ContextCompat.getDrawable(it, R.drawable.uitm)}
                                        "Pulau Pinang" -> drawable1 = context?.let { ContextCompat.getDrawable(it, R.drawable.penang)}
                                        "Terengganu II" -> drawable1 = context?.let { ContextCompat.getDrawable(it, R.drawable.terengganu2)}
                                        "Kuala Lumpur" -> drawable1 = context?.let { ContextCompat.getDrawable(it, R.drawable.kl)}
                                        "Johor Darul Ta'zim II" -> drawable1 = context?.let { ContextCompat.getDrawable(it, R.drawable.jdt2)}
                                        "Kelantan" -> drawable1 = context?.let { ContextCompat.getDrawable(it, R.drawable.kelantan)}
                                        "Kuching FA" -> drawable1 = context?.let { ContextCompat.getDrawable(it, R.drawable.kuching)}
                                        "Selangor II" -> drawable1 = context?.let { ContextCompat.getDrawable(it, R.drawable.selangor2)}
                                        "Kelantan United" -> drawable1 = context?.let { ContextCompat.getDrawable(it, R.drawable.kelantanunited)}
                                        "UKM" -> drawable1 = context?.let { ContextCompat.getDrawable(it, R.drawable.ukm)}
                                        "Sarawak United" -> drawable1 = context?.let { ContextCompat.getDrawable(it, R.drawable.sarawakunited)}
                                        "Negeri Sembilan" -> drawable1 = context?.let { ContextCompat.getDrawable(it, R.drawable.n9)}
                                        "Perak II" -> drawable1 = context?.let { ContextCompat.getDrawable(it, R.drawable.perak2)}
                                    }

                                    when (data.child("team2").value) {
                                        "PDRM" -> drawable2 = context?.let { ContextCompat.getDrawable(it, R.drawable.pdrm)}
                                        "Selangor" -> drawable2 = context?.let { ContextCompat.getDrawable(it, R.drawable.selangor)}
                                        "Felda United" -> drawable2 = context?.let { ContextCompat.getDrawable(it, R.drawable.felda)}
                                        "Johor Darul Ta'zim" -> drawable2 = context?.let { ContextCompat.getDrawable(it, R.drawable.jdt)}
                                        "Melaka United" -> drawable2 = context?.let { ContextCompat.getDrawable(it, R.drawable.melaka)}
                                        "Pahang" -> drawable2 = context?.let { ContextCompat.getDrawable(it, R.drawable.pahang)}
                                        "Kedah" -> drawable2 = context?.let { ContextCompat.getDrawable(it, R.drawable.kedah)}
                                        "Perak" -> drawable2 = context?.let { ContextCompat.getDrawable(it, R.drawable.perak)}
                                        "PJ City FC" -> drawable2 = context?.let { ContextCompat.getDrawable(it, R.drawable.petalingjaya)}
                                        "Sabah FA" -> drawable2 = context?.let { ContextCompat.getDrawable(it, R.drawable.sabah)}
                                        "Terengganu" -> drawable2 = context?.let { ContextCompat.getDrawable(it, R.drawable.terengganu) }
                                        "UiTM" -> drawable2 = context?.let { ContextCompat.getDrawable(it, R.drawable.uitm)}
                                        "Pulau Pinang" -> drawable2 = context?.let { ContextCompat.getDrawable(it, R.drawable.penang)}
                                        "Terengganu II" -> drawable2 = context?.let { ContextCompat.getDrawable(it, R.drawable.terengganu2)}
                                        "Kuala Lumpur" -> drawable2 = context?.let { ContextCompat.getDrawable(it, R.drawable.kl)}
                                        "Johor Darul Ta'zim II" -> drawable2 = context?.let { ContextCompat.getDrawable(it, R.drawable.jdt2)}
                                        "Kelantan" -> drawable2 = context?.let { ContextCompat.getDrawable(it, R.drawable.kelantan)}
                                        "Kuching FA" -> drawable2 = context?.let { ContextCompat.getDrawable(it, R.drawable.kuching)}
                                        "Selangor II" -> drawable2 = context?.let { ContextCompat.getDrawable(it, R.drawable.selangor2)}
                                        "Kelantan United" -> drawable2 = context?.let { ContextCompat.getDrawable(it, R.drawable.kelantanunited)}
                                        "UKM" -> drawable2 = context?.let { ContextCompat.getDrawable(it, R.drawable.ukm)}
                                        "Sarawak United" -> drawable2 = context?.let { ContextCompat.getDrawable(it, R.drawable.sarawakunited)}
                                        "Negeri Sembilan" -> drawable2 = context?.let { ContextCompat.getDrawable(it, R.drawable.n9)}
                                        "Perak II" -> drawable2 = context?.let { ContextCompat.getDrawable(it, R.drawable.perak2)}
                                    }

                                    viewerRow.firstImageView.setImageDrawable(drawable1)
                                    viewerRow.secondImageView.setImageDrawable(drawable2)

                                    linearRow.addView(viewerRow)

                                } // end of forEach - data


                            } // end of forEach - dataT

                            linear.addView(viewer)

                        } // end of forEach - dataP

                    } // end of forEach - dataD

                } // end of if (snapshot.hasChildren())
                binding.swipeRefresh.isRefreshing = false

            } // end of snapshot

            override fun onCancelled(error: DatabaseError) {

            }

        }

        //database.addValueEventListener(getData) // Realtime firebase update to apps
        database.addListenerForSingleValueEvent(getData) // Not realtime firebase update to apps (need refresh)

    }

}


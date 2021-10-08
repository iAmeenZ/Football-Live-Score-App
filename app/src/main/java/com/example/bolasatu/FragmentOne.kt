package com.example.bolasatu

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ImageSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.DataBindingUtil.inflate
import androidx.fragment.app.Fragment
import com.example.bolasatu.databinding.FragmentOneBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.match_box.view.*
import kotlinx.android.synthetic.main.match_box_row.view.*
import java.text.SimpleDateFormat
import java.util.*

class FragmentOne : Fragment() {

    @SuppressLint("UseCompatLoadingForDrawables", "SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentOneBinding = inflate(inflater, R.layout.fragment_one, container, false)
        val linear: LinearLayout = binding.layoutList


        binding.swipeRefresh.isRefreshing
        matchBoxList(inflater, binding, linear)
        binding.swipeRefresh.isRefreshing

        binding.swipeRefresh.setOnRefreshListener {
            linear.removeAllViews()
            matchBoxList(inflater, binding, linear)

        }

        return binding.root
    }

    private fun matchBoxList(inflater: LayoutInflater, binding: FragmentOneBinding, linear: LinearLayout) {

        binding.dateList

        val database = FirebaseDatabase.getInstance().reference
        val getData = object : ValueEventListener {
            @SuppressLint("InflateParams", "InlinedApi", "SimpleDateFormat", "SetTextI18n")
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onDataChange(snapshot: DataSnapshot) {

                var viewer: View

                val timeZone: TimeZone = TimeZone.getTimeZone("GMT+08:00")
                val calendar: Calendar = Calendar.getInstance()
                val sdf = SimpleDateFormat("yyyyMMdd HH:mm")
                sdf.timeZone = timeZone
                val currentDateTime = sdf.format(calendar.time)

                //https://www.codota.com/code/java/methods/java.util.TimeZone/getTimeZone

                val sdf2 = SimpleDateFormat("HHmm")
                sdf2.timeZone = timeZone
                val currentDateTime2 = sdf2.format(calendar.time)


                val aLigaSuper = snapshot.child("Akan Datang")
                if (aLigaSuper.hasChildren()) {

                    aLigaSuper.children.forEach { dataD ->

                        dataD.children.forEach { dataP ->

                            viewer = inflater.inflate(R.layout.match_box, null)

                            val bulan = when (dataD.key?.substring(4, 6)) {
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
                                currentDateTime.substring(0, 8) == dataD.key?.substring(0, 8) -> {
                                    viewer.dateView.text = "Hari Ini"
                                    viewer.dateView.setTextColor(Color.parseColor("#177E01"))
                                }
                                currentDateTime.substring(0, 8).toInt() + 1 == dataD.key?.substring(0, 8).toString().toInt() -> {
                                    viewer.dateView.text = "Esok"
                                    viewer.dateView.setTextColor(Color.parseColor("#105900"))
                                }
                                currentDateTime.substring(0, 8).toInt() - 1 == dataD.key?.substring(0, 8).toString().toInt() -> viewer.dateView.text = "Semalam"
                                else -> viewer.dateView.text = "${dataD.key?.substring(8)}, ${dataD.key?.substring(6, 8)} $bulan"
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
                                    viewerRow.timeView.text = "${data.child("time").value}"

                                    val tempDate = data.child("date").value
                                    val tempTime = data.child("time").value
                                    val eachDateTime = "$tempDate $tempTime"

                                    var drawable1: Drawable? = null
                                    var drawable2: Drawable? = null
                                    val drawableTime: Drawable? = context?.let { ContextCompat.getDrawable(it, R.drawable.transparent) }
                                    var drawableRedDot: Drawable?

                                    val currentTime = currentDateTime2.toInt()
                                    val extract1 = dataT.key?.substring(0,2)
                                    val extract2 = dataT.key?.substring(3,5)
                                    val extract = "$extract1$extract2".toInt()
                                    var movingTime = currentTime - extract
                                    val getTimeRange = currentDateTime2.substring(0,2).toInt() - extract1?.toInt()!!
                                    movingTime -= (getTimeRange * 40)
                                    val drawableLiveTime: Drawable? = context?.let { ContextCompat.getDrawable(it, R.drawable.transparent) }


                                    // =============================  S E D A N G   B E R L A N G S U N G  ===================================================
                                    if (currentDateTime >= eachDateTime && currentDateTime.substring(0,8) == data.child("date").value) {

                                        val dataK1 = snapshot.child("Kedudukan").child("${dataP.key?.substring(1)}").child("${data.child("team1").value}")
                                        val dataK2 = snapshot.child("Kedudukan").child("${dataP.key?.substring(1)}").child("${data.child("team2").value}")

                                        if (data.child("langsung").value == "0") {
                                            if (dataP.key == "bLiga Super" || dataP.key == "eLiga Premier" || dataP.key == "fLiga M3" || dataP.key == "gPiala Presiden" || dataP.key == "hPiala Belia" || dataP.key == "iLiga M4") {
                                                val setLangsungDanP = FragmentThree.Game("${data.child("date").value}", "${data.child("hari").value}", "${data.child("time").value}", "${data.child("team1").value}", "${data.child("team2").value}", "1", "0", "0", "${data.child("tm1").value}", "${data.child("tm2").value}", "${dataK1.child("p").value}".toInt(), "${dataK2.child("p").value}".toInt(), "${dataK1.child("m").value}".toInt(), "${dataK2.child("m").value}".toInt(), "${dataK1.child("s").value}".toInt(), "${dataK2.child("s").value}".toInt(), "${dataK1.child("k").value}".toInt(), "${dataK2.child("k").value}".toInt(), "${dataK1.child("mata").value}".toInt(), "${dataK2.child("mata").value}".toInt())
                                                database.child("Akan Datang").child("${dataD.key}").child("${dataP.key}").child("${dataT.key}").child("${data.key}").setValue(setLangsungDanP)
                                                val addP1 = dataK1.child("p").value.toString().toInt() + 1
                                                val addP2 = dataK2.child("p").value.toString().toInt() + 1
                                                val addMata1 = dataK1.child("mata").value.toString().toInt() + 1 // Auto Draw 0-0
                                                val addMata2 = dataK2.child("mata").value.toString().toInt() + 1 // Auto Draw 0-0
                                                val addS1 = dataK1.child("s").value.toString().toInt() + 1
                                                val addS2 = dataK2.child("s").value.toString().toInt() + 1
                                                val updateKedudukanT1 = FragmentThree.Kedudukan(addP1, dataK1.child("m").value.toString().toInt(), addS1, dataK1.child("k").value.toString().toInt(), dataK1.child("j").value.toString().toInt(), dataK1.child("b").value.toString().toInt(), dataK1.child("bg").value.toString().toInt(), addMata1)
                                                val updateKedudukanT2 = FragmentThree.Kedudukan(addP2, dataK2.child("m").value.toString().toInt(), addS2, dataK2.child("k").value.toString().toInt(), dataK2.child("j").value.toString().toInt(), dataK2.child("b").value.toString().toInt(), dataK2.child("bg").value.toString().toInt(), addMata2)
                                                database.child("Kedudukan").child("${dataP.key?.substring(1)}").child("${data.child("team1").value}").setValue(updateKedudukanT1)
                                                database.child("Kedudukan").child("${dataP.key?.substring(1)}").child("${data.child("team2").value}").setValue(updateKedudukanT2)
                                            }
                                        }


                                        viewerRow.liveRedDot.setImageDrawable(context?.let { ContextCompat.getDrawable(it, R.drawable.reddot) })
                                        viewerRow.timeView.text = "${data.child("skor1").value} - ${data.child("skor2").value}"

                                        drawableRedDot = context?.let { ContextCompat.getDrawable(it, R.drawable.reddot) }
                                        val tm1Moving = data.child("tm1").value?.toString()?.toInt()
                                        val tm2Moving = data.child("tm2").value?.toString()?.toInt()

                                        if (tm1Moving != null) {
                                            if (tm2Moving != null) {
                                                when (movingTime) {
                                                    in 0..45 -> viewerRow.liveTimeView.text = "$movingTime'"
                                                    in 46..46 + tm1Moving -> viewerRow.liveTimeView.text = "45`+"
                                                    in 47 + tm1Moving..47 + tm1Moving + 15 -> viewerRow.liveTimeView.text = "SM"
                                                    in 48 + tm1Moving + 15..48 + tm1Moving + 15 + 45 -> {
                                                        movingTime = movingTime - tm1Moving - 18
                                                        viewerRow.liveTimeView.text = "$movingTime`"
                                                    }
                                                    in 49 + tm1Moving + 15 + 45..49 + tm1Moving + 15 + 45 + tm2Moving -> {
                                                        movingTime = movingTime - tm1Moving - 15
                                                        viewerRow.liveTimeView.text = "90`+"
                                                    }
                                                    in 50 + tm1Moving + 15 + 45 + tm2Moving..50 + tm1Moving + 15 + 45 + tm2Moving + 2 -> {    viewerRow.liveTimeView.text = "MP"
                                                        drawableRedDot = context?.let { ContextCompat.getDrawable(it, R.drawable.transparent) }
                                                    }
                                                    else -> {
                                                        viewerRow.liveRedDot.setImageDrawable(context?.let { ContextCompat.getDrawable(it, R.drawable.transparent) })
                                                        viewerRow.liveTimeView.text = "MP"

                                                        // Sort latest date to the top
                                                        var currentIndex = 599999
                                                        var currentTimeIndex = 599
                                                        if (snapshot.child("Keputusan").hasChildren()) {
                                                            val currentKeputusan = snapshot.child("Keputusan").children.firstOrNull()?.key
                                                            if (currentKeputusan != null) {
                                                                if (currentKeputusan.substring(6) != dataD.key) {
                                                                    currentIndex = currentKeputusan.substring(0, 6).toInt()
                                                                    currentIndex--
                                                                }
                                                                else {
                                                                    currentIndex = currentKeputusan.substring(0, 6).toInt()
                                                                    // Sort latest start match to the top
                                                                    val currentTiming = snapshot.child("Keputusan").children.firstOrNull()?.child("${dataP.key}")?.children?.firstOrNull()?.key
                                                                    if (currentTiming != null) {
                                                                        currentTimeIndex = currentTiming.substring(0, 3).toInt()
                                                                    }
                                                                    currentTimeIndex--
                                                                }
                                                            }
                                                        }
                                                        // Sort latest start match to the top
                                                        /*
                                                        var currentTimeIndex = 599
                                                        if (snapshot.child("Keputusan").hasChildren()) {
                                                            val currentTiming = snapshot.child("Keputusan").children.firstOrNull()?.child("${dataP.key}")?.children?.firstOrNull()?.key
                                                            if (currentTiming != null) {
                                                                currentTimeIndex = currentTiming.substring(0, 3).toInt()
                                                            }
                                                            currentTimeIndex--
                                                        }
                                                        */

                                                        val setKeputusan = FragmentThree.Game("${data.child("date").value}", "${data.child("hari").value}", "${data.child("time").value}", "${data.child("team1").value}", "${data.child("team2").value}", "2", "${data.child("skor1").value}", "${data.child("skor2").value}", "${data.child("tm1").value}", "${data.child("tm2").value}", "${data.child("gp1").value}".toInt(), "${data.child("gp2").value}".toInt(), "${data.child("gm1").value}".toInt(), "${data.child("gm2").value}".toInt(), "${data.child("gs1").value}".toInt(), "${data.child("gs2").value}".toInt(), "${data.child("gk1").value}".toInt(), "${data.child("gk2").value}".toInt(), "${data.child("gmata1").value}".toInt(), "${data.child("gmata2").value}".toInt())
                                                        database.child("Keputusan").child("${currentIndex}${dataD.key}").child("${dataP.key}").child("${currentTimeIndex}${dataT.key}").child("${data.key}").setValue(setKeputusan)
                                                        database.child("Akan Datang").child("${dataD.key}").child("${dataP.key}").child("${dataT.key}").child("${data.key}").removeValue()
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    else if (currentDateTime >= eachDateTime) {

                                        viewerRow.timeView.text = "${data.child("skor1").value} - ${data.child("skor2").value}"
                                        viewerRow.liveTimeView.text = "MP"

                                        // Sort latest date to the top
                                        var currentIndex = 599999
                                        var currentTimeIndex = 599
                                        if (snapshot.child("Keputusan").hasChildren()) {
                                            val currentKeputusan = snapshot.child("Keputusan").children.firstOrNull()?.key
                                            if (currentKeputusan != null) {
                                                if (currentKeputusan.substring(6) != dataD.key) {
                                                    currentIndex = currentKeputusan.substring(0, 6).toInt()
                                                    currentIndex--
                                                }
                                                else {
                                                    currentIndex = currentKeputusan.substring(0, 6).toInt()
                                                    // Sort latest start match to the top
                                                    val currentTiming = snapshot.child("Keputusan").children.firstOrNull()?.child("${dataP.key}")?.children?.firstOrNull()?.key
                                                    if (currentTiming != null) {
                                                        currentTimeIndex = currentTiming.substring(0, 3).toInt()
                                                    }
                                                    currentTimeIndex--
                                                }
                                            }
                                        }

                                        val setKeputusan = FragmentThree.Game("${data.child("date").value}", "${data.child("hari").value}", "${data.child("time").value}", "${data.child("team1").value}", "${data.child("team2").value}", "2", "${data.child("skor1").value}", "${data.child("skor2").value}", "${data.child("tm1").value}", "${data.child("tm2").value}", "${data.child("gp1").value}".toInt(), "${data.child("gp2").value}".toInt(), "${data.child("gm1").value}".toInt(), "${data.child("gm2").value}".toInt(), "${data.child("gs1").value}".toInt(), "${data.child("gs2").value}".toInt(), "${data.child("gk1").value}".toInt(), "${data.child("gk2").value}".toInt(), "${data.child("gmata1").value}".toInt(), "${data.child("gmata2").value}".toInt())
                                        database.child("Keputusan").child("${currentIndex}${dataD.key}").child("${dataP.key}").child("${currentTimeIndex}${dataT.key}").child("${data.key}").setValue(setKeputusan)
                                        database.child("Akan Datang").child("${dataD.key}").child("${dataP.key}").child("${dataT.key}").child("${data.key}").removeValue()

                                        viewerRow.liveRedDot.setImageDrawable(context?.let { ContextCompat.getDrawable(it, R.drawable.transparent) })
                                    }
                                    else {
                                        viewerRow.liveRedDot.setImageDrawable(context?.let { ContextCompat.getDrawable(it, R.drawable.transparent) })
                                    }
                                    // =========================================================================================================




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

                } // end of if (aLigaSuper.hasChildren())
                binding.swipeRefresh.isRefreshing = false
            } // end of snapshot

            @SuppressLint("InflateParams")
            override fun onCancelled(error: DatabaseError) {
                val linear: LinearLayout = binding.layoutList
                val viewer = inflater.inflate(R.layout.error_occur, null)

                linear.addView(viewer)
                binding.swipeRefresh.isRefreshing = false
            }

        }

        //database.addValueEventListener(getData) // Realtime firebase update to apps
        database.addListenerForSingleValueEvent(getData) // Not realtime firebase update to apps (need refresh)

    }


}


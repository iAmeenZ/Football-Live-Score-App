package com.example.bolasatu

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ImageSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.bolasatu.databinding.FragmentThreeBinding
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.match_box_edit.view.*
import kotlinx.android.synthetic.main.match_box_edit.view.dateView
import kotlinx.android.synthetic.main.match_box_edit.view.firstImageView
import kotlinx.android.synthetic.main.match_box_edit.view.firstView
import kotlinx.android.synthetic.main.match_box_edit.view.liveTimeView
import kotlinx.android.synthetic.main.match_box_edit.view.pialaView
import kotlinx.android.synthetic.main.match_box_edit.view.secondImageView
import kotlinx.android.synthetic.main.match_box_edit.view.secondView
import kotlinx.android.synthetic.main.match_box_edit.view.timeView
import kotlinx.android.synthetic.main.match_box_row.view.*
import java.text.SimpleDateFormat
import java.util.*

class FragmentThree : Fragment() {

    data class Game(var date: String, var hari: String, var time: String, var team1: String, var team2: String, var langsung: String, var skor1: String, var skor2: String, var tm1: String, var tm2: String, var gp1: Int, var gp2: Int, var gm1: Int, var gm2: Int, var gs1: Int, var gs2: Int, var gk1: Int, var gk2: Int, var gmata1: Int, var gmata2: Int)
    data class Kedudukan(var p: Int, var m: Int, var s: Int, var k: Int, var j: Int, var b: Int, var bg: Int, var mata: Int)

    private lateinit var team1: String
    private lateinit var team2: String
    private lateinit var piala: String
    private lateinit var hari: String


    @SuppressLint("SimpleDateFormat")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil.inflate<FragmentThreeBinding>(inflater,
            R.layout.fragment_three, container, false)

        val database = FirebaseDatabase.getInstance().reference
        val linear: LinearLayout = binding.layoutList

        val pialaList : MutableList<String> = mutableListOf("bLiga Super", "eLiga Premier", "cPiala FA", "dPiala Malaysia", "aPiala Sumbangsih", "fLiga M3", "gPiala Presiden", "hPiala Belia", "iLiga M4")

        val timeZone: TimeZone = TimeZone.getTimeZone("GMT+08:00")
        val calendar: Calendar = Calendar.getInstance()
        val sdf = SimpleDateFormat("yyyyMMddHHmm")
        sdf.timeZone = timeZone
        val currentDateTime = sdf.format(calendar.time)

        akanDatang(binding, database, currentDateTime, pialaList)

        lepas(binding, database, currentDateTime, pialaList)

        langsung(inflater, database, linear)

        binding.swipeRefresh.setOnRefreshListener {
            linear.removeAllViews()
            langsung(inflater, database, linear)
            binding.swipeRefresh.isRefreshing = false
        }
        binding.refreshButton.setOnClickListener {
            linear.removeAllViews()
            langsung(inflater, database, linear)
            binding.swipeRefresh.isRefreshing = false
        }



        return binding.root
    }


    private fun langsung(inflater: LayoutInflater, database: DatabaseReference, linear: LinearLayout) {
        val getData = object : ValueEventListener {
            @SuppressLint("InflateParams", "InlinedApi", "SimpleDateFormat", "SetTextI18n")
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onDataChange(snapshot: DataSnapshot) {

                var tm1 = SpannableStringBuilder()
                var tm2 = SpannableStringBuilder()

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

                            if (dataD.key?.substring(0, 8)  == currentDateTime.substring(0, 8)) {

                                dataP.children.forEach { dataT ->

                                    dataT.children.forEach { data ->

                                        viewer = inflater.inflate(R.layout.match_box_edit, null)

                                        val currentTime = currentDateTime2.toInt()
                                        val extract1 = dataT.key?.substring(0,2)
                                        val extract2 = dataT.key?.substring(3,5)
                                        val extract = "$extract1$extract2".toInt()
                                        var movingTime = currentTime - extract
                                        val getTimeRange = currentDateTime2.substring(0,2).toInt() - extract1?.toInt()!!
                                        movingTime -= (getTimeRange * 40)
                                        val tm1Moving = data.child("tm1").value?.toString()?.toInt()
                                        val tm2Moving = data.child("tm2").value?.toString()?.toInt()

                                        if (tm1Moving != null) {
                                            if (tm2Moving != null) {
                                                when (movingTime) {
                                                    in 0..45 -> viewer.liveTimeView.text = "$movingTime'"
                                                    in 46..46 + tm1Moving -> viewer.liveTimeView.text = "45`+"
                                                    in 47 + tm1Moving..47 + tm1Moving + 15 -> viewer.liveTimeView.text = "SM"
                                                    in 48 + tm1Moving + 15..48 + tm1Moving + 15 + 45 -> {
                                                        movingTime = movingTime - tm1Moving - 18
                                                        viewer.liveTimeView.text = "$movingTime`"
                                                    }
                                                    in 49 + tm1Moving + 15 + 45..49 + tm1Moving + 15 + 45 + tm2Moving -> {
                                                        movingTime = movingTime - tm1Moving - 15
                                                        viewer.liveTimeView.text = "90`+"
                                                    }
                                                    in 50 + tm1Moving + 15 + 45 + tm2Moving..50 + tm1Moving + 15 + 45 + tm2Moving + 2 -> viewer.liveTimeView.text = "MP"
                                                    else -> viewer.liveTimeView.text = "#"
                                                }
                                            }
                                        }

                                        if (data.child("langsung").value == "1") {


                                            val bulan = when (dataD.key?.substring(4, 6)) {
                                                "01" -> "Januari"
                                                "02" -> "Februari"
                                                "03" -> "Mac"
                                                "04" -> "April"
                                                "05" -> "Mei"
                                                "06" -> "Jun"
                                                "07" -> "July"
                                                "08" -> "Ogos"
                                                "09" -> "September"
                                                "10" -> "Oktober"
                                                "11" -> "November"
                                                else -> "Disember"
                                            }

                                            viewer.dateView.text = "${dataD.key?.substring(8)}, ${dataD.key?.substring(6, 8)} $bulan"
                                            viewer.pialaView.text = "${dataP.key?.substring(1)}"

                                            val drawablePiala: Drawable? = if (dataP.key == "aLiga Super") {
                                                context?.let { ContextCompat.getDrawable(it, R.drawable.ligasuper) }
                                            } else {
                                                context?.let { ContextCompat.getDrawable(it, R.drawable.ligaperdana) }
                                            }
                                            viewer.pialaImageView.setImageDrawable(drawablePiala)

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
                                            viewer.firstView.text = "$ringkas1"
                                            viewer.secondView.text = "$ringkas2"
                                            viewer.timeView.text = "${data.child("skor1").value} - ${data.child("skor2").value}"

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

                                            viewer.firstImageView.setImageDrawable(drawable1)
                                            viewer.secondImageView.setImageDrawable(drawable2)

                                            val dataK1 = snapshot.child("Kedudukan").child("${dataP.key?.substring(1)}").child("${data.child("team1").value}")
                                            val dataK2 = snapshot.child("Kedudukan").child("${dataP.key?.substring(1)}").child("${data.child("team2").value}")

                                            viewer.firstMinus.setOnClickListener {
                                                var skorFirstMinus = data.child("skor1").value.toString().toInt()
                                                skorFirstMinus--

                                                val setSkor = Game("${data.child("date").value}", "${data.child("hari").value}", "${data.child("time").value}", "${data.child("team1").value}", "${data.child("team2").value}", "${data.child("langsung").value}", "$skorFirstMinus", "${data.child("skor2").value}", "${data.child("tm1").value}", "${data.child("tm2").value}", "${data.child("gp1").value}".toInt(), "${data.child("gp2").value}".toInt(), "${data.child("gm1").value}".toInt(), "${data.child("gm2").value}".toInt(), "${data.child("gs1").value}".toInt(), "${data.child("gs2").value}".toInt(), "${data.child("gk1").value}".toInt(), "${data.child("gk2").value}".toInt(), "${data.child("gmata1").value}".toInt(), "${data.child("gmata2").value}".toInt())
                                                database.child("Akan Datang").child("${dataD.key}").child("${dataP.key}").child("${dataT.key}").child("${data.key}").setValue(setSkor)

                                                val minusJ = dataK1.child("j").value.toString().toInt() - 1
                                                val updateBG1 = dataK1.child("bg").value.toString().toInt() - 1

                                                val minusB = dataK2.child("b").value.toString().toInt() - 1
                                                val updateBG2 = dataK2.child("bg").value.toString().toInt() + 1

                                                var addM1 = data.child("gm1").value.toString().toInt()
                                                var addM2 = data.child("gm2").value.toString().toInt()
                                                var addK1 = data.child("gk1").value.toString().toInt()
                                                var addK2 = data.child("gk2").value.toString().toInt()
                                                var addS1 = data.child("gs1").value.toString().toInt()
                                                var addS2 = data.child("gs2").value.toString().toInt()
                                                var addMata1 = data.child("gmata1").value.toString().toInt()
                                                var addMata2 = data.child("gmata2").value.toString().toInt()
                                                if (data.child("skor1").value.toString().toInt() <= data.child("skor2").value.toString().toInt()) {
                                                    addM2++
                                                    addK1++
                                                    addMata2 += 3
                                                }
                                                else if (data.child("skor1").value.toString().toInt() == data.child("skor2").value.toString().toInt() + 1) {
                                                    addS1++
                                                    addS2++
                                                    addMata1++
                                                    addMata2++
                                                }
                                                else {
                                                    addM1++
                                                    addK2++
                                                    addMata1 += 3
                                                }

                                                val updateKedudukanT1 = Kedudukan(dataK1.child("p").value.toString().toInt(), addM1, addS1, addK1, minusJ, dataK1.child("b").value.toString().toInt(), updateBG1, addMata1)
                                                database.child("Kedudukan").child("${dataP.key?.substring(1)}").child("${data.child("team1").value}").setValue(updateKedudukanT1)
                                                val updateKedudukanT2 = Kedudukan(dataK2.child("p").value.toString().toInt(), addM2, addS2, addK2, dataK2.child("j").value.toString().toInt(), minusB, updateBG2, addMata2)
                                                database.child("Kedudukan").child("${dataP.key?.substring(1)}").child("${data.child("team2").value}").setValue(updateKedudukanT2)

                                                Toast.makeText(context, "${data.child("team1").value} minus 1 score", Toast.LENGTH_SHORT).show()
                                            }
                                            viewer.firstPlus.setOnClickListener {
                                                var skorFirstPlus = data.child("skor1").value.toString().toInt()
                                                skorFirstPlus++

                                                val setSkor = Game("${data.child("date").value}", "${data.child("hari").value}", "${data.child("time").value}", "${data.child("team1").value}", "${data.child("team2").value}", "${data.child("langsung").value}", "$skorFirstPlus", "${data.child("skor2").value}", "${data.child("tm1").value}", "${data.child("tm2").value}", "${data.child("gp1").value}".toInt(), "${data.child("gp2").value}".toInt(), "${data.child("gm1").value}".toInt(), "${data.child("gm2").value}".toInt(), "${data.child("gs1").value}".toInt(), "${data.child("gs2").value}".toInt(), "${data.child("gk1").value}".toInt(), "${data.child("gk2").value}".toInt(), "${data.child("gmata1").value}".toInt(), "${data.child("gmata2").value}".toInt())
                                                database.child("Akan Datang").child("${dataD.key}").child("${dataP.key}").child("${dataT.key}").child("${data.key}").setValue(setSkor)

                                                val addJ = dataK1.child("j").value.toString().toInt() + 1
                                                val updateBG1 = dataK1.child("bg").value.toString().toInt() + 1

                                                val addB = dataK2.child("b").value.toString().toInt() + 1
                                                val updateBG2 = dataK2.child("bg").value.toString().toInt() - 1

                                                var addM1 = data.child("gm1").value.toString().toInt()
                                                var addM2 = data.child("gm2").value.toString().toInt()
                                                var addK1 = data.child("gk1").value.toString().toInt()
                                                var addK2 = data.child("gk2").value.toString().toInt()
                                                var addS1 = data.child("gs1").value.toString().toInt()
                                                var addS2 = data.child("gs2").value.toString().toInt()
                                                var addMata1 = data.child("gmata1").value.toString().toInt()
                                                var addMata2 = data.child("gmata2").value.toString().toInt()
                                                if (data.child("skor1").value.toString().toInt() >= data.child("skor2").value.toString().toInt()) {
                                                    addM1++
                                                    addK2++
                                                    addMata1 += 3
                                                }
                                                else if (data.child("skor1").value.toString().toInt() == data.child("skor2").value.toString().toInt() - 1) {
                                                    addS1++
                                                    addS2++
                                                    addMata1++
                                                    addMata2++
                                                }
                                                else {
                                                    addM2++
                                                    addK1++
                                                    addMata2 += 3
                                                }


                                                val updateKedudukanT1 = Kedudukan(dataK1.child("p").value.toString().toInt(), addM1, addS1, addK1, addJ, dataK1.child("b").value.toString().toInt(), updateBG1, addMata1)
                                                database.child("Kedudukan").child("${dataP.key?.substring(1)}").child("${data.child("team1").value}").setValue(updateKedudukanT1)
                                                val updateKedudukanT2 = Kedudukan(dataK2.child("p").value.toString().toInt(), addM2, addS2, addK2, dataK2.child("j").value.toString().toInt(), addB, updateBG2, addMata2)
                                                database.child("Kedudukan").child("${dataP.key?.substring(1)}").child("${data.child("team2").value}").setValue(updateKedudukanT2)

                                                Toast.makeText(context, "${data.child("team1").value} added 1 score", Toast.LENGTH_SHORT).show()
                                            }

                                            viewer.secondMinus.setOnClickListener {
                                                var skorSecondMinus = data.child("skor2").value.toString().toInt()
                                                skorSecondMinus--

                                                val setSkor = Game("${data.child("date").value}", "${data.child("hari").value}", "${data.child("time").value}", "${data.child("team1").value}", "${data.child("team2").value}", "${data.child("langsung").value}", "${data.child("skor1").value}", "$skorSecondMinus", "${data.child("tm1").value}", "${data.child("tm2").value}", "${data.child("gp1").value}".toInt(), "${data.child("gp2").value}".toInt(), "${data.child("gm1").value}".toInt(), "${data.child("gm2").value}".toInt(), "${data.child("gs1").value}".toInt(), "${data.child("gs2").value}".toInt(), "${data.child("gk1").value}".toInt(), "${data.child("gk2").value}".toInt(), "${data.child("gmata1").value}".toInt(), "${data.child("gmata2").value}".toInt())
                                                database.child("Akan Datang").child("${dataD.key}").child("${dataP.key}").child("${dataT.key}").child("${data.key}").setValue(setSkor)

                                                val minusJ = dataK2.child("j").value.toString().toInt() - 1
                                                val updateBG1 = dataK2.child("bg").value.toString().toInt() - 1

                                                val minusB = dataK1.child("b").value.toString().toInt() - 1
                                                val updateBG2 = dataK1.child("bg").value.toString().toInt() + 1

                                                var addM1 = data.child("gm1").value.toString().toInt()
                                                var addM2 = data.child("gm2").value.toString().toInt()
                                                var addK1 = data.child("gk1").value.toString().toInt()
                                                var addK2 = data.child("gk2").value.toString().toInt()
                                                var addS1 = data.child("gs1").value.toString().toInt()
                                                var addS2 = data.child("gs2").value.toString().toInt()
                                                var addMata1 = data.child("gmata1").value.toString().toInt()
                                                var addMata2 = data.child("gmata2").value.toString().toInt()
                                                if (data.child("skor2").value.toString().toInt() <= data.child("skor1").value.toString().toInt()) {
                                                    addM1++
                                                    addK2++
                                                    addMata1 += 3
                                                }
                                                else if (data.child("skor2").value.toString().toInt() == data.child("skor1").value.toString().toInt() + 1) {
                                                    addS1++
                                                    addS2++
                                                    addMata1++
                                                    addMata2++
                                                }
                                                else {
                                                    addM2++
                                                    addK1++
                                                    addMata2 += 3
                                                }

                                                val updateKedudukanT1 = Kedudukan(dataK2.child("p").value.toString().toInt(), addM2, addS2, addK2, minusJ, dataK2.child("b").value.toString().toInt(), updateBG1, addMata2)
                                                database.child("Kedudukan").child("${dataP.key?.substring(1)}").child("${data.child("team2").value}").setValue(updateKedudukanT1)
                                                val updateKedudukanT2 = Kedudukan(dataK1.child("p").value.toString().toInt(), addM1, addS1, addK1, dataK1.child("j").value.toString().toInt(), minusB, updateBG2, addMata1)
                                                database.child("Kedudukan").child("${dataP.key?.substring(1)}").child("${data.child("team1").value}").setValue(updateKedudukanT2)

                                                Toast.makeText(context, "${data.child("team2").value} minus 1 score", Toast.LENGTH_SHORT).show()
                                            }
                                            viewer.secondPlus.setOnClickListener {
                                                var skorSecondPlus = data.child("skor2").value.toString().toInt()
                                                skorSecondPlus++

                                                val setSkor = Game("${data.child("date").value}", "${data.child("hari").value}", "${data.child("time").value}", "${data.child("team1").value}", "${data.child("team2").value}", "${data.child("langsung").value}", "${data.child("skor1").value}", "$skorSecondPlus", "${data.child("tm1").value}", "${data.child("tm2").value}", "${data.child("gp1").value}".toInt(), "${data.child("gp2").value}".toInt(), "${data.child("gm1").value}".toInt(), "${data.child("gm2").value}".toInt(), "${data.child("gs1").value}".toInt(), "${data.child("gs2").value}".toInt(), "${data.child("gk1").value}".toInt(), "${data.child("gk2").value}".toInt(), "${data.child("gmata1").value}".toInt(), "${data.child("gmata2").value}".toInt())
                                                database.child("Akan Datang").child("${dataD.key}").child("${dataP.key}").child("${dataT.key}").child("${data.key}").setValue(setSkor)

                                                val addJ = dataK2.child("j").value.toString().toInt() + 1
                                                val updateBG1 = dataK2.child("bg").value.toString().toInt() + 1

                                                val addB = dataK1.child("b").value.toString().toInt() + 1
                                                val updateBG2 = dataK1.child("bg").value.toString().toInt() - 1

                                                var addM1 = data.child("gm1").value.toString().toInt()
                                                var addM2 = data.child("gm2").value.toString().toInt()
                                                var addK1 = data.child("gk1").value.toString().toInt()
                                                var addK2 = data.child("gk2").value.toString().toInt()
                                                var addS1 = data.child("gs1").value.toString().toInt()
                                                var addS2 = data.child("gs2").value.toString().toInt()
                                                var addMata1 = data.child("gmata1").value.toString().toInt()
                                                var addMata2 = data.child("gmata2").value.toString().toInt()
                                                if (data.child("skor2").value.toString().toInt() >= data.child("skor1").value.toString().toInt()) {
                                                    addM2++
                                                    addK1++
                                                    addMata2 += 3
                                                }
                                                else if (data.child("skor2").value.toString().toInt() == data.child("skor1").value.toString().toInt() - 1) {
                                                    addS1++
                                                    addS2++
                                                    addMata1++
                                                    addMata2++
                                                }
                                                else {
                                                    addM1++
                                                    addK2++
                                                    addMata1 += 3
                                                }

                                                val updateKedudukanT1 = Kedudukan(dataK2.child("p").value.toString().toInt(), addM2, addS2, addK2, addJ, dataK2.child("b").value.toString().toInt(), updateBG1, addMata2)
                                                database.child("Kedudukan").child("${dataP.key?.substring(1)}").child("${data.child("team2").value}").setValue(updateKedudukanT1)
                                                val updateKedudukanT2 = Kedudukan(dataK1.child("p").value.toString().toInt(), addM1, addS1, addK1, dataK1.child("j").value.toString().toInt(), addB, updateBG2, addMata1)
                                                database.child("Kedudukan").child("${dataP.key?.substring(1)}").child("${data.child("team1").value}").setValue(updateKedudukanT2)

                                                Toast.makeText(context, "${data.child("team2").value} added 1 score", Toast.LENGTH_SHORT).show()
                                            }

                                            var tma1: String = data.child("tm1").value as String
                                            var tma2: String = data.child("tm2").value as String
                                            viewer.tm1View.setSelection(tma1.toInt())
                                            viewer.tm2View.setSelection(tma2.toInt())
                                            viewer.tm1View.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                                                override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, position: Int, id: Long) {
                                                    if (adapterView?.getItemAtPosition(position).toString() != "X") {
                                                        tma1 = adapterView?.getItemAtPosition(position).toString()
                                                        val setTm = Game("${data.child("date").value}", "${data.child("hari").value}", "${data.child("time").value}", "${data.child("team1").value}", "${data.child("team2").value}", "${data.child("langsung").value}", "${data.child("skor1").value}", "${data.child("skor2").value}", tma1, tma2, "${data.child("gp1").value}".toInt(), "${data.child("gp2").value}".toInt(), "${data.child("gm1").value}".toInt(), "${data.child("gm2").value}".toInt(), "${data.child("gs1").value}".toInt(), "${data.child("gs2").value}".toInt(), "${data.child("gk1").value}".toInt(), "${data.child("gk2").value}".toInt(), "${data.child("gmata1").value}".toInt(), "${data.child("gmata2").value}".toInt())
                                                        database.child("Akan Datang").child("${dataD.key}").child("${dataP.key}").child("${dataT.key}").child("${data.key}").setValue(setTm)
                                                    }
                                                }

                                                override fun onNothingSelected(p0: AdapterView<*>?) {

                                                }
                                            }

                                            viewer.tm2View.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                                                override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, position: Int, id: Long) {
                                                    if (adapterView?.getItemAtPosition(position).toString() != "X") {
                                                        tma2 = adapterView?.getItemAtPosition(position).toString()
                                                        val setTm = Game("${data.child("date").value}", "${data.child("hari").value}", "${data.child("time").value}", "${data.child("team1").value}", "${data.child("team2").value}", "${data.child("langsung").value}", "${data.child("skor1").value}", "${data.child("skor2").value}", tma1, tma2, "${data.child("gp1").value}".toInt(), "${data.child("gp2").value}".toInt(), "${data.child("gm1").value}".toInt(), "${data.child("gm2").value}".toInt(), "${data.child("gs1").value}".toInt(), "${data.child("gs2").value}".toInt(), "${data.child("gk1").value}".toInt(), "${data.child("gk2").value}".toInt(), "${data.child("gmata1").value}".toInt(), "${data.child("gmata2").value}".toInt())
                                                        database.child("Akan Datang").child("${dataD.key}").child("${dataP.key}").child("${dataT.key}").child("${data.key}").setValue(setTm)
                                                    }
                                                }

                                                override fun onNothingSelected(p0: AdapterView<*>?) {

                                                }
                                            }

                                            linear.addView(viewer)
                                        }

                                    } // end of forEach - data


                                } // end of forEach - dataT

                            }

                        } // end of forEach - dataP

                    } // end of forEach - dataD

                } // end of if (aLigaSuper.hasChildren())

            } // end of snapshot

            override fun onCancelled(error: DatabaseError) {

            }

        }

        //database.addValueEventListener(getData) // Realtime firebase update to apps
        database.addListenerForSingleValueEvent(getData) // Not realtime firebase update to apps (need refresh)
    }


    private fun lepas(binding: FragmentThreeBinding, database: DatabaseReference, currentDateTime: String, pialaList: MutableList<String>) {

        val adapterPiala = context?.let { ArrayAdapter(it, R.layout.support_simple_spinner_dropdown_item, pialaList) }
        binding.spinnerPialaLepas.adapter = adapterPiala

        binding.spinnerPialaLepas.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, position: Int, id: Long) {
                piala = adapterView?.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }

        binding.spinnerHariLepas.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, position: Int, id: Long) {
                hari = adapterView?.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }

        val teamListHome : MutableList<String> = mutableListOf("HOME TEAM")
        val teamListAway : MutableList<String> = mutableListOf("AWAY TEAM")
        val getTeam = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                val pialaKey = snapshot.child("Pasukan")

                pialaKey.children.forEach { data ->
                    teamListHome.add(data.key!!)
                    teamListAway.add(data.key!!)
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        }
        database.addListenerForSingleValueEvent(getTeam)

        val adapterHome = context?.let { ArrayAdapter(it, R.layout.support_simple_spinner_dropdown_item, teamListHome) }
        binding.spinner1Lepas.adapter = adapterHome
        val adapterAway = context?.let { ArrayAdapter(it, R.layout.support_simple_spinner_dropdown_item, teamListAway) }
        binding.spinner2Lepas.adapter = adapterAway

        binding.spinner1Lepas.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, position: Int, id: Long) {
                team1 = adapterView?.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }

        binding.spinner2Lepas.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, position: Int, id: Long) {
                team2 = adapterView?.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }

        binding.addButtonLepas.setOnClickListener {
            Toast.makeText(context,
                "Match added",
                Toast.LENGTH_LONG).show()
        }

        binding.addButtonLepas.setOnClickListener {
            val matchid = binding.matchIDLepas.text.toString()
            val time = binding.timeLepas.text.toString()
            val date = binding.dateLepas.text.toString()

            val score1 = binding.skor1Lepas.text.toString()
            val score2 = binding.skor2Lepas.text.toString()

            val dateHari = "${date}${hari}"

            var extractTime = ""

            if (time.length >= 5) {
                extractTime = "${time.substring(0, 2)}${time.substring(3, 5)}"
            }

            if (date == "" || time == "") {
                Toast.makeText(context, "Please fill in the blank", Toast.LENGTH_SHORT).show()
            }
            else if (date.length < 8 || date.length > 8 || date.substring(4, 6).toInt() > 12 || date.substring(6, 8).toInt() > 31 || date.substring(6, 8).toInt() == 0) {
                Toast.makeText(context, "Date must be in proper format (YYYYMMDD)", Toast.LENGTH_SHORT).show()
            }
            else if (time.length < 5 ||  time.length > 5 ||  time.substring(2, 3) != ":" || time.substring(0, 2).toInt() > 23 || time.substring(3, 5).toInt() > 59) {
                Toast.makeText(context, "Time must be in proper format (HH:mm)", Toast.LENGTH_SHORT).show()
            }
            else if (date.toInt() > currentDateTime.substring(0, 8).toInt() || (date.toInt() >= currentDateTime.substring(0, 8).toInt() && extractTime.toInt() > currentDateTime.substring(8, 12).toInt())) {
                Toast.makeText(context, "Date and time must be in the future", Toast.LENGTH_SHORT).show()
            }
            else if (team1 == "HOME TEAM" || team2 == "AWAY TEAM") {
                Toast.makeText(context, "Please choose a team", Toast.LENGTH_SHORT).show()
            }
            else if (team1 == team2) {
                Toast.makeText(context, "Both can't be the same team", Toast.LENGTH_SHORT).show()
            }
            else {
                database.child("Keputusan").child(dateHari).child(piala).child(time).child(matchid).setValue(Game(date, hari, time, team1, team2, "2", score1, score2, "3", "3", 0, 0, 0, 0, 0, 0, 0, 0, 0, 0))
                Toast.makeText(context, "Match past added", Toast.LENGTH_SHORT).show()
            }
        }
    }







    private fun akanDatang(binding: FragmentThreeBinding, database: DatabaseReference, currentDateTime: String, pialaList: MutableList<String>) {
        binding.akanDatangButton.setOnClickListener {
            binding.akanDatangLayout.visibility = View.VISIBLE
            binding.langsungLayout.visibility = View.GONE
            binding.lepasLayout.visibility = View.GONE
        }

        binding.langsungButton.setOnClickListener {
            binding.akanDatangLayout.visibility = View.GONE
            binding.langsungLayout.visibility = View.VISIBLE
            binding.lepasLayout.visibility = View.GONE
        }

        binding.lepasButton.setOnClickListener {
            binding.akanDatangLayout.visibility = View.GONE
            binding.langsungLayout.visibility = View.GONE
            binding.lepasLayout.visibility = View.VISIBLE
        }

        val adapterPiala = context?.let { ArrayAdapter(it, R.layout.support_simple_spinner_dropdown_item, pialaList) }
        binding.spinnerPiala.adapter = adapterPiala

        binding.spinnerPiala.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, position: Int, id: Long) {
                piala = adapterView?.getItemAtPosition(position).toString()

                val teamListHome : MutableList<String> = mutableListOf("HOME TEAM")
                val teamListAway : MutableList<String> = mutableListOf("AWAY TEAM")
                val getTeam = object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {

                        val pialaKey = snapshot.child("Kedudukan").child(piala.substring(1))

                        pialaKey.children.forEach { data ->
                            teamListHome.add(data.key!!)
                            teamListAway.add(data.key!!)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }
                }
                database.addListenerForSingleValueEvent(getTeam)

                val adapterHome = context?.let { ArrayAdapter(it, R.layout.support_simple_spinner_dropdown_item, teamListHome) }
                binding.spinner1.adapter = adapterHome
                val adapterAway = context?.let { ArrayAdapter(it, R.layout.support_simple_spinner_dropdown_item, teamListAway) }
                binding.spinner2.adapter = adapterAway
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }

        binding.spinnerHari.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, position: Int, id: Long) {
                hari = adapterView?.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }

        binding.spinner1.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, position: Int, id: Long) {
                team1 = adapterView?.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }

        binding.spinner2.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, position: Int, id: Long) {
                team2 = adapterView?.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }

        binding.addButton.setOnClickListener {
            //val matchid = binding.matchID.text.toString()

            val time = binding.time.text.toString()
            val date = binding.date.text.toString()
            val dateHari = "${date}${hari}"

            var id = 1
            val getData = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.hasChild("Akan Datang")) {
                        if (snapshot.child("Akan Datang").hasChild(dateHari)) {
                            if (snapshot.child("Akan Datang").child(dateHari).hasChild(piala)) {
                                if (snapshot.child("Akan Datang").child(dateHari).child(piala).hasChild(time)) {
                                    if (snapshot.child("Akan Datang").child(dateHari).child(piala).child(time).hasChildren()) {
                                        id = snapshot.child("Akan Datang").child(dateHari).child(piala).child(time).childrenCount.toInt() + 1
                                    }
                                }
                            }
                        }
                    }

                    var extractTime = ""

                    if (time.length >= 5) {
                        extractTime = "${time.substring(0, 2)}${time.substring(3, 5)}"
                    }

                    if (date == "" || time == "") {
                        Toast.makeText(context, "Please fill in the blank", Toast.LENGTH_SHORT).show()
                    }
                    else if (date.length < 8 || date.length > 8 || date.substring(4, 6).toInt() > 12 || date.substring(6, 8).toInt() > 31 || date.substring(6, 8).toInt() == 0) {
                        Toast.makeText(context, "Date must be in proper format (YYYYMMDD)", Toast.LENGTH_SHORT).show()
                    }
                    else if (time.length < 5 ||  time.length > 5 ||  time.substring(2, 3) != ":" || time.substring(0, 2).toInt() > 23 || time.substring(3, 5).toInt() > 59) {
                        Toast.makeText(context, "Time must be in proper format (HH:mm)", Toast.LENGTH_SHORT).show()
                    }
                    else if (date.toInt() < currentDateTime.substring(0, 8).toInt() || (date.toInt() <= currentDateTime.substring(0, 8).toInt() && extractTime.toInt() < currentDateTime.substring(8, 12).toInt())) {
                        Toast.makeText(context, "Date and time must be in the future", Toast.LENGTH_SHORT).show()
                    }
                    else if (team1 == "HOME TEAM" || team2 == "AWAY TEAM") {
                        Toast.makeText(context, "Please choose a team", Toast.LENGTH_SHORT).show()
                    }
                    else if (team1 == team2) {
                        Toast.makeText(context, "Both can't be the same team", Toast.LENGTH_SHORT).show()
                    }
                    else {
                        database.child("Akan Datang").child(dateHari).child(piala).child(time).child("$id").setValue(Game(date, hari, time, team1, team2, "0", "0", "0", "3", "3", 0, 0, 0, 0, 0, 0, 0, 0, 0, 0))
                        Toast.makeText(context, "Match added", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            }
            database.addListenerForSingleValueEvent(getData)


        }
    }

}


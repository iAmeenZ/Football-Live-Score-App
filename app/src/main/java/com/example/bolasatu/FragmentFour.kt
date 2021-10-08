package com.example.bolasatu

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.bolasatu.databinding.FragmentFourBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_four.*
import kotlinx.android.synthetic.main.table_fa_cup.view.*
import kotlinx.android.synthetic.main.table_league.view.*
import kotlinx.android.synthetic.main.table_league.view.pialaImageKedudukanView
import kotlinx.android.synthetic.main.table_league.view.pialaImageKedudukanView2
import kotlinx.android.synthetic.main.table_league.view.pialaKedudukanView
import java.text.SimpleDateFormat
import java.util.*

class FragmentFour : Fragment() {

    data class Team(var name: String, var age: Int, var salary: Int)
    data class Kedudukan2(var p: Int, var m: Int, var s: Int, var k: Int, var j: Int, var b: Int, var bg: Int, var mata: Int, var Pa: String)

    @SuppressLint("SetTextI18n", "SimpleDateFormat", "InflateParams")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = DataBindingUtil.inflate<FragmentFourBinding>(
            inflater, R.layout.fragment_four, container, false)
        val linear: LinearLayout = binding.tableLeague
        var piala = "Liga Super"

        val headerLayout: View = binding.cupID
        val headerText = headerLayout.nameQ1a

        headerText.text = "this is header by program"

        tableLeague(inflater, linear, piala, binding)

        binding.swipeRefresh.setOnRefreshListener {
            linear.removeAllViews()
            if (piala == "Liga Super" || piala == "Liga Premier") {
                tableLeague(inflater, linear, piala, binding)
            }
            else if (piala == "Piala FA") {
                headerText.text = "Lukaku"
                binding.cupID.visibility = View.VISIBLE
            }
            binding.swipeRefresh.isRefreshing = false
        }

        binding.ligaSuperButton.setOnClickListener {
            linear.removeAllViews()
            piala = "Liga Super"
            tableLeague(inflater, linear, piala, binding)
            binding.cupID.visibility = View.GONE
        }

        binding.ligaPremierButton.setOnClickListener {
            linear.removeAllViews()
            piala = "Liga Premier"
            tableLeague(inflater, linear, piala, binding)
            binding.cupID.visibility = View.GONE
        }

        binding.pialaFaButton.setOnClickListener {
            linear.removeAllViews()
            piala = "Piala FA"
            binding.cupID.visibility = View.VISIBLE
        }

        return binding.root
    }

    @SuppressLint("InflateParams", "SetTextI18n", "SimpleDateFormat")
    private fun tableLeague(inflater: LayoutInflater, linear: LinearLayout, piala: String, binding: FragmentFourBinding) {
        val viewer = inflater.inflate(R.layout.table_league, null)

        val timeZone: TimeZone = TimeZone.getTimeZone("GMT+08:00")
        val calendar: Calendar = Calendar.getInstance()
        val sdf = SimpleDateFormat("yyyyMMdd HH:mm")
        sdf.timeZone = timeZone
        val currentDateTime = sdf.format(calendar.time)

        val database = FirebaseDatabase.getInstance().getReference("Kedudukan").child(piala)

        when (piala) {
            "Liga Super" -> {
                viewer.pialaKedudukanView.text = "LIGA SUPER ${currentDateTime.substring(0, 4)}"
                viewer.pialaImageKedudukanView.setImageDrawable(context?.let { ContextCompat.getDrawable(it, R.drawable.ligasuper) })
                viewer.pialaImageKedudukanView2.setImageDrawable(context?.let { ContextCompat.getDrawable(it, R.drawable.ligasuper) })
            }
            "Liga Premier" -> {
                viewer.pialaKedudukanView.text = "LIGA PREMIER ${currentDateTime.substring(0, 4)}"
                viewer.pialaImageKedudukanView.setImageDrawable(context?.let { ContextCompat.getDrawable(it, R.drawable.ligaperdana) })
                viewer.pialaImageKedudukanView2.setImageDrawable(context?.let { ContextCompat.getDrawable(it, R.drawable.ligaperdana) })
            }
        }

        val listExtract: MutableList<Kedudukan2> = mutableListOf()

        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                var drawer1: Drawable? = null
                var drawer2: Drawable? = null
                var drawer3: Drawable? = null
                var drawer4: Drawable? = null
                var drawer5: Drawable? = null
                var drawer6: Drawable? = null
                var drawer7: Drawable? = null
                var drawer8: Drawable? = null
                var drawer9: Drawable? = null
                var drawer10: Drawable? = null
                var drawer11: Drawable? = null
                var drawer12: Drawable? = null

                snapshot.children.forEach { data ->
                    listExtract.add(Kedudukan2(data.child("p").value.toString().toInt(), data.child("m").value.toString().toInt(), data.child("s").value.toString().toInt(), data.child("k").value.toString().toInt(), data.child("j").value.toString().toInt(), data.child("b").value.toString().toInt(), data.child("bg").value.toString().toInt(), data.child("mata").value.toString().toInt(), data.key.toString()))
                }

                val list = listExtract.sortedWith(compareBy({it.mata}, {it.bg}, {it.j})).reversed()

                for (i in list.indices) {

                    var drawable: Drawable? = null
                    when (list[i].Pa) {
                        "PDRM" -> drawable = context?.let { ContextCompat.getDrawable(it, R.drawable.pdrm)}
                        "Selangor" -> drawable = context?.let { ContextCompat.getDrawable(it, R.drawable.selangor)}
                        "Felda United" -> drawable = context?.let { ContextCompat.getDrawable(it, R.drawable.felda)}
                        "Johor Darul Ta'zim" -> drawable = context?.let { ContextCompat.getDrawable(it, R.drawable.jdt)}
                        "Melaka United" -> drawable = context?.let { ContextCompat.getDrawable(it, R.drawable.melaka)}
                        "Pahang" -> drawable = context?.let { ContextCompat.getDrawable(it, R.drawable.pahang)}
                        "Kedah" -> drawable = context?.let { ContextCompat.getDrawable(it, R.drawable.kedah)}
                        "Perak" -> drawable = context?.let { ContextCompat.getDrawable(it, R.drawable.perak)}
                        "PJ City FC" -> drawable = context?.let { ContextCompat.getDrawable(it, R.drawable.petalingjaya)}
                        "Sabah FA" -> drawable = context?.let { ContextCompat.getDrawable(it, R.drawable.sabah)}
                        "Terengganu" -> drawable = context?.let { ContextCompat.getDrawable(it, R.drawable.terengganu) }
                        "UiTM" -> drawable = context?.let { ContextCompat.getDrawable(it, R.drawable.uitm)}
                        "Pulau Pinang" -> drawable = context?.let { ContextCompat.getDrawable(it, R.drawable.penang)}
                        "Terengganu II" -> drawable = context?.let { ContextCompat.getDrawable(it, R.drawable.terengganu2)}
                        "Kuala Lumpur" -> drawable = context?.let { ContextCompat.getDrawable(it, R.drawable.kl)}
                        "Johor Darul Ta'zim II" -> drawable = context?.let { ContextCompat.getDrawable(it, R.drawable.jdt2)}
                        "Kelantan" -> drawable = context?.let { ContextCompat.getDrawable(it, R.drawable.kelantan)}
                        "Kuching FA" -> drawable = context?.let { ContextCompat.getDrawable(it, R.drawable.kuching)}
                        "Selangor II" -> drawable = context?.let { ContextCompat.getDrawable(it, R.drawable.selangor2)}
                        "Kelantan United" -> drawable = context?.let { ContextCompat.getDrawable(it, R.drawable.kelantanunited)}
                        "UKM" -> drawable = context?.let { ContextCompat.getDrawable(it, R.drawable.ukm)}
                        "Sarawak United" -> drawable = context?.let { ContextCompat.getDrawable(it, R.drawable.sarawakunited)}
                        "Negeri Sembilan" -> drawable = context?.let { ContextCompat.getDrawable(it, R.drawable.n9)}
                        "Perak II" -> drawable = context?.let { ContextCompat.getDrawable(it, R.drawable.perak2)}
                    }

                    val ringkas = when (list[i].Pa) {
                        "Johor Darul Ta'zim II" -> "JDT II"
                        else -> list[i].Pa
                    }

                    when {
                        viewer.row1Pa.text == "" -> {
                            viewer.row1Pa.text = ringkas
                            viewer.row1I.setImageDrawable(drawable)
                            viewer.row1P.text = list[i].p.toString()
                            viewer.row1M.text = list[i].m.toString()
                            viewer.row1S.text = list[i].s.toString()
                            viewer.row1K.text = list[i].k.toString()
                            val extractJB = "${list[i].j}/${list[i].b}"
                            viewer.row1JB.text = extractJB
                            viewer.row1BG.text = list[i].bg.toString()
                            viewer.row1Mata.text = list[i].mata.toString()
                        }
                        viewer.row2Pa.text == "" -> {
                            viewer.row2Pa.text = ringkas
                            viewer.row2I.setImageDrawable(drawable)
                            viewer.row2P.text = list[i].p.toString()
                            viewer.row2M.text = list[i].m.toString()
                            viewer.row2S.text = list[i].s.toString()
                            viewer.row2K.text = list[i].k.toString()
                            val extractJB = "${list[i].j}/${list[i].b}"
                            viewer.row2JB.text = extractJB
                            viewer.row2BG.text = list[i].bg.toString()
                            viewer.row2Mata.text = list[i].mata.toString()
                        }
                        viewer.row3Pa.text == "" -> {
                            viewer.row3Pa.text = ringkas
                            viewer.row3I.setImageDrawable(drawable)
                            viewer.row3P.text = list[i].p.toString()
                            viewer.row3M.text = list[i].m.toString()
                            viewer.row3S.text = list[i].s.toString()
                            viewer.row3K.text = list[i].k.toString()
                            val extractJB = "${list[i].j}/${list[i].b}"
                            viewer.row3JB.text = extractJB
                            viewer.row3BG.text = list[i].bg.toString()
                            viewer.row3Mata.text = list[i].mata.toString()
                        }
                        viewer.row4Pa.text == "" -> {
                            viewer.row4Pa.text = ringkas
                            viewer.row4I.setImageDrawable(drawable)
                            viewer.row4P.text = list[i].p.toString()
                            viewer.row4M.text = list[i].m.toString()
                            viewer.row4S.text = list[i].s.toString()
                            viewer.row4K.text = list[i].k.toString()
                            val extractJB = "${list[i].j}/${list[i].b}"
                            viewer.row4JB.text = extractJB
                            viewer.row4BG.text = list[i].bg.toString()
                            viewer.row4Mata.text = list[i].mata.toString()
                        }
                        viewer.row5Pa.text == "" -> {
                            viewer.row5Pa.text = ringkas
                            viewer.row5I.setImageDrawable(drawable)
                            viewer.row5P.text = list[i].p.toString()
                            viewer.row5M.text = list[i].m.toString()
                            viewer.row5S.text = list[i].s.toString()
                            viewer.row5K.text = list[i].k.toString()
                            val extractJB = "${list[i].j}/${list[i].b}"
                            viewer.row5JB.text = extractJB
                            viewer.row5BG.text = list[i].bg.toString()
                            viewer.row5Mata.text = list[i].mata.toString()
                        }
                        viewer.row6Pa.text == "" -> {
                            viewer.row6Pa.text = ringkas
                            viewer.row6I.setImageDrawable(drawable)
                            viewer.row6P.text = list[i].p.toString()
                            viewer.row6M.text = list[i].m.toString()
                            viewer.row6S.text = list[i].s.toString()
                            viewer.row6K.text = list[i].k.toString()
                            val extractJB = "${list[i].j}/${list[i].b}"
                            viewer.row6JB.text = extractJB
                            viewer.row6BG.text = list[i].bg.toString()
                            viewer.row6Mata.text = list[i].mata.toString()
                        }
                        viewer.row7Pa.text == "" -> {
                            viewer.row7Pa.text = ringkas
                            viewer.row7I.setImageDrawable(drawable)
                            viewer.row7P.text = list[i].p.toString()
                            viewer.row7M.text = list[i].m.toString()
                            viewer.row7S.text = list[i].s.toString()
                            viewer.row7K.text = list[i].k.toString()
                            val extractJB = "${list[i].j}/${list[i].b}"
                            viewer.row7JB.text = extractJB
                            viewer.row7BG.text = list[i].bg.toString()
                            viewer.row7Mata.text = list[i].mata.toString()
                        }
                        viewer.row8Pa.text == "" -> {
                            viewer.row8Pa.text = ringkas
                            viewer.row8I.setImageDrawable(drawable)
                            viewer.row8P.text = list[i].p.toString()
                            viewer.row8M.text = list[i].m.toString()
                            viewer.row8S.text = list[i].s.toString()
                            viewer.row8K.text = list[i].k.toString()
                            val extractJB = "${list[i].j}/${list[i].b}"
                            viewer.row8JB.text = extractJB
                            viewer.row8BG.text = list[i].bg.toString()
                            viewer.row8Mata.text = list[i].mata.toString()
                        }
                        viewer.row9Pa.text == "" -> {
                            viewer.row9Pa.text = ringkas
                            viewer.row9I.setImageDrawable(drawable)
                            viewer.row9P.text = list[i].p.toString()
                            viewer.row9M.text = list[i].m.toString()
                            viewer.row9S.text = list[i].s.toString()
                            viewer.row9K.text = list[i].k.toString()
                            val extractJB = "${list[i].j}/${list[i].b}"
                            viewer.row9JB.text = extractJB
                            viewer.row9BG.text = list[i].bg.toString()
                            viewer.row9Mata.text = list[i].mata.toString()
                        }
                        viewer.row10Pa.text == "" -> {
                            viewer.row10Pa.text = ringkas
                            viewer.row10I.setImageDrawable(drawable)
                            viewer.row10P.text = list[i].p.toString()
                            viewer.row10M.text = list[i].m.toString()
                            viewer.row10S.text = list[i].s.toString()
                            viewer.row10K.text = list[i].k.toString()
                            val extractJB = "${list[i].j}/${list[i].b}"
                            viewer.row10JB.text = extractJB
                            viewer.row10BG.text = list[i].bg.toString()
                            viewer.row10Mata.text = list[i].mata.toString()
                        }
                        viewer.row11Pa.text == "" -> {
                            viewer.row11Pa.text = ringkas
                            viewer.row11I.setImageDrawable(drawable)
                            viewer.row11P.text = list[i].p.toString()
                            viewer.row11M.text = list[i].m.toString()
                            viewer.row11S.text = list[i].s.toString()
                            viewer.row11K.text = list[i].k.toString()
                            val extractJB = "${list[i].j}/${list[i].b}"
                            viewer.row11JB.text = extractJB
                            viewer.row11BG.text = list[i].bg.toString()
                            viewer.row11Mata.text = list[i].mata.toString()
                        }
                        viewer.row12Pa.text == "" -> {
                            viewer.row12Pa.text = ringkas
                            viewer.row12I.setImageDrawable(drawable)
                            viewer.row12P.text = list[i].p.toString()
                            viewer.row12M.text = list[i].m.toString()
                            viewer.row12S.text = list[i].s.toString()
                            viewer.row12K.text = list[i].k.toString()
                            val extractJB = "${list[i].j}/${list[i].b}"
                            viewer.row12JB.text = extractJB
                            viewer.row12BG.text = list[i].bg.toString()
                            viewer.row12Mata.text = list[i].mata.toString()
                        }
                    }
                }

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })


        linear.addView(viewer)
        binding.swipeRefresh.isRefreshing = false
    }

}

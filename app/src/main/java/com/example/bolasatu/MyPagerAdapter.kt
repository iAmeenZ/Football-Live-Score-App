package com.example.bolaikan

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.bolasatu.FragmentFour
import com.example.bolasatu.FragmentOne
import com.example.bolasatu.FragmentThree
import com.example.bolasatu.FragmentTwo

class MyPagerAdapter (fn : FragmentManager) : FragmentPagerAdapter(fn) {
    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> {
                FragmentOne()
            }
            1 -> {
                FragmentTwo()
            }
            2 -> {
                FragmentFour()
            }
            else -> {
                return FragmentThree()
            }
            // Set tabs positions

        }
    }

    // Return 3 tabs
    override fun getCount(): Int {
        return 4
    }

    // Set tabs titles
    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> "Akan Datang"
            1 -> "Keputusan"
            2 -> "Kedudukan"
            else -> {
                return "Edit"
            }
        }
    }
}

/*

public class main {
    public static void main (String args[]){

        Scanner in = new Scanner(System.in);

        String month[];
        double reading[];

        // Input each month and its reading
        for (int i=0; i<12; i++) {
            System.out.print("Enter Month = ");
            month[i] = in.next();
            System.out.print("Enter " + month[i] + " rainfall reading in mm = ");
            reading[i] = in.nextDouble();
        }

        // Find highest rainfall reading month and its reading
        double highest = reading[0];
        int index;
        for (int i=0; i<12; i++) {
            if (reading[i] > highest) {
                highest = reading[i];
                index = i;
            }
        }

        // Calculate average rainfall reading
        double sum = 0;
        double average;
        for (int i=0; i<12; i++) {
            sum += reading[i];
        }
        average = sum / 12;

        System.out.println("\n\n--------------------------------------------------");
        System.out.println("Highest rainfall reading month = " + month[index]);
        System.out.println("Highest rainfall reading = " + highest);
        System.out.println("Average rainfall reading = " + average);
        System.out.println("\n--------------------------------------------------");

    }
}

*/
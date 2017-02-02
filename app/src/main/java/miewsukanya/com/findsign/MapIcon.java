package miewsukanya.com.findsign;

import android.content.Context;

/**
 * Created by masterUNG on 9/30/2016 AD.
 */

public class MapIcon {

    //Explicit
    private Context context;
    private int anInt;
    private int[] ints = new int[]{R.drawable.icon45, R.drawable.icon60, R.drawable.icon80};

    public MapIcon(Context context,
                   int anInt) {
        this.context = context;
        this.anInt = anInt;
    }   // Constructor

    public int showIcon() {

        return ints[anInt];
    }

}   // MapIcon

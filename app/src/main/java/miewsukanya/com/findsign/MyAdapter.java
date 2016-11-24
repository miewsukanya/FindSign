package miewsukanya.com.findsign;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * Created by Sukanya Boonpun on 13/11/2559.
 */

public class MyAdapter extends BaseAdapter{
    //Explicit
    private Context context;
    private String[] SignIDStrings,SignNameStrings,LatitudeStrings, LongitudeStrings;
    TextView txtJsonTextView;

    public MyAdapter(Context context,
                     String[] SignIDStrings,
                     String[] SignNameStrings,
                     String[] LatitudeStrings,
                     String[] LongitudeStrings) {
        this.context = context;
        this.SignIDStrings = SignIDStrings;
        this.SignNameStrings = SignNameStrings;
        this.LatitudeStrings = LatitudeStrings;
        this.LongitudeStrings = LongitudeStrings;
    }

    @Override
    public int getCount() {
        return SignIDStrings.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.activity_map_search, parent, false);

        //BindGet
        //txtJsonTextView = (TextView) view.findViewById(R.id.textJson);
       // phoneTextView = (TextView) view.findViewById(R.id.textView3);
       // imageView = (ImageView) view.findViewById(R.id.imageView2);

        //show view
       // txtJsonTextView.setText(SignIDStrings[position]);
        //phoneTextView.setText(phoneStrings[position]);

        //Picasso.with(context).load(imageStrings[position]).into(imageView);

        return view;
    }
}//main class

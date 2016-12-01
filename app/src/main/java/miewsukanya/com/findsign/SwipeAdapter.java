package miewsukanya.com.findsign;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * Created by Sukanya Boonpun on 26/11/2559.
 */

public class SwipeAdapter extends PagerAdapter{
    private int[] image_resource = new int[] {R.drawable.kn_0,
            R.drawable.kn_1, R.drawable.kn_2,
            R.drawable.kn_3, R.drawable.kn_4,R.drawable.kn_5
            };
    private Context context;
    private LayoutInflater layoutInflater;

    public SwipeAdapter(Context context) {
        this.context = context;
    }
    @Override
    public int getCount() {
        return image_resource.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view ==(LinearLayout)object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.swipe_layout, container, false);
        ImageView imageView = (ImageView) view.findViewById(R.id.image_view);
       // TextView textView = (TextView) view.findViewById(R.id.image_count);
        imageView.setImageResource(image_resource[position]);
       // textView.setText(""+position);
        container.addView(view);
        Log.d("01DecV1", String.valueOf(position));
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout)object);
    }
}

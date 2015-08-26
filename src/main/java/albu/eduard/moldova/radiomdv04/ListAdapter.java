package albu.eduard.moldova.radiomdv04;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;


public class ListAdapter extends ArrayAdapter<String> {

    public ListAdapter(Context context, ArrayList<String> list) {
        super(context, R.layout.row_layout, list);
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {

        LayoutInflater theInflater = LayoutInflater.from(getContext());
        convertView = theInflater.inflate(R.layout.row_layout, parent, false);
        String canal = getItem(position);
        TextView theTextView = (TextView) convertView.findViewById(R.id.chanel_text_view);
        ImageView chanelLogo = (ImageView) convertView.findViewById(R.id.chanel_logo);

        theTextView.setText(canal);

        switch (position) {
            case 0:
                chanelLogo.setImageResource(R.drawable.jurnal_fm);
                break;
            case 1:
                chanelLogo.setImageResource(R.drawable.kiss_fm_little);
                break;
            case 2:
                chanelLogo.setImageResource(R.drawable.muz_fm_moldova);
                break;
            case 3:
                chanelLogo.setImageResource(R.drawable.hitfm_logo);
                break;
            case 4:
                chanelLogo.setImageResource(R.drawable.radio_albena);
                break;
            case 5:
                chanelLogo.setImageResource(R.drawable.fresh_fm);
                break;
            case 6:
                chanelLogo.setImageResource(R.drawable.maestro_fm);
                break;
            case 7:
                chanelLogo.setImageResource(R.drawable.megapolisfm_mld);
                break;
            case 8:
                chanelLogo.setImageResource(R.drawable.pro_fm);
                break;
            case 9:
                chanelLogo.setImageResource(R.drawable.publika_fm);
                break;
            case 10:
                chanelLogo.setImageResource(R.drawable.radio_7);
                break;
            case 11:
                chanelLogo.setImageResource(R.drawable.noroc);
                break;
            case 12:
                chanelLogo.setImageResource(R.drawable.radio_plai);
                break;
            case 13:
                chanelLogo.setImageResource(R.drawable.radio_stil);
                break;
            case 14:
                chanelLogo.setImageResource(R.drawable.radio_zum);
                break;
            case 15:
                chanelLogo.setImageResource(R.drawable.vocea_basarabiei);
                break;
            case 16:
                chanelLogo.setImageResource(R.drawable.radio_alla);
                break;
            case 17:
                chanelLogo.setImageResource(R.drawable.micul_samaritean);
                break;
            case 18:
                chanelLogo.setImageResource(R.drawable.radio_21);
                break;
        }
        return convertView;
    }
}

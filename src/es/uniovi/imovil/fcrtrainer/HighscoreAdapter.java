package es.uniovi.imovil.fcrtrainer;

import java.util.ArrayList;

import es.uniovi.imovil.fcrtrainer.highscores.Highscore;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class HighscoreAdapter extends ArrayAdapter<Highscore> {

	private Context mContext;
	private ArrayList<Highscore> mHighscores;

	public HighscoreAdapter(Context context, ArrayList<Highscore> highscores) {
		super(context, R.layout.highscore_item, highscores);
		mContext = context;
		mHighscores = highscores;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.highscore_item, parent, false);

		TextView pos = (TextView) rowView.findViewById(R.id.text_view_position);
		pos.setText(Integer.toString(position + 1)); // Position 0 is 1st place

		TextView userName = (TextView) rowView.findViewById(R.id.text_view_user_name);
		userName.setText(mHighscores.get(position).getUserName());

		TextView score = (TextView) rowView.findViewById(R.id.text_view_score);
		score.setText(Integer.toString(mHighscores.get(position).getScore()));

		return rowView;
	}

}

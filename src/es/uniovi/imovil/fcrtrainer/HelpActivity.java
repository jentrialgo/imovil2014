package es.uniovi.imovil.fcrtrainer;

import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

public class HelpActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_help);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new HelpFragment()).commit();
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * HelpFragment fragment with the information about the developers
	 */
	public static class HelpFragment extends Fragment {
		private ImageView mEpiLogo;
		private ImageView mGroupImage;

		public HelpFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_help, container,
					false);
			
			mEpiLogo = (ImageView) rootView.findViewById(R.id.epilogo);
			mGroupImage = (ImageView) rootView.findViewById(R.id.groupphoto);
					
			mEpiLogo.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View arg0) {
					if(mGroupImage.getVisibility() == View.GONE)
						mGroupImage.setVisibility(View.VISIBLE);
					else 
						mGroupImage.setVisibility(View.GONE);
					
				}
				
			});
			
			return rootView;
		}
	}

}

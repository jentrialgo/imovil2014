/*

Copyright 2014 Profesores y alumnos de la asignatura Informática Móvil de la EPI de Gijón

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

 */

package es.uniovi.imovil.fcrtrainer;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

public class HelpActivity extends AppCompatActivity {

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

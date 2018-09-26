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

package es.uniovi.imovil.fcrtrainer.networks;

import es.uniovi.imovil.fcrtrainer.Screen;
import es.uniovi.imovil.fcrtrainer.R;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.WindowManager;

public class NetworkAddressExerciseFragment 
		extends BaseNetworkMaskExerciseFragment
		implements View.OnClickListener {
	private static final String STATE_IP = "mIp";

	private static final long GAME_DURATION_MS = 2 * 60 * 1000; // 2 minutes

	int mIp; // IP address

	public static NetworkAddressExerciseFragment newInstance() {
		return new NetworkAddressExerciseFragment();
	}

	public NetworkAddressExerciseFragment() {
		setGameDuration(GAME_DURATION_MS);
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		if (savedInstanceState == null) {
			// Required by the framework
			super.onActivityCreated(savedInstanceState);
			return;
		}

		mIp = savedInstanceState.getInt(STATE_IP, 0);
		
		// Called after the previous initializations because it requires mIp
		// for printQuestion
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onSaveInstanceState(@NonNull Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(STATE_IP, mIp);
	}

	public void checkAnswer() {
		String net = intToIpString(mIp & mMask);
		if (net.equals(mAnswer.getText().toString())) {
			showAnimationAnswer(true);
			if (mIsPlaying) {
				computeCorrectQuestionAndUpdateScore();
			}
			newQuestion();
		} else {
			showAnimationAnswer(false);
			computeIncorrectQuestionAndUpdateScore();
		}
	}

	@Override
	public void showSolution() {
		mAnswer.setText(intToIpString(mIp & mMask));

		// Set the cursor at the end
		mAnswer.setSelection(mAnswer.getText().length());
	}

	public void newQuestion() {
		mIp = generateRandomIP();
		mMask = generateRandomMask();

		// The solution cannot be all zeros or the IP itself
		while ((mIp & mMask) == 0
				|| (mIp & mMask) == mIp) {
			mIp = generateRandomIP();
		}
		
		printQuestion();
	}

	@Override
	protected void printQuestion() {
		mQuestion.setText(intToIpString(mIp) + "\n" + intToIpString(mMask));

		// Show the IP as starting point for the network mask, because they
		// probably have many numbers in common
		mAnswer.setText(intToIpString(mIp));
		
		// Set the focus on the edit text and show the keyboard
		if(mAnswer.requestFocus()) {
		    getActivity().getWindow().setSoftInputMode(
		    		WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
		}
		
		// Set the cursor at the end so that the user can begin deleting the
		// numbers that are not part of the network address
		mAnswer.setSelection(mAnswer.getText().length());
	}

	private int generateRandomIP() {
		int value = 0;
		while (value == 0) {
			value = mRandom.nextInt(0x7ffffffe);
		}
		return value;
	}

	@Override
	protected int exerciseId() {
		return R.string.network_address;
	}

	@Override
	protected String titleString() {
		return getString(R.string.na_title);
	}

    @Override
    protected Screen associatedExercise() {
        return Screen.NETWORK_ADDRESS;
    }

}

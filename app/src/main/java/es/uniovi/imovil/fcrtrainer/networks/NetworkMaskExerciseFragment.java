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

import android.view.WindowManager;

import es.uniovi.imovil.fcrtrainer.Screen;
import es.uniovi.imovil.fcrtrainer.R;

public class NetworkMaskExerciseFragment
		extends BaseNetworkMaskExerciseFragment {

	public static NetworkMaskExerciseFragment newInstance() {
		return new NetworkMaskExerciseFragment();
	}

	public NetworkMaskExerciseFragment() {
	}

	private int correctAnswer() {
		return mMask;
	}

	public void newQuestion() {
		do {
			mMask = generateRandomMask();
		} while (maxHost() < 2); // A network needs at least two hosts

		printQuestion();
	}

	private int maxHost() {
		// 1 is subtracted for removing the value with all 0, that
		// corresponds to the network address and is not a valid
		// host address
		return (mMask ^ 0xffffffff) - 1;
	}

	@Override
	protected void printQuestion() {
		mQuestion.setText(Integer.toString(maxHost()));
		mAnswer.setText(R.string.default_net_mask);
		
		// Set the focus on the edit text and show the keyboard
		if(mAnswer.requestFocus()) {
		    getActivity().getWindow().setSoftInputMode(
		    		WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
		}
		
		// Set the cursor at the end so that the user can begin deleting the
		// numbers that are not part of the network address
		mAnswer.setSelection(mAnswer.getText().length());
	}

	@Override
	protected void showSolution() {
		mAnswer.setText(intToIpString(correctAnswer()));
		
		// Set the cursor at the end
		mAnswer.setSelection(mAnswer.getText().length());
	}

	@Override
	protected void checkAnswer() {
		String answer = mAnswer.getText().toString();
		if (answer.equals(intToIpString(correctAnswer()))) {
			showAnimationAnswer(true);
			newQuestion();
			if (mIsPlaying) {
				computeCorrectQuestion();
			}
		} else {
			showAnimationAnswer(false);
			computeIncorrectQuestion();
		}
	}

	@Override
	protected int exerciseId() {
		return R.string.network_mask;
	}

	@Override
	protected String titleString() {
		return getString(R.string.network_mask_title);
	}

	@Override
	protected Screen associatedExercise() {
		return Screen.NETWORK_MASK;
	}

}

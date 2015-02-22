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

import es.uniovi.imovil.fcrtrainer.R;

public class HostCountExerciseFragment extends BaseNetworkMaskExerciseFragment {

	public HostCountExerciseFragment() {
	}

	public static BaseNetworkMaskExerciseFragment newInstance() {
		BaseNetworkMaskExerciseFragment fragment = new HostCountExerciseFragment();
		return fragment;
	}

	private int correctAnswer() {
		return (mMask ^ 0xffffffff) - 1;
	}

	public void newQuestion() {
		do {
			mMask = generateRandomMask();
		} while (correctAnswer() < 2); // have a minimum number of hosts
		printQuestion();
	}

	@Override
	protected void printQuestion() {
		mQuestion.setText(intToIpString(mMask));
	}

	@Override
	protected void showSolution() {
		mAnswer.setText(Integer.toString(correctAnswer()));

		// Set the cursor at the end
		mAnswer.setSelection(mAnswer.getText().length());
	}

	@Override
	protected void checkAnswer() {
		String answer = mAnswer.getText().toString();
		if (answer.equals(Integer.toString(correctAnswer()))) {
			showAnimationAnswer(true);
			newQuestion();
			if (mIsPlaying) {
				computeCorrectQuestion();
			}
			mAnswer.setText("");
		} else {
			showAnimationAnswer(false);
			computeIncorrectQuestion();
		}
	}

	@Override
	protected int exerciseId() {
		return R.string.host_count;
	}

	@Override
	protected String titleString() {
		return getString(R.string.host_count_title);
	}

	@Override
	protected int obtainExerciseId() {
		return R.string.host_count;
	}

}

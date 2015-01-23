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

public class CidrExerciseFragment extends BaseNetworkMaskExerciseFragment {

	public static BaseNetworkMaskExerciseFragment newInstance() {
		BaseNetworkMaskExerciseFragment fragment = new CidrExerciseFragment();
		return fragment;
	}

	public CidrExerciseFragment() {
	}

	@Override
	protected void checkAnswer() {
		String ans = mAnswer.getEditableText().toString();
		if (ans.isEmpty()) {
			showAnimationAnswer(false);
			return;
		}

		if (ans.equals(correctAnswer())) {
			showAnimationAnswer(true);
			if (mIsPlaying) {
				gameModeControl();
			}
			mAnswer.setText("");
			newQuestion();
		} else {
			showAnimationAnswer(false);
		}
	}

	private String correctAnswer() {
		return Integer.toString(cidrSuffixFromMask(mMask));
	}

	private int cidrSuffixFromMask(int mMask) {
		int zeroCount = 0;
		int mask = mMask;
		while ((mask & 0x1) == 0) {
			zeroCount++;
			mask = mask >> 1;
		}
		return 32 - zeroCount;
	}

	protected void newQuestion() {
		mMask = generateRandomMask();
		printQuestion();
	}

	@Override
	protected void printQuestion() {
		mQuestion.setText(intToIpString(mMask));
	}

	@Override
	protected void showSolution() {
		mAnswer.setText(correctAnswer());

		// Set the cursor at the end
		mAnswer.setSelection(mAnswer.getText().length());
	}

	@Override
	protected int exerciseId() {
		return R.string.cidr;
	}

	@Override
	protected String titleString() {
		return getString(R.string.cidr_title);
	}

	@Override
	protected int obtainExerciseId() {
		return R.string.cidr;
	}

}

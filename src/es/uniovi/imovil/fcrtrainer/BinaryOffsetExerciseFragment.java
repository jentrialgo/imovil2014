package es.uniovi.imovil.fcrtrainer;

public class BinaryOffsetExerciseFragment extends BaseNumericalExerciseFragment {

	private static final int POINTS_FOR_QUESTION = 10;
	private int mNumberOfBits = 6;
	private int mOffset = 32;
	private String mCorrectAnswer;

	public static BinaryOffsetExerciseFragment newInstance() {

		BinaryOffsetExerciseFragment fragment = new BinaryOffsetExerciseFragment();
		return fragment;
	}

	public BinaryOffsetExerciseFragment() {
	}

	@Override
	protected int getExerciseId() {
		return R.string.offset_binary;
	}

	@Override
	protected boolean isResultNumeric() {
		return true;
	}

	@Override
	protected String getTitleString() {
		int formatStringId;
		if (mDirectConversion) {
			formatStringId = R.string.convert_dec_to_bin_offset;
		} else {
			formatStringId = R.string.convert_bin_offset_to_dec;
		}
		String formatString = getResources().getString(formatStringId);
		return String.format(formatString, mOffset, mNumberOfBits);
	}

	@Override
	protected String generateRandomNumber() {
		int maxInNaturalBinary = ((int) Math.pow(2, mNumberOfBits));
		int numberInOffset = mRandomGenerator.nextInt(maxInNaturalBinary);

		if (mDirectConversion) {
			mCorrectAnswer = BinaryConverter.binaryToStringWithNbits(
					numberInOffset, mNumberOfBits);
			return Integer.toString(numberInOffset - mOffset);
		} else {
			mCorrectAnswer = Integer.toString(numberInOffset - mOffset);
			return BinaryConverter.binaryToStringWithNbits(numberInOffset,
					mNumberOfBits);
		}
	}

	@Override
	protected String getSolution() {
		return mCorrectAnswer;
	}

	@Override
	protected boolean isCorrect(String answer) {
		return answer.equals(mCorrectAnswer);
	}

	@Override
	protected int pointsForCorrectAnswer() {
		return POINTS_FOR_QUESTION;
	}

}

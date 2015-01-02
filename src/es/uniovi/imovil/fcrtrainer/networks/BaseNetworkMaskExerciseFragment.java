package es.uniovi.imovil.fcrtrainer.networks;

import java.util.Random;

import es.uniovi.imovil.fcrtrainer.BaseExerciseFragment;
import es.uniovi.imovil.fcrtrainer.Level;
import es.uniovi.imovil.fcrtrainer.PreferenceUtils;

public abstract class BaseNetworkMaskExerciseFragment extends BaseExerciseFragment {

	protected Random mRandom = new Random();
	int mMask;

	protected int generateRandomMask() {
		Level level = PreferenceUtils.getLevel(getActivity());
	
		int maxOffset = 8;
		switch (level) {
		case BEGINNER:
			maxOffset = 4;
			break;
		case INTERMEDIATE:
			maxOffset = 16;
			break;
		case PROFICIENCY:
			maxOffset = 26;
			break;
		}
		
		// Add 1 because 0 is not a valid mask
		int offset = mRandom.nextInt(maxOffset) + 1;
	
		return 0xffffffff << offset;
	}

	protected String intToIpString(int ipAddress) {
		int[] bytes = new int[] {
				(ipAddress >> 24 & 0xff),
				(ipAddress >> 16 & 0xff),
				(ipAddress >> 8 & 0xff),
				(ipAddress &0xff)
		};
		return Integer.toString(bytes[0]) 
				+ "." + Integer.toString(bytes[1])
				+ "." + Integer.toString(bytes[2])
				+ "." + Integer.toString(bytes[3]);
	}

}

package es.uniovi.imovil.fcrtrainer;

import android.support.v4.app.Fragment;

/**
 * Clase factoría para crear los diferentes fragmentos de los ejercicios. Cada
 * alumno debería insertar la llamada a su fragmento.newInstance() donde le
 * corresponda.
 * 
 */
final public class FragmentFactory {

	/**
	 * Crea el fragmento de un ejercicio a partir de un índice. Este índice
	 * coincide con el índice de la opción correspondiente en el Drawer.
	 * 
	 * @param índice
	 *            del ejercicio en el Drawer.
	 * @return el fragmento del ejercicio.
	 */
	static Fragment createExercise(int resIndex) {
		switch (resIndex) {
		case R.string.highscores:
			return HighscoresFragment.newInstance();
		case R.string.binary:
			// TODO: return BinaryExerciseFragment.newInstance();
		case R.string.hexadecimal:
			// TODO: return HexadecimalExerciseFragment.newInstance();
		case R.string.sign_and_magnitude:
			// TODO: return SignedMagnitudeExerciseFragment.newInstance();
		case R.string.interpretation:
			// TODO: return InterpretationExerciseFragment.newInstance();
		case R.string.floating_point:
			// TODO: return FloatingPointExerciseFragment.newInstance();
		case R.string.logic_gate:
			// TODO: return LogicGateExerciseFragment.newInstance();
		case R.string.logic_operation:
			// TODO: return LogicOperationExerciseFragment.newInstance();
		case R.string.network_address:
			// TODO: return NetworAddressExerciseFragment.newInstance();
		case R.string.cidr:
			// TODO: return CidrExerciseFragment.newInstance();
		case R.string.host_count:
			// TODO: return HostCountExerciseFragment.newInstance();
		case R.string.network_mask:
			// TODO: return NetworkMaskExerciseFragment.newInstance();
		case R.string.network_layer:
			// TODO: return NetworkLayerExerciseFragment.newInstance();
		case R.string.protocol:
			// TODO: return ProtocolExerciseFragment.newInstance();

			return DummyExerciseFragment.newInstance();

		default:
			throw new IllegalStateException();
		}
	}
}

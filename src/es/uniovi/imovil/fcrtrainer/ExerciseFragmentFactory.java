package es.uniovi.imovil.fcrtrainer;

/**
 * Clase factoría para crear los diferentes fragmentos de los ejercicios.
 * Cada alumno debería insertar la llamada a su fragmento.newInstance() donde
 * le corresponda.
 *
 */
final public class ExerciseFragmentFactory {
	
	static final int CODES_MODULE_BASE_INDEX = 0;	
	static final int BINARY_EXERCISE_INDEX = CODES_MODULE_BASE_INDEX + 1;
	static final int HEXADECIMAL_EXERCISE_INDEX = BINARY_EXERCISE_INDEX + 1;
	static final int SIGNED_MAGNITUDE_EXERCISE_INDEX = HEXADECIMAL_EXERCISE_INDEX + 1;	
	static final int CODES_MODULE_TOP_INDEX = SIGNED_MAGNITUDE_EXERCISE_INDEX;
			
	static final int DIGITAL_SYSTEMS_MODULE_BASE_INDEX = CODES_MODULE_TOP_INDEX + 1;	
	static final int LOGIC_GATE_EXERCISE_INDEX = DIGITAL_SYSTEMS_MODULE_BASE_INDEX + 1;
	static final int LOGIC_OPERATION_EXERCISE_INDEX = LOGIC_GATE_EXERCISE_INDEX + 1;	
	static final int DIGITAL_SYSTEMS_MODULE_TOP_INDEX = LOGIC_OPERATION_EXERCISE_INDEX;
		
	static final int NETWORKS_MODULE_BASE_INDEX = DIGITAL_SYSTEMS_MODULE_TOP_INDEX + 1;	
	static final int NETWORK_ADDRESS_EXERCISE_INDEX = NETWORKS_MODULE_BASE_INDEX + 1;
	static final int CIDR_EXERCISE_INDEX = NETWORK_ADDRESS_EXERCISE_INDEX + 1;
	static final int HOST_COUNT_EXERCISE_INDEX = CIDR_EXERCISE_INDEX + 1;
	static final int NETWORK_MASK_EXERCISE_INDEX = HOST_COUNT_EXERCISE_INDEX + 1;
	static final int NETWORK_LAYER_EXERCISE_INDEX = NETWORK_MASK_EXERCISE_INDEX + 1;
	static final int PROTOCOL_EXERCISE_INDEX = NETWORK_LAYER_EXERCISE_INDEX + 1;		
	static final int NETWORKS_MODULE_TOP_INDEX = PROTOCOL_EXERCISE_INDEX;
	
	
	/**
	 * Crea el fragmento de un ejercicio a partir de un índice. Este índice coincide
	 * con el índice de la opción correspondiente en el Drawer.
	 * @param índice del ejercicio en el Drawer.
	 * @return el fragmento del ejercicio.
	 */
	static BaseExerciseFragment createExercise(int index) {
		switch (index) {
			case BINARY_EXERCISE_INDEX:
				// TODO: return BinaryExerciseFragment.newInstance();
			case HEXADECIMAL_EXERCISE_INDEX:
				// TODO: return HexadecimalExerciseFragment.newInstance();
			case SIGNED_MAGNITUDE_EXERCISE_INDEX:
				// TODO: return SignedMagnitudeExerciseFragment.newInstance();
			case LOGIC_GATE_EXERCISE_INDEX:
				// TODO: return LogicGateExerciseFragment.newInstance();
			case LOGIC_OPERATION_EXERCISE_INDEX:
				// TODO: return LogicOperationExerciseFragment.newInstance();
			case NETWORK_ADDRESS_EXERCISE_INDEX:
				// TODO: return NetworAddressExerciseFragment.newInstance();
			case CIDR_EXERCISE_INDEX:
				// TODO: return CidrExerciseFragment.newInstance();
			case HOST_COUNT_EXERCISE_INDEX:
				// TODO: return HostCountExerciseFragment.newInstance();
			case NETWORK_MASK_EXERCISE_INDEX:
				// TODO: return NetworkMaskExerciseFragment.newInstance();
			case NETWORK_LAYER_EXERCISE_INDEX:
				// TODO: return NetworkLayerExerciseFragment.newInstance();
			case PROTOCOL_EXERCISE_INDEX:
				// TODO: return ProtocolExerciseFragment.newInstance();
				
				return DummyExerciseFragment.newInstance();
					
		default:
			throw new IllegalStateException();
		}
	}
}

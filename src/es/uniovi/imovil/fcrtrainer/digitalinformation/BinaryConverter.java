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

package es.uniovi.imovil.fcrtrainer.digitalinformation;

import java.util.Random;

import android.util.Log;

public class BinaryConverter{
	private Random mRandom = new Random();

	public boolean isStringEmpty(String string) {
		if (string.matches("") || !string.contains("1")) {
			return true;
		}
		return false;
	}
	
	public String deleteStartingZeroesFromBinaryInput(String binaryText){
		if(isStringEmpty(binaryText))
			return "0"; //empty string equals "0" in this case.
		
		String c = "" + binaryText.charAt(0); 
		int i=0;
		int lastPosition = 0;
		while(c.equals("0")){
			c = "" + binaryText.charAt(i); 
			if(c.equals("1")){
				lastPosition = i;
			}
			i++;
		}
		
		if(binaryText.substring(lastPosition) == "")
			return "0";
		//System.out.println("substring: " + binaryText.substring(lastPosition));
		//Now substring and return	
		return binaryText.substring(lastPosition);
	}
	
	public int createRandomNumber(int numberOfBits) {
		double x = Math.pow(2, numberOfBits - 1);
		Log.d("borrame", "number of bits: " + numberOfBits + " x: " + x);
		int maxNumber = (int) x;
		return mRandom.nextInt(maxNumber);
	}

	public String convertDecimalToBinary(int decimalNumber) {
		return Integer.toBinaryString(decimalNumber);
	}
	
	public String convertDecimalToBinary(String decimalNumber) {		
		int number = Integer.parseInt(decimalNumber);
		return convertDecimalToBinary(number);
	}
	
	public String convertBinaryToDecimal(String binary){
		return "" + Integer.parseInt(binary, 2);
	}

	/**
	 * Returns a string with the representation of number with N bits in two's
	 * complement. It assumes N is less or equal than 32 and that it is possible
	 * to do it
	 */
	public static String binaryToStringWithNbits(int number, int N) {
		String formatString = "%" + N + "s";
		String bits = String.format(formatString, Integer.toBinaryString(number))
				.replace(' ', '0');
		
		if (number >= 0) {
			return bits;
		} else {
			return bits.substring(32 - N);
		}
	}
	
}


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
import android.util.SparseArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import es.uniovi.imovil.fcrtrainer.digitalinformation.BinaryExerciseFragment;
import es.uniovi.imovil.fcrtrainer.digitalinformation.FloatingPointExerciseFragment;
import es.uniovi.imovil.fcrtrainer.digitalinformation.HexadecimalExerciseFragment;
import es.uniovi.imovil.fcrtrainer.digitalinformation.SignedMagnitudeExerciseFragment;
import es.uniovi.imovil.fcrtrainer.digitalinformation.TwosComplementExerciseFragment;
import es.uniovi.imovil.fcrtrainer.digitalsystems.LogicGateExerciseFragment;
import es.uniovi.imovil.fcrtrainer.digitalsystems.LogicOperationExerciseFragment;
import es.uniovi.imovil.fcrtrainer.highscores.HighscoresFragment;
import es.uniovi.imovil.fcrtrainer.networks.CidrExerciseFragment;
import es.uniovi.imovil.fcrtrainer.networks.HostCountExerciseFragment;
import es.uniovi.imovil.fcrtrainer.networks.NetworkAddressExerciseFragment;
import es.uniovi.imovil.fcrtrainer.networks.NetworkLayerExerciseFragment;
import es.uniovi.imovil.fcrtrainer.networks.NetworkMaskExerciseFragment;
import es.uniovi.imovil.fcrtrainer.networks.ProtocolExerciseFragment;

/**
 * This enumeration is used for handling navigation, linking the different screens that can be
 * accessed with the navigation drawer to the corresponding fragment. A screen can be an exercise.
 * The ordinal() of this enumeration will be used to identify exercises where numbers are required.
 *
 * Warning: changing the order of the values in the enumeration will cause problems when updating
 * the application. For instance, high score management will not work correctly.
 *
 * To insert a new screen that can be accessed in the navigation drawer, in addition to adding
 * a value in the enumeration, the static maps to strings, fragments and navIds must be updated.
 */
public enum Screen {
    HIGH_SCORES(false),
    BINARY(true), HEXADECIMAL(true), SIGN_MAGNITUDE(true), TWOS_COMPLEMENT(true),
    FLOATING_POINT(true),
    LOGIC_GATE(true), LOGIC_OPERATION(true),
    NETWORK_ADDRESS(true), CIDR(true), HOST_COUNT(true), NETWORK_MASK(true), NETWORK_LAYER(true),
    PROTOCOL(false); // TODO: it is disabled at the moment

    private static Map<Screen, Integer> stringIdMap;
    static {
        stringIdMap = new HashMap<>();
        stringIdMap.put(HIGH_SCORES, R.string.highscores);
        stringIdMap.put(BINARY, R.string.binary);
        stringIdMap.put(HEXADECIMAL, R.string.hexadecimal);
        stringIdMap.put(SIGN_MAGNITUDE, R.string.sign_and_magnitude);
        stringIdMap.put(TWOS_COMPLEMENT, R.string.twoscomplement);
        stringIdMap.put(FLOATING_POINT, R.string.floating_point);
        stringIdMap.put(LOGIC_GATE, R.string.logic_gate);
        stringIdMap.put(LOGIC_OPERATION, R.string.logic_operation);
        stringIdMap.put(NETWORK_ADDRESS, R.string.network_address);
        stringIdMap.put(CIDR, R.string.cidr);
        stringIdMap.put(HOST_COUNT, R.string.host_count);
        stringIdMap.put(NETWORK_MASK, R.string.network_mask);
        stringIdMap.put(NETWORK_LAYER, R.string.network_layer);
        stringIdMap.put(PROTOCOL, R.string.protocol);
    }

    private static Map<Screen, Fragment> fragmentMap;
    static {
        fragmentMap = new HashMap<>();
        fragmentMap.put(Screen.HIGH_SCORES, HighscoresFragment.newInstance());
        fragmentMap.put(Screen.BINARY, BinaryExerciseFragment.newInstance());
        fragmentMap.put(Screen.HEXADECIMAL, HexadecimalExerciseFragment.newInstance());
        fragmentMap.put(Screen.SIGN_MAGNITUDE, SignedMagnitudeExerciseFragment.newInstance());
        fragmentMap.put(Screen.TWOS_COMPLEMENT, TwosComplementExerciseFragment.newInstance());
        fragmentMap.put(Screen.FLOATING_POINT, FloatingPointExerciseFragment.newInstance());
        fragmentMap.put(Screen.LOGIC_GATE, LogicGateExerciseFragment.newInstance());
        fragmentMap.put(Screen.LOGIC_OPERATION, LogicOperationExerciseFragment.newInstance());
        fragmentMap.put(Screen.NETWORK_ADDRESS, NetworkAddressExerciseFragment.newInstance());
        fragmentMap.put(Screen.CIDR, CidrExerciseFragment.newInstance());
        fragmentMap.put(Screen.HOST_COUNT, HostCountExerciseFragment.newInstance());
        fragmentMap.put(Screen.NETWORK_MASK, NetworkMaskExerciseFragment.newInstance());
        fragmentMap.put(Screen.NETWORK_LAYER, NetworkLayerExerciseFragment.newInstance());
        fragmentMap.put(Screen.PROTOCOL, ProtocolExerciseFragment.newInstance());
    }

    private static SparseArray<Screen> navMap;
    static {
        navMap = new SparseArray<>();
        navMap.put(R.id.nav_highscores, Screen.HIGH_SCORES);
        navMap.put(R.id.nav_binary, Screen.BINARY);
        navMap.put(R.id.nav_hexadecimal, Screen.HEXADECIMAL);
        navMap.put(R.id.nav_sign_and_magnitude, Screen.SIGN_MAGNITUDE);
        navMap.put(R.id.nav_twoscomplement, Screen.TWOS_COMPLEMENT);
        navMap.put(R.id.nav_floating_point, Screen.FLOATING_POINT);
        navMap.put(R.id.nav_logic_gate, Screen.LOGIC_GATE);
        navMap.put(R.id.nav_logic_operation, Screen.LOGIC_OPERATION);
        navMap.put(R.id.nav_network_address, Screen.NETWORK_ADDRESS);
        navMap.put(R.id.nav_cidr, Screen.CIDR);
        navMap.put(R.id.nav_host_count, Screen.HOST_COUNT);
        navMap.put(R.id.nav_network_mask, Screen.NETWORK_MASK);
        navMap.put(R.id.nav_network_layer, Screen.NETWORK_LAYER);
        //navMap.put(R.id.nav_protocol, Screen.PROTOCOL); // TODO: it is disabled at the moment
    }

    private static Map<Screen, Integer> leaderboard;
    static {
        leaderboard = new HashMap<>();
        leaderboard.put(Screen.BINARY, R.string.leaderboard_binary);
        leaderboard.put(Screen.HEXADECIMAL, R.string.leaderboard_hexadecimal);
        leaderboard.put(Screen.SIGN_MAGNITUDE, R.string.leaderboard_sign_and_magnitude);
        leaderboard.put(Screen.TWOS_COMPLEMENT, R.string.leaderboard_twos_complement);
        leaderboard.put(Screen.FLOATING_POINT, R.string.leaderboard_floating_point);
        leaderboard.put(Screen.LOGIC_GATE, R.string.leaderboard_logic_gate);
        leaderboard.put(Screen.LOGIC_OPERATION, R.string.leaderboard_logic_operation);
        leaderboard.put(Screen.NETWORK_ADDRESS, R.string.leaderboard_network_address);
        leaderboard.put(Screen.CIDR, R.string.leaderboard_cidr);
        leaderboard.put(Screen.HOST_COUNT, R.string.leaderboard_host_count);
        leaderboard.put(Screen.NETWORK_MASK, R.string.leaderboard_network_mask);
        leaderboard.put(Screen.NETWORK_LAYER, R.string.leaderboard_network_layer);
    }

    private final boolean mIsExercise;

    Screen(boolean isExercise) {
        mIsExercise = isExercise;
    }

    @Override
    public String toString() {
        return getResourceString(stringIdMap.get(this));
    }

    private String getResourceString(int id) {
        return FcrTrainerApplication.getAppContext().getString(id);
    }

    public Fragment toFragment() {
        return fragmentMap.get(this);
    }

    public static Screen fromNavId(int navId) {
        return navMap.get(navId);
    }

    public String toLeaderboardId() {
        return getResourceString(leaderboard.get(this));
    }

    public boolean isExercise() {
        return mIsExercise;
    }

    public static Screen[] exercises() {
        ArrayList<Screen> result = new ArrayList<>();
        for (Screen screen : Screen.values()) {
            if (screen.isExercise()) {
                result.add(screen);
            }
        }
        return result.toArray(new Screen[result.size()]);
    }
}

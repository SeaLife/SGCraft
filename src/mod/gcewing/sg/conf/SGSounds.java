package gcewing.sg.conf;

import gcewing.sg.SGCraft;
import net.minecraft.util.SoundEvent;

/**
 * Class for holding all sounds to make the SGBaseTE great again.
 */
public class SGSounds {

    public static SoundEvent DIAL_FAIL;
    public static SoundEvent CONNECT;
    public static SoundEvent DISCONNECT;
    public static SoundEvent IRIS_OPEN;
    public static SoundEvent IRIS_CLOSE;
    public static SoundEvent IRIS_HIT;
    public static SoundEvent DHD_PRESS;
    public static SoundEvent DHD_DIAL;
    public static SoundEvent CHEVRON_OUTGOING;
    public static SoundEvent CHEVRON_INCOMING;
    public static SoundEvent LOCK_OUTGOING;
    public static SoundEvent LOCK_INCOMING;
    public static SoundEvent GATE_ROLL;

    public static void registerSounds(SGCraft mod) {
        DIAL_FAIL = mod.newSound("dial_fail");
        CONNECT = mod.newSound("gate_open");
        DISCONNECT = mod.newSound("gate_close");
        IRIS_OPEN = mod.newSound("iris_open");
        IRIS_CLOSE = mod.newSound("iris_close");
        IRIS_HIT = mod.newSound("iris_hit");
        DHD_PRESS = mod.newSound("dhd_press");
        DHD_DIAL = mod.newSound("dhd_dial");
        CHEVRON_OUTGOING = mod.newSound("chevron_outgoing");
        CHEVRON_INCOMING = mod.newSound("chevron_incoming");
        LOCK_OUTGOING = mod.newSound("lock_outgoing");
        LOCK_INCOMING = mod.newSound("lock_incoming");
        GATE_ROLL = mod.newSound("gate_roll");
    }
}

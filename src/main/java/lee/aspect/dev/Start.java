package lee.aspect.dev;

import lee.aspect.dev.application.Launch;

public class Start {
    /**
     * Redirect main Class to {@link lee.aspect.dev.application.Launch Launch}
     * This program is used to customize Discord rich perference via a interface by JavaFX
     * It connects to Discord via IPC by the libary {@link lee.aspect.dev.discordipc}
     * @param args
     * @author Qinzhi Li
     */
    public static void main(String[]args){
        Launch.Launch(args);
    }
}

package lee.aspect.dev;

import lee.aspect.dev.application.CustomDiscordRPC;

public class Launch {
    /**
     * Redirect main Class to {@link CustomDiscordRPC Launch}
     * This program is used to customize Discord rich perference via a interface by JavaFX
     * It connects to Discord via IPC by the libary {@link lee.aspect.dev.discordipc}
     * @param args
     * @author Qinzhi Li
     */
    public static void main(String[]args){
        CustomDiscordRPC.getInstance().Launch(args);
    }
}

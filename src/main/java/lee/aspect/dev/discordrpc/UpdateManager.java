package lee.aspect.dev.discordrpc;

import lee.aspect.dev.jsonreader.FileManager;

import java.io.File;


public class UpdateManager {
    protected Script updates;


    public UpdateManager() {
        updates = loadScriptFromJson();
    }


    public Script loadScriptFromJson() {
        Script loaded = FileManager.readFromJson(new File(FileManager.getROOT_DIR(), "UpdateScript.json"), Script.class);
        System.out.println(loaded);
        this.updates = loaded;

        if (loaded == null) {
            loaded = Script.fromTotalUpdates();
            this.updates = loaded;
            Script.addUpdates(new Updates(16000, "1", "", "", "", "First line 1", "Second line 1"));
            saveScriptToFile();
        }


        return loaded;

    }

    public void saveScriptToFile() {
        FileManager.writeJsonTofile(new File(FileManager.getROOT_DIR(), "UpdateScript.json"), updates);
    }

    public Script getUpdates() {
        return updates;
    }


}

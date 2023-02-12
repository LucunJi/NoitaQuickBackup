package io.github.lucunji.noitaqb.config;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.SystemUtils;

public class Configs {
    public static final String LINUX_SAVE_PATH =
            ".steam/steam/steamapps/compatdata/881100/pfx/drive_c/users/steamuser/AppData/LocalLow/Nolla_Games_Noita/";
    public static final String DEFAULT_BACKUP_PATH = "noitaqb/";

    @Getter
    private General general = new General();
    @Getter
    private Save save = new Save();
    @Getter
    private QSave qSave = new QSave();
    @Getter
    private Load load = new Load();

    public static class General {
        @Getter
        private String savePath;
        @Getter
        private String backupPath;
        @Getter @Setter
        private int windowX = 0;
        @Getter @Setter
        private int windowY = 0;
        @Getter @Setter
        private int windowWidth = 480;
        @Getter @Setter
        private int windowHeight = 640;

        public General() {
            if (SystemUtils.IS_OS_LINUX) {
                var home = SystemUtils.getUserHome().toPath();
                savePath = home.resolve(LINUX_SAVE_PATH).toAbsolutePath().toString();
                backupPath = home.resolve(DEFAULT_BACKUP_PATH).toAbsolutePath().toString();
            }
        }
    }

    public static class Save {
//        @Getter
//        @Setter
//        private boolean backupPlayerData = true;
//        @Getter
//        @Setter
//        private boolean backupWorldData = true;
    }

    public static class QSave {
//        @Getter
//        @Setter
//        private boolean backupPlayerData = true;
//        @Getter
//        @Setter
//        private boolean backupWorldData = true;
    }

    public static class Load {
        @Getter
        @Setter
        private boolean qbBeforeLoad = true;
//        @Getter
//        @Setter
//        private boolean loadPlayerData = true;
//        @Getter
//        @Setter
//        private boolean loadWorldData = true;
    }
}

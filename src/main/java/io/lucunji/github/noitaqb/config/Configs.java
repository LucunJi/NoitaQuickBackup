package io.lucunji.github.noitaqb.config;

import io.lucunji.github.noitaqb.FileUtils;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.SystemUtils;

import java.nio.file.Paths;

public class Configs {
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

        public General() {
            if (SystemUtils.IS_OS_LINUX) {
                var home = SystemUtils.getUserHome().getAbsolutePath();
                savePath = Paths.get(home, FileUtils.LINUX_SAVE_PATH).toFile().getAbsolutePath();
                backupPath = Paths.get(home, FileUtils.DEFAULT_BACKUP_PATH).toFile().getAbsolutePath();
            }
        }
    }

    public static class Save {
        @Getter
        @Setter
        private boolean backupPlayerData = false;
        @Getter
        @Setter
        private boolean backupWorldData = false;
    }

    public static class QSave {
        @Getter
        @Setter
        private boolean backupPlayerData = false;
        @Getter
        @Setter
        private boolean backupWorldData = false;
    }

    public static class Load {

    }
}

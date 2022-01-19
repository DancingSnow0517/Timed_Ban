package cn.dancingsnow.timedban.data;

import java.nio.file.Path;

public class Config extends AbstractConfig<Config.ConfigData>{

    private ConfigData config = new ConfigData();

    public Config(Path dataFolderPath) {
        super(dataFolderPath.resolve("config.json"), ConfigData.class);
    }

    @Override
    protected ConfigData getData() {
        return config;
    }

    @Override
    protected void setData(ConfigData data) {
        config = data;
    }

    public static class ConfigData {

        private Boolean status = Boolean.FALSE;

    }

    public Boolean getStatus() {
        return config.status;
    }

    public Boolean setStatus(Boolean status) {
        config.status = status;
        return save();
    }
}

package cn.dancingsnow.timedban.data;

import cn.dancingsnow.timedban.utils.BanPlayer;

import java.nio.file.Path;
import java.util.HashMap;

public class BanList extends AbstractConfig<BanList.ConfigData>{

    private BanList.ConfigData config = new ConfigData();

    public BanList(Path dataFolderPath) {
        super(dataFolderPath.resolve("ban list.json"), ConfigData.class);
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

        private HashMap<String, BanPlayer> banList = new HashMap<>();

    }

    public BanPlayer getBanPlayer(String name) {
        return config.banList.get(name);
    }

    public Boolean hasPlayer(String name) {
        return config.banList.containsKey(name);
    }

    public void addBanPlayer(String name, BanPlayer banPlayer) {
        config.banList.put(name, banPlayer);
        save();
    }

    public void removeBanPlayer(String name) {
        config.banList.remove(name);
        save();
    }

}

package cn.dancingsnow.timedban;

import cn.dancingsnow.timedban.commands.BanCommand;
import cn.dancingsnow.timedban.commands.BanListCommand;
import cn.dancingsnow.timedban.commands.UnBanCommand;
import cn.dancingsnow.timedban.data.BanList;
import cn.dancingsnow.timedban.data.Config;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.event.EventManager;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import javax.annotation.Nullable;
import java.io.File;
import java.nio.file.Path;
import java.util.Objects;

@Plugin(
        id = "timedban",
        name = "Timedban",
        version = BuildConstants.VERSION
)
public class Timedban {

    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();


    @Inject
    public Logger logger;

    @Inject
    public ProxyServer server;

    @Inject
    public CommandManager commandManager;

    @Inject
    public EventManager eventManager;

    @Nullable
    private static Timedban instance;

    @Inject
    @DataDirectory
    public Path dataFolderPath;

    @Inject
    public Injector injector;

    public Config config;

    public BanList banList;

    @NotNull
    public static Timedban getInstance() {
        return Objects.requireNonNull(instance);
    }

    public static Logger logger() {
        return getInstance().logger;
    }



    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        instance = this;
        load();
        logger.info("Plugin Init");

    }

    private void load() {

        File dataFolder = dataFolderPath.toFile();
        if (!dataFolder.exists() && !dataFolder.isDirectory()) {
            if (dataFolder.mkdir()) {
                logger.info("Directory create success");
            }
        }
        config = new Config(dataFolderPath);
        if (!config.load()) {
            logger.error("Timed Ban load fail");
            throw new IllegalStateException("Timed Ban init fail");
        }
        config.save();

        banList = new BanList(dataFolderPath);
        if (!banList.load()) {
            logger.error("Timed Ban load fail");
            throw new IllegalStateException("Timed Ban init fail");
        }
        banList.save();

        // 注册命令
        BanCommand.init(this);
        UnBanCommand.init(this);
        BanListCommand.init(this);
    }
}

package cn.dancingsnow.timedban.commands;

import cn.dancingsnow.timedban.Timedban;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import net.kyori.adventure.text.Component;

@Singleton
public class BanListCommand {

    @Inject
    private Timedban timedban;

    public static void init(Timedban timedban) {
        timedban.server.getEventManager().register(timedban, timedban.injector.getInstance(BanListCommand.class));
        timedban.commandManager.register(timedban.injector.getInstance(BanListCommand.class).createBrigadierCommand());
    }

    public BrigadierCommand createBrigadierCommand() {
        LiteralCommandNode<CommandSource> node = LiteralArgumentBuilder
                .<CommandSource>literal("tbanlist").requires(commandSource -> commandSource.hasPermission("timedban.banlist"))
                .executes(context -> replyMessage(context.getSource()))
                .build();
        return new BrigadierCommand(node);
    }

    private int replyMessage(CommandSource src) {
        src.sendMessage(Component.text("以下玩家已被封禁："));
        timedban.banList.getConfig().forEach(
                (name, banPlayer) -> {
                    src.sendMessage(Component.text(name+" "+"解封时间："+banPlayer.getUnBanTimeStr()+" 原因："+banPlayer.getReason()));
                }
        );
        return 1;
    }

}

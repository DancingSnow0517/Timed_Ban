package cn.dancingsnow.timedban.commands;

import cn.dancingsnow.timedban.Timedban;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;

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
                    src.sendMessage(Component.text(name).color(NamedTextColor.GOLD).clickEvent(ClickEvent.copyToClipboard(name)).hoverEvent(HoverEvent.showText(Component.text("单击粘贴到剪切板")))
                            .append(Component.text(" 解封时间：" + banPlayer.getUnBanTimeStr()).color(NamedTextColor.AQUA))
                            .append(Component.text(" 原因：" + banPlayer.getReason()).color(NamedTextColor.GREEN))
                            .append(Component.text(" [×]").color(NamedTextColor.DARK_RED).clickEvent(ClickEvent.suggestCommand("/tunban " + name))
                                    .hoverEvent(HoverEvent.showText(Component.text("单击将解封命令填到聊天栏")))));
                }
        );
        return 1;
    }

}

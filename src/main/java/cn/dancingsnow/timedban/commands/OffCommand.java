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
public class OffCommand {
    @Inject
    private Timedban timedban;

    public static void init(Timedban timedban) {
        timedban.eventManager.register(timedban, timedban.injector.getInstance(OffCommand.class));
        timedban.commandManager.register(timedban.injector.getInstance(OffCommand.class).createBrigadierCommand());
    }

    public BrigadierCommand createBrigadierCommand() {
        LiteralCommandNode<CommandSource> node = LiteralArgumentBuilder
                .<CommandSource>literal("toff").requires(source -> source.hasPermission("timedban.switch"))
                .executes(context -> {
                    timedban.config.setStatus(false);
                    context.getSource().sendMessage(Component.text("已禁用 Timed Ban"));
                    return 1;
                })
                .build();
        return new BrigadierCommand(node);
    }
}

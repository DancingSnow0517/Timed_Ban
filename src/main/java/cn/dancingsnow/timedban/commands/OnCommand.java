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
public class OnCommand {
    @Inject
    private Timedban timedban;

    public static void init(Timedban timedban) {
        timedban.eventManager.register(timedban, timedban.injector.getInstance(OnCommand.class));
        timedban.commandManager.register(timedban.injector.getInstance(OnCommand.class).createBrigadierCommand());
    }

    public BrigadierCommand createBrigadierCommand() {
        LiteralCommandNode<CommandSource> node = LiteralArgumentBuilder
                .<CommandSource>literal("ton").requires(source -> source.hasPermission("timedban.switch"))
                .executes(context -> {
                    timedban.config.setStatus(true);
                    timedban.logger.info("Timed Ban turned on.");
                    context.getSource().sendMessage(Component.text("已启用 Timed Ban"));
                    return 1;
                })
                .build();
        return new BrigadierCommand(node);
    }
}

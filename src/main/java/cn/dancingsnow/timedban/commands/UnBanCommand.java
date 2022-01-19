package cn.dancingsnow.timedban.commands;

import cn.dancingsnow.timedban.Timedban;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentSkipListSet;

@Singleton
public class UnBanCommand {
    @Inject
    Timedban timedban;

    public static void init(Timedban timedban) {
        timedban.eventManager.register(timedban, timedban.injector.getInstance(UnBanCommand.class));
        timedban.commandManager.register(timedban.injector.getInstance(UnBanCommand.class).createBrigadierCommand());
    }

    public BrigadierCommand createBrigadierCommand() {
        LiteralCommandNode<CommandSource> node = LiteralArgumentBuilder
                .<CommandSource>literal("tunban").requires(source -> source.hasPermission("timedban.unban"))
                .then(RequiredArgumentBuilder.<CommandSource, String>argument("name", StringArgumentType.string())
                        .suggests(this::getUsernameSuggestions)
                        .executes(context -> {
                            String name = context.getArgument("name", String.class);
                            if (timedban.banList.hasPlayer(name)) {
                                timedban.banList.removeBanPlayer(name);
                                context.getSource().sendMessage(Component.text("解禁成功").color(NamedTextColor.GREEN));
                            } else {
                                context.getSource().sendMessage(Component.text("该玩家未被封禁").color(NamedTextColor.RED));
                            }
                            return 1;
                        }))
                .build();
        return new BrigadierCommand(node);
    }

    public CompletableFuture<Suggestions> getUsernameSuggestions(final CommandContext<CommandSource> context, final SuggestionsBuilder builder) {
        ConcurrentSkipListSet<String> playerSet = new ConcurrentSkipListSet<>();
        timedban.banList.getConfig().forEach((s, banPlayer) -> playerSet.add(s));
        playerSet.forEach(
                username -> {
                    if (username.contains(builder.getRemaining())) {
                        builder.suggest(username);
                    }
                }
        );
        return builder.buildFuture();
    }

}

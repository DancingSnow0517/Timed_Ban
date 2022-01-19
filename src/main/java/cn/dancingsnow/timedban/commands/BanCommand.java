package cn.dancingsnow.timedban.commands;

import cn.dancingsnow.timedban.Timedban;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.ProxyServer;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentSkipListSet;

@Singleton
public class BanCommand {

    @Inject
    Timedban timedban;

    @Inject
    public ProxyServer server;

    public static void init(Timedban timedban) {
        timedban.server.getEventManager().register(timedban, timedban.injector.getInstance(BanCommand.class));
        timedban.commandManager.register(timedban.injector.getInstance(BanCommand.class).createBrigadierCommand());
    }

    public BrigadierCommand createBrigadierCommand() {
        LiteralCommandNode<CommandSource> node = LiteralArgumentBuilder
                .<CommandSource>literal("tb").requires(commandSource -> commandSource.hasPermission("timedban.admin"))
                .then(RequiredArgumentBuilder.<CommandSource, String>argument("username", StringArgumentType.string())
                        .suggests(this::getUsernameSuggestions)
                        .then(RequiredArgumentBuilder.argument("day", IntegerArgumentType.integer())))
                .build();
        return new BrigadierCommand(node);
    }

    public CompletableFuture<Suggestions> getUsernameSuggestions(final CommandContext<CommandSource> context, final SuggestionsBuilder builder) {
        ConcurrentSkipListSet<String> playerSet = new ConcurrentSkipListSet<>();
        playerSet.clear();
        server.getAllPlayers().forEach(player -> playerSet.add(player.getUsername()));
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

package cn.dancingsnow.timedban.commands;

import cn.dancingsnow.timedban.Timedban;
import cn.dancingsnow.timedban.utils.BanPlayer;
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
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentSkipListSet;

@Singleton
public class BanCommand {

    @Inject
    private Timedban timedban;

    @Inject
    private ProxyServer server;

    public static void init(Timedban timedban) {
        timedban.server.getEventManager().register(timedban, timedban.injector.getInstance(BanChecks.class));
        timedban.commandManager.register(timedban.injector.getInstance(BanCommand.class).createBrigadierCommand());
    }

    public BrigadierCommand createBrigadierCommand() {
        LiteralCommandNode<CommandSource> node = LiteralArgumentBuilder
                .<CommandSource>literal("tban").requires(commandSource -> commandSource.hasPermission("timedban.ban"))
                .then(RequiredArgumentBuilder.<CommandSource, String>argument("username", StringArgumentType.string())
                        .suggests(this::getUsernameSuggestions)
                        .then(RequiredArgumentBuilder.<CommandSource, Integer>argument("day", IntegerArgumentType.integer())
                                .executes(this::banPlayer)
                                .then(RequiredArgumentBuilder.<CommandSource, String>argument("reason", StringArgumentType.string())
                                        .executes(this::banPlayer))))
                .build();
        return new BrigadierCommand(node);
    }

    private Integer banPlayer(CommandContext<CommandSource> context) {
        BanPlayer banPlayer;
        Optional<Player> player;
        String name = context.getArgument("username", String.class);
        Integer day = context.getArgument("day", Integer.class);
        try {
            String reason = context.getArgument("reason", String.class);
            banPlayer = new BanPlayer(day, reason);
        } catch (IllegalArgumentException e) {
            banPlayer = new BanPlayer(day, "I don't know");
        }
        timedban.banList.addBanPlayer(name, banPlayer);
        player = server.getPlayer(name);
        if (player.isPresent()) {
            player.get().disconnect(banPlayer.getBanMsg());
        }
        timedban.logger.info("Banned {}.", name);
        context.getSource().sendMessage(Component.text("封禁成功").color(NamedTextColor.GREEN));
        return 1;
    }

    public CompletableFuture<Suggestions> getUsernameSuggestions(final CommandContext<CommandSource> context, final SuggestionsBuilder builder) {
        ConcurrentSkipListSet<String> playerSet = new ConcurrentSkipListSet<>();
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

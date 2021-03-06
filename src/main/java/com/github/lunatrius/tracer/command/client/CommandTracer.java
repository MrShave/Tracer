package com.github.lunatrius.tracer.command.client;

import com.github.lunatrius.tracer.handler.ConfigurationHandler;
import com.github.lunatrius.tracer.handler.TraceHandler;
import com.github.lunatrius.tracer.reference.Names;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.EntityList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class CommandTracer extends CommandBase {
    @Override
    public String getName() {
        return Names.Command.NAME;
    }

    @Override
    public String getUsage(final ICommandSender sender) {
        return Names.Command.Message.USAGE;
    }

    @Override
    public boolean checkPermission(final MinecraftServer server, final ICommandSender sender) {
        return true;
    }

    @Override
    public List<String> getTabCompletions(final MinecraftServer server, final ICommandSender sender, final String[] args, final @Nullable BlockPos pos) {
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, Names.Command.REGISTER, Names.Command.UNREGISTER, Names.Command.CLEAR);
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase(Names.Command.REGISTER)) {
                final ArrayList<String> possibilities = new ArrayList<String>();
                for (ResourceLocation location : EntityList.getEntityNameList()) {
                    possibilities.add(location.toString());
                }
                return getSortedListOfStringsMatchingLastWord(args, possibilities);
            } else if (args[0].equalsIgnoreCase(Names.Command.UNREGISTER)) {
                return getSortedListOfStringsMatchingLastWord(args, ConfigurationHandler.getRegisteredEntityNames());
            }
        }

        return Collections.emptyList();
    }

    private List<String> getSortedListOfStringsMatchingLastWord(final String[] args, final List<String> possibilities) {
        Collections.sort(possibilities);
        return getListOfStringsMatchingLastWord(args, possibilities);
    }

    @Override
    public void execute(final MinecraftServer server, final ICommandSender sender, final String[] args) throws CommandException {
        if (args.length == 0) {
            throw new WrongUsageException(getUsage(sender));
        }

        if (args[0].equalsIgnoreCase(Names.Command.REGISTER)) {
            if (args.length == 2) {
                final String entityName = args[1];
                ConfigurationHandler.registerTraceRenderInformation(entityName);
                ConfigurationHandler.loadConfiguration();
                sender.sendMessage(new TextComponentTranslation(Names.Command.Message.REGISTER, entityName));
            } else {
                throw new WrongUsageException(Names.Command.Message.REGISTER_USAGE);
            }
        } else if (args[0].equalsIgnoreCase(Names.Command.UNREGISTER)) {
            if (args.length == 2) {
                final String entityName = args[1];
                ConfigurationHandler.unregisterTraceRenderInformation(entityName);
                ConfigurationHandler.loadConfiguration();
                sender.sendMessage(new TextComponentTranslation(Names.Command.Message.UNREGISTER, entityName));
            } else {
                throw new WrongUsageException(Names.Command.Message.UNREGISTER_USAGE);
            }
        } else if (args[0].equalsIgnoreCase(Names.Command.CLEAR)) {
            final int count = TraceHandler.INSTANCE.clearTraces();
            sender.sendMessage(new TextComponentTranslation(Names.Command.Message.CLEAR, count));
        }
    }
}

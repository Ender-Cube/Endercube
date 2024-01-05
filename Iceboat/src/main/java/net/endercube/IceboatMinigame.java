package net.endercube;

import net.endercube.Common.EndercubeMinigame;
import net.endercube.Common.EndercubeServer;
import net.minestom.server.command.builder.Command;
import net.minestom.server.instance.InstanceContainer;

import java.util.ArrayList;

public class IceboatMinigame extends EndercubeMinigame {


    public IceboatMinigame(EndercubeServer endercubeServer) {
        super(endercubeServer);
    }

    public static void main(String[] args) {
        System.out.println("Hello world!");
    }

    @Override
    public String getName() {
        return "iceboat";
    }

    @Override
    protected ArrayList<InstanceContainer> initInstances() {
        return null;
    }

    @Override
    protected Command initCommands(Command rootCommand) {
        return rootCommand;
    }
}
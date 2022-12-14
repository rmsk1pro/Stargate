package br.trcraft.Stargate.event;

import org.bukkit.event.*;

import br.trcraft.Stargate.*;

public class StargateDeactivateEvent extends StargateEvent
{
    private static final HandlerList handlers;
    
    public HandlerList getHandlers() {
        return StargateDeactivateEvent.handlers;
    }
    
    public static HandlerList getHandlerList() {
        return StargateDeactivateEvent.handlers;
    }
    
    public StargateDeactivateEvent(final Portal portal) {
        super("StargatDeactivateEvent", portal);
    }
    
    static {
        handlers = new HandlerList();
    }
}

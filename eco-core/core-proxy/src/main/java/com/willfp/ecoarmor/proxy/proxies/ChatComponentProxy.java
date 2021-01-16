package com.willfp.ecoarmor.proxy.proxies;


import com.willfp.eco.util.proxy.AbstractProxy;
import org.jetbrains.annotations.NotNull;

public interface ChatComponentProxy extends AbstractProxy {
    /**
     * Modify hover {@link org.bukkit.inventory.ItemStack}s using ArmorDisplay.
     * @param object The NMS ChatComponent to modify.
     * @return The modified ChatComponent.
     */
    Object modifyComponent(@NotNull Object object);
}

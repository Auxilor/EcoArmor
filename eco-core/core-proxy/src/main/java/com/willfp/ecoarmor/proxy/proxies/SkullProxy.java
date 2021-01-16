package com.willfp.ecoarmor.proxy.proxies;

import com.willfp.eco.util.proxy.AbstractProxy;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

public interface SkullProxy extends AbstractProxy {
    /**
     * Set the texture of a skull from base64.
     *
     * @param meta   The meta to modify.
     * @param base64 The base64 texture.
     */
    void setTexture(@NotNull SkullMeta meta,
                    @NotNull String base64);
}

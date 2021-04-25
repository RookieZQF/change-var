package com.perfma.xlab.change.var;

import com.perfma.xlab.xpocket.spi.XPocketPlugin;
import com.perfma.xlab.xpocket.spi.command.AbstractXPocketCommand;
import com.perfma.xlab.xpocket.spi.command.CommandList;
import com.perfma.xlab.xpocket.spi.process.XPocketProcess;

/**
 * @author: ZQF
 * @date: 2021-04-23
 * @description: desc
 */
@CommandList(names={"attach","read", "write", "detach"},
        usage={"attach pid,attach a any process[now only support Linux little-endian]",
                "read symbol length,read assign symbol and length data[length only 1,4,8]",
                "write symbol length value,write assign value and length data[length only 1,4,8]",
                "detach, unlock process"
        })
public class ChangeVarCommand extends AbstractXPocketCommand {
    private ChangeVarPlugin plugin;

    @Override
    public boolean isPiped() {
        return false;
    }

    @Override
    public void init(XPocketPlugin plugin) {
        this.plugin = (ChangeVarPlugin)plugin;
    }

    @Override
    public boolean isAvailableNow(String cmd) {
        return plugin.isAvailableNow(cmd);
    }

    @Override
    public void invoke(XPocketProcess process) throws Throwable {
        plugin.invoke(process);
    }

    @Override
    public String details(String cmd) {
        return plugin.details(cmd);
    }
}

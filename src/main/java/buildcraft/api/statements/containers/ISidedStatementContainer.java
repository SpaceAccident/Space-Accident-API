package buildcraft.api.statements.containers;

import buildcraft.api.statements.IStatementContainer;
import net.minecraftforge.common.util.ForgeDirection;

public interface ISidedStatementContainer extends IStatementContainer {
	ForgeDirection getSide();
}

package org.violetmoon.zeta.block;

import java.util.function.BooleanSupplier;

import net.minecraft.world.level.block.state.properties.WoodType;
import org.jetbrains.annotations.Nullable;

import net.minecraft.world.level.block.FenceGateBlock;
import org.violetmoon.zeta.module.ZetaModule;
import org.violetmoon.zeta.util.BooleanSuppliers;

/**
 * @author WireSegal
 * Created at 9:14 PM on 10/8/19.
 */
public class ZetaFenceGateBlock extends FenceGateBlock implements IZetaBlock {

	private final @Nullable ZetaModule module;
	private BooleanSupplier enabledSupplier = BooleanSuppliers.TRUE;

	public ZetaFenceGateBlock(String regname, @Nullable ZetaModule module, Properties properties) {
		super(properties, WoodType.OAK); //TODO 1.20: change parameter or pass SoundType parameters thru
		this.module = module;

		if(module == null) //auto registration below this line
			return;

		module.zeta.registry.registerBlock(this, regname, true);
	}

	@Override
	public ZetaFenceGateBlock setCondition(BooleanSupplier enabledSupplier) {
		this.enabledSupplier = enabledSupplier;
		return this;
	}

	@Override
	public boolean doesConditionApply() {
		return enabledSupplier.getAsBoolean();
	}

	@Nullable
	@Override
	public ZetaModule getModule() {
		return module;
	}

}
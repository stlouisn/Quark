package vazkii.quark.building.feature;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.oredict.OreDictionary;
import vazkii.arl.util.RecipeHandler;
import vazkii.quark.base.module.Feature;
import vazkii.quark.building.block.BlockVerticalPlanks;

public class VerticalWoodPlanks extends Feature {

	public static Block vertical_planks;
	
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		vertical_planks = new BlockVerticalPlanks();
		
		for(int i = 0; i < 6; i++) {
			RecipeHandler.addShapelessOreDictRecipe(new ItemStack(vertical_planks, 1, i), new ItemStack(Blocks.PLANKS, 1, i));
			RecipeHandler.addShapelessOreDictRecipe(new ItemStack(Blocks.PLANKS, 1, i), new ItemStack(vertical_planks, 1, i));
		}
		OreDictionary.registerOre("plankWood", new ItemStack(vertical_planks, 1, OreDictionary.WILDCARD_VALUE));
	}
	
}

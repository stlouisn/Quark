package org.violetmoon.quark.content.mobs.module;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacements.Type;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.levelgen.structure.Structure;

import java.util.List;
import java.util.stream.Collectors;

import org.violetmoon.quark.base.Quark;
import org.violetmoon.quark.base.config.Config;
import org.violetmoon.quark.base.config.type.CompoundBiomeConfig;
import org.violetmoon.quark.base.config.type.CostSensitiveEntitySpawnConfig;
import org.violetmoon.quark.base.config.type.EntitySpawnConfig;
import org.violetmoon.quark.base.handler.EntityAttributeHandler;
import org.violetmoon.quark.base.handler.advancement.QuarkAdvancementHandler;
import org.violetmoon.quark.base.handler.advancement.mod.MonsterHunterModifier;
import org.violetmoon.quark.base.world.EntitySpawnHandler;
import org.violetmoon.quark.content.mobs.client.render.entity.SoulBeadRenderer;
import org.violetmoon.quark.content.mobs.client.render.entity.WraithRenderer;
import org.violetmoon.quark.content.mobs.entity.SoulBead;
import org.violetmoon.quark.content.mobs.entity.Wraith;
import org.violetmoon.quark.content.mobs.item.SoulBeadItem;
import org.violetmoon.zeta.client.event.load.ZClientSetup;
import org.violetmoon.zeta.event.bus.LoadEvent;
import org.violetmoon.zeta.event.load.ZCommonSetup;
import org.violetmoon.zeta.event.load.ZConfigChanged;
import org.violetmoon.zeta.event.load.ZRegister;
import org.violetmoon.zeta.module.ZetaLoadModule;
import org.violetmoon.zeta.module.ZetaModule;
import org.violetmoon.zeta.util.Hint;

@ZetaLoadModule(category = "mobs")
public class WraithModule extends ZetaModule {

	public static EntityType<Wraith> wraithType;
	public static EntityType<SoulBead> soulBeadType;

	@Config(description = "List of sound sets to use with wraiths.\nThree sounds must be provided per entry, separated by | (in the format idle|hurt|death). Leave blank for no sound (i.e. if a mob has no ambient noise)")
	private static List<String> wraithSounds = Lists.newArrayList(
			"entity.sheep.ambient|entity.sheep.hurt|entity.sheep.death",
			"entity.cow.ambient|entity.cow.hurt|entity.cow.death",
			"entity.pig.ambient|entity.pig.hurt|entity.pig.death",
			"entity.chicken.ambient|entity.chicken.hurt|entity.chicken.death",
			"entity.horse.ambient|entity.horse.hurt|entity.horse.death",
			"entity.cat.ambient|entity.cat.hurt|entity.cat.death",
			"entity.wolf.ambient|entity.wolf.hurt|entity.wolf.death",
			"entity.villager.ambient|entity.villager.hurt|entity.villager.death",
			"entity.polar_bear.ambient|entity.polar_bear.hurt|entity.polar_bear.death",
			"entity.zombie.ambient|entity.zombie.hurt|entity.zombie.death",
			"entity.skeleton.ambient|entity.skeleton.hurt|entity.skeleton.death",
			"entity.spider.ambient|entity.spider.hurt|entity.spider.death",
			"|entity.creeper.hurt|entity.creeper.death",
			"entity.endermen.ambient|entity.endermen.hurt|entity.endermen.death",
			"entity.zombie_pig.ambient|entity.zombie_pig.hurt|entity.zombie_pig.death",
			"entity.witch.ambient|entity.witch.hurt|entity.witch.death",
			"entity.blaze.ambient|entity.blaze.hurt|entity.blaze.death",
			"entity.llama.ambient|entity.llama.hurt|entity.llama.death",
			"|quark:entity.stoneling.cry|quark:entity.stoneling.die",
			"quark:entity.frog.idle|quark:entity.frog.hurt|quark:entity.frog.die"
			);

	@Config
	public static EntitySpawnConfig spawnConfig = new CostSensitiveEntitySpawnConfig(5, 1, 3, 0.7, 0.15, CompoundBiomeConfig.fromBiomeReslocs(false, "minecraft:soul_sand_valley"));

	public static TagKey<Block> wraithSpawnableTag;

	public static TagKey<Structure> soulBeadTargetTag;

	public static List<String> validWraithSounds;
	
	@Hint Item soul_bead;

	@LoadEvent
	public final void register(ZRegister event) {
		soul_bead = new SoulBeadItem(this);

		wraithType = EntityType.Builder.of(Wraith::new, MobCategory.MONSTER)
				.sized(0.6F, 1.95F)
				.clientTrackingRange(8)
				.fireImmune()
				.setCustomClientFactory((spawnEntity, world) -> new Wraith(wraithType, world))
				.build("wraith");
		Quark.ZETA.registry.register(wraithType, "wraith", Registry.ENTITY_TYPE_REGISTRY);

		soulBeadType = EntityType.Builder.of(SoulBead::new, MobCategory.MISC)
				.sized(0F, 0F)
				.clientTrackingRange(4)
				.updateInterval(10) // update frequency
				.fireImmune()
				.setCustomClientFactory((spawnEntity, world) -> new SoulBead(soulBeadType, world))
				.build("soul_bead");
		Quark.ZETA.registry.register(soulBeadType, "soul_bead", Registry.ENTITY_TYPE_REGISTRY);

		EntitySpawnHandler.registerSpawn(wraithType, MobCategory.MONSTER, Type.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules, spawnConfig);
		EntitySpawnHandler.addEgg(this, wraithType, 0xececec, 0xbdbdbd, spawnConfig);

		EntityAttributeHandler.put(wraithType, Wraith::registerAttributes);
		
		QuarkAdvancementHandler.addModifier(new MonsterHunterModifier(this, ImmutableSet.of(wraithType)));
	}

	@LoadEvent
	public final void setup(ZCommonSetup event) {
		wraithSpawnableTag = BlockTags.create(new ResourceLocation(Quark.MOD_ID, "wraith_spawnable"));
		soulBeadTargetTag = TagKey.create(Registry.STRUCTURE_REGISTRY, new ResourceLocation(Quark.MOD_ID, "soul_bead_target"));
	}

	@LoadEvent
	public final void clientSetup(ZClientSetup event) {
		EntityRenderers.register(wraithType, WraithRenderer::new);
		EntityRenderers.register(soulBeadType, SoulBeadRenderer::new);
	}

	@LoadEvent
	public final void configChanged(ZConfigChanged event) {
		validWraithSounds = wraithSounds.stream().filter((s) -> s.split("\\|").length == 3).collect(Collectors.toList());
	}

}
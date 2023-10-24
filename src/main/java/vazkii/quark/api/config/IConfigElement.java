package vazkii.quark.api.config;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@Deprecated
@OnlyIn(Dist.CLIENT)
public interface IConfigElement extends Comparable<IConfigElement> {

	String getName();
	String getGuiDisplayName();
	List<String> getTooltip();
	@Nullable IConfigCategory getParent();
	boolean isDirty();
	void clean();
	void save();

	void refresh();
	void reset(boolean hard);

}

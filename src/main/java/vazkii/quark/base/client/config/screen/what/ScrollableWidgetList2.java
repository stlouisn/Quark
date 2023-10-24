package vazkii.quark.base.client.config.screen.what;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.Screen;
import vazkii.quark.base.client.config.screen.WidgetWrapper;

public class ScrollableWidgetList2<S extends Screen, E extends ScrollableWidgetList2.Entry<E>> extends ObjectSelectionList<E> {
	public final S parent;

	public ScrollableWidgetList2(S parent) {
		super(Minecraft.getInstance(), parent.width, parent.height, 40, parent.height - 40, 30);
		this.parent = parent;
	}

	//making public
	@Override
	public int addEntry(E entry) {
		return super.addEntry(entry);
	}

	@Override
	protected int getScrollbarPosition() {
		return super.getScrollbarPosition() + 20;
	}

	@Override
	public int getRowWidth() {
		return super.getRowWidth() + 50;
	}

	public void forEachWidgetWrapper(Consumer<WidgetWrapper> action) {
		children().forEach(e -> e.children.forEach(action));
	}

	//Taking Consumers b/c these methods on Screen are protected. Just pass this::addRenderableWidget
	public void addChildWidgets(Consumer<AbstractWidget> addRenderableWidget, Consumer<AbstractWidget> addWidget) {
		forEachWidgetWrapper(w -> {
			if(w.widget instanceof Button)
				addRenderableWidget.accept(w.widget);
			else
				addWidget.accept(w.widget);
		});
	}

	public void removeChildWidgets(Consumer<AbstractWidget> removeWidget) {
		forEachWidgetWrapper(w -> removeWidget.accept(w.widget));
	}

	// Intended flow (from Screen.render):
	//
	// list.render(mstack, x, y, pt);
	// super.render(mstack, x, y, pt);
	// list.reenableVisibleWidgets();

	@Override
	public void render(@Nonnull PoseStack mstack, int mouseX, int mouseY, float partialTicks) {
		//turn off wasOnScreen, then render widgets - minecraft does some simple culling when rendering,
		//and as a side effect of Entry.render, wasOnScreen will be turned back on
		forEachWidgetWrapper(w -> {
			w.widget.visible = false;
			w.wasOnScreen = false;
		});
		super.render(mstack, mouseX, mouseY, partialTicks);
	}

	public void reenableVisibleWidgets() {
		//if a widget was on screen, re-enable its "visible" flag so you can click on them
		forEachWidgetWrapper(w -> {
			if(w.wasOnScreen)
				w.widget.visible = true;
		});
	}

	public static abstract class Entry<E extends ScrollableWidgetList2.Entry<E>> extends ObjectSelectionList.Entry<E> {
		public List<WidgetWrapper> children = new ArrayList<>();

		@Override
		public void render(@Nonnull PoseStack mstack, int index, int rowTop, int rowLeft, int rowWidth, int rowHeight, int mouseX, int mouseY, boolean hovered, float partialTicks) {
			children.forEach(c -> {
				c.updatePosition(rowLeft, rowTop);

				c.wasOnScreen = true;

				//only enable the visible flag as long as needed to render the widget
				c.widget.visible = true;
				c.widget.render(mstack, mouseX, mouseY, partialTicks);
				c.widget.visible = false;
			});
		}

		//Convenience for drawing a striped background
		public void drawBackground(PoseStack mstack, int index, int rowTop, int rowLeft, int rowWidth, int rowHeight, int mouseX, int mouseY, boolean hovered) {
			if(index % 2 == 0)
				fill(mstack, rowLeft, rowTop, rowLeft + rowWidth, rowTop + rowHeight, 0x66000000);

			if(hovered) {
				fill(mstack, rowLeft, rowTop, rowLeft + 1, rowTop + rowHeight, 0xFFFFFFFF);
				fill(mstack, rowLeft + rowWidth - 1, rowTop, rowLeft + rowWidth, rowTop + rowHeight, 0xFFFFFFFF);

				fill(mstack, rowLeft, rowTop, rowLeft + rowWidth, rowTop + 1, 0xFFFFFFFF);
				fill(mstack, rowLeft, rowTop + rowHeight - 1, rowLeft + rowWidth, rowTop + rowHeight, 0xFFFFFFFF);
			}
		}
	}
}

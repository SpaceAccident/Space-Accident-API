package space.accident.api.interfaces;

/**
 * To allow addons to make use of GuiIcon
 */
public interface IGuiIcon {
	int getX();
	int getY();
	int getWidth();
	int getHeight();
	int getTexId();
	IGuiIcon getOverlay();
}
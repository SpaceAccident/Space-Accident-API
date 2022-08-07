package space.accident.main.render;

import codechicken.lib.vec.Rotation;
import codechicken.lib.vec.Scale;
import codechicken.lib.vec.Transformation;
import codechicken.lib.vec.Translation;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.common.util.ForgeDirection;
import org.lwjgl.opengl.GL11;
import space.accident.api.interfaces.tileentity.ICoverable;
import space.accident.api.metatileentity.base.BaseMetaPipeEntity;
import space.accident.structurelib.alignment.IAlignment;
import space.accident.structurelib.alignment.IAlignmentProvider;

import static org.lwjgl.opengl.GL11.GL_LINE_LOOP;

public class RenderUtil {
	
	public static final int ROTATION_MARKER_RESOLUTION = 120;
	private static final int[][] GRID_SWITCH_TABLE = new int[][]{{0, 5, 3, 1, 2, 4}, {5, 0, 1, 3, 2, 4}, {1, 3, 0, 5, 2, 4}, {3, 1, 5, 0, 2, 4}, {4, 2, 3, 1, 0, 5}, {2, 4, 3, 1, 5, 0},};
	private static final Transformation ROTATION_MARKER_TRANSFORM_CENTER = new Scale(0.5);
	private static final Transformation[] ROTATION_MARKER_TRANSFORMS_SIDES_TRANSFORMS = {new Scale(0.25).with(new Translation(0, 0, 0.375)).compile(), new Scale(0.25).with(new Translation(0.375, 0, 0)).compile(), new Scale(0.25).with(new Translation(0, 0, -0.375)).compile(), new Scale(0.25).with(new Translation(-0.375, 0, 0)).compile(),};
	private static final Transformation[] ROTATION_MARKER_TRANSFORMS_CORNER = {new Scale(0.25).with(new Translation(0.375, 0, 0.375)).compile(), new Scale(0.25).with(new Translation(-0.375, 0, 0.375)).compile(), new Scale(0.25).with(new Translation(0.375, 0, -0.375)).compile(), new Scale(0.25).with(new Translation(-0.375, 0, -0.375)).compile(),};
	private static final int[] ROTATION_MARKER_TRANSFORMS_SIDES = {-1, -1, 2, 0, 3, 1, -1, -1, 0, 2, 3, 1, 0, 2, -1, -1, 3, 1, 2, 0, -1, -1, 3, 1, 1, 3, 2, 0, -1, -1, 3, 1, 2, 0, -1, -1};
	
	public static void renderItemIcon(IIcon icon, double size, double z, float nx, float ny, float nz) {
		renderItemIcon(icon, 0.0D, 0.0D, size, size, z, nx, ny, nz);
	}
	
	public static void renderItemIcon(IIcon icon, double xStart, double yStart, double xEnd, double yEnd, double z, float nx, float ny, float nz) {
		if (icon == null) {
			return;
		}
		Tessellator.instance.startDrawingQuads();
		Tessellator.instance.setNormal(nx, ny, nz);
		if (nz > 0.0F) {
			Tessellator.instance.addVertexWithUV(xStart, yStart, z, icon.getMinU(), icon.getMinV());
			Tessellator.instance.addVertexWithUV(xEnd, yStart, z, icon.getMaxU(), icon.getMinV());
			Tessellator.instance.addVertexWithUV(xEnd, yEnd, z, icon.getMaxU(), icon.getMaxV());
			Tessellator.instance.addVertexWithUV(xStart, yEnd, z, icon.getMinU(), icon.getMaxV());
		} else {
			Tessellator.instance.addVertexWithUV(xStart, yEnd, z, icon.getMinU(), icon.getMaxV());
			Tessellator.instance.addVertexWithUV(xEnd, yEnd, z, icon.getMaxU(), icon.getMaxV());
			Tessellator.instance.addVertexWithUV(xEnd, yStart, z, icon.getMaxU(), icon.getMinV());
			Tessellator.instance.addVertexWithUV(xStart, yStart, z, icon.getMinU(), icon.getMinV());
		}
		Tessellator.instance.draw();
	}
	private static int rotationMarkerDisplayList;
	private static boolean rotationMarkerDisplayListCompiled = false;
	private static boolean checkedForChicken = false;
	
	public static void drawGrid(DrawBlockHighlightEvent aEvent, boolean showCoverConnections, boolean aIsWrench, boolean aIsSneaking) {
		if (!checkedForChicken) {
			try {
				Class.forName("codechicken.lib.vec.Rotation");
			} catch (ClassNotFoundException e) {
				return;
			}
			checkedForChicken = true;
		}
		
		GL11.glPushMatrix();
		GL11.glTranslated(-(aEvent.player.lastTickPosX + (aEvent.player.posX - aEvent.player.lastTickPosX) * (double) aEvent.partialTicks), -(aEvent.player.lastTickPosY + (aEvent.player.posY - aEvent.player.lastTickPosY) * (double) aEvent.partialTicks), -(aEvent.player.lastTickPosZ + (aEvent.player.posZ - aEvent.player.lastTickPosZ) * (double) aEvent.partialTicks));
		GL11.glTranslated((float) aEvent.target.blockX + 0.5F, (float) aEvent.target.blockY + 0.5F, (float) aEvent.target.blockZ + 0.5F);
		final int tSideHit = aEvent.target.sideHit;
		Rotation.sideRotations[tSideHit].glApply();
		// draw grid
		GL11.glTranslated(0.0D, -0.501D, 0.0D);
		GL11.glLineWidth(2.0F);
		GL11.glColor4f(0.0F, 0.0F, 0.0F, 0.5F);
		GL11.glBegin(GL11.GL_LINES);
		GL11.glVertex3d(+.50D, .0D, -.25D);
		GL11.glVertex3d(-.50D, .0D, -.25D);
		GL11.glVertex3d(+.50D, .0D, +.25D);
		GL11.glVertex3d(-.50D, .0D, +.25D);
		GL11.glVertex3d(+.25D, .0D, -.50D);
		GL11.glVertex3d(+.25D, .0D, +.50D);
		GL11.glVertex3d(-.25D, .0D, -.50D);
		GL11.glVertex3d(-.25D, .0D, +.50D);
		final TileEntity tTile = aEvent.player.worldObj.getTileEntity(aEvent.target.blockX, aEvent.target.blockY, aEvent.target.blockZ);
		
		// draw connection indicators
		int tConnections = 0;
		if (tTile instanceof ICoverable) {
			if (showCoverConnections) {
				for (int i = 0; i < 6; i++) {
					if (((ICoverable) tTile).getCoverIDAtSide(i) > 0) tConnections = tConnections + (1 << i);
				}
			} else if (tTile instanceof BaseMetaPipeEntity) tConnections = ((BaseMetaPipeEntity) tTile).mConnections;
		}
		
		if (tConnections > 0) {
			for (int i = 0; i < 6; i++) {
				if ((tConnections & (1 << i)) != 0) {
					switch (GRID_SWITCH_TABLE[aEvent.target.sideHit][i]) {
						case 0:
							GL11.glVertex3d(+.25D, .0D, +.25D);
							GL11.glVertex3d(-.25D, .0D, -.25D);
							GL11.glVertex3d(-.25D, .0D, +.25D);
							GL11.glVertex3d(+.25D, .0D, -.25D);
							break;
						case 1:
							GL11.glVertex3d(-.25D, .0D, +.50D);
							GL11.glVertex3d(+.25D, .0D, +.25D);
							GL11.glVertex3d(-.25D, .0D, +.25D);
							GL11.glVertex3d(+.25D, .0D, +.50D);
							break;
						case 2:
							GL11.glVertex3d(-.50D, .0D, -.25D);
							GL11.glVertex3d(-.25D, .0D, +.25D);
							GL11.glVertex3d(-.50D, .0D, +.25D);
							GL11.glVertex3d(-.25D, .0D, -.25D);
							break;
						case 3:
							GL11.glVertex3d(-.25D, .0D, -.50D);
							GL11.glVertex3d(+.25D, .0D, -.25D);
							GL11.glVertex3d(-.25D, .0D, -.25D);
							GL11.glVertex3d(+.25D, .0D, -.50D);
							break;
						case 4:
							GL11.glVertex3d(+.50D, .0D, -.25D);
							GL11.glVertex3d(+.25D, .0D, +.25D);
							GL11.glVertex3d(+.50D, .0D, +.25D);
							GL11.glVertex3d(+.25D, .0D, -.25D);
							break;
						case 5:
							GL11.glVertex3d(+.50D, .0D, +.50D);
							GL11.glVertex3d(+.25D, .0D, +.25D);
							GL11.glVertex3d(+.50D, .0D, +.25D);
							GL11.glVertex3d(+.25D, .0D, +.50D);
							GL11.glVertex3d(+.50D, .0D, -.50D);
							GL11.glVertex3d(+.25D, .0D, -.25D);
							GL11.glVertex3d(+.50D, .0D, -.25D);
							GL11.glVertex3d(+.25D, .0D, -.50D);
							GL11.glVertex3d(-.50D, .0D, +.50D);
							GL11.glVertex3d(-.25D, .0D, +.25D);
							GL11.glVertex3d(-.50D, .0D, +.25D);
							GL11.glVertex3d(-.25D, .0D, +.50D);
							GL11.glVertex3d(-.50D, .0D, -.50D);
							GL11.glVertex3d(-.25D, .0D, -.25D);
							GL11.glVertex3d(-.50D, .0D, -.25D);
							GL11.glVertex3d(-.25D, .0D, -.50D);
							break;
					}
				}
			}
		}
		GL11.glEnd();
		// draw turning indicator
		if (aIsWrench && tTile instanceof IAlignmentProvider) {
			final IAlignment tAlignment = ((IAlignmentProvider) (tTile)).getAlignment();
			if (tAlignment != null) {
				final ForgeDirection direction = tAlignment.getDirection();
				if (direction.ordinal() == tSideHit) drawExtendedRotationMarker(ROTATION_MARKER_TRANSFORM_CENTER, aIsSneaking, false);
				else if (direction.getOpposite().ordinal() == tSideHit) {
					for (Transformation t : ROTATION_MARKER_TRANSFORMS_CORNER) {
						drawExtendedRotationMarker(t, aIsSneaking, true);
					}
				} else {
					drawExtendedRotationMarker(ROTATION_MARKER_TRANSFORMS_SIDES_TRANSFORMS[ROTATION_MARKER_TRANSFORMS_SIDES[tSideHit * 6 + direction.ordinal()]], aIsSneaking, true);
				}
			}
		}
		GL11.glPopMatrix(); // get back to player center
	}
	
	private static void drawExtendedRotationMarker(Transformation transform, boolean sneaking, boolean small) {
		if (sneaking) drawFlipMarker(transform);
		else drawRotationMarker(transform);
	}
	
	private static void drawRotationMarker(Transformation transform) {
		if (!rotationMarkerDisplayListCompiled) {
			rotationMarkerDisplayList = GLAllocation.generateDisplayLists(1);
			compileRotationMarkerDisplayList(rotationMarkerDisplayList);
			rotationMarkerDisplayListCompiled = true;
		}
		GL11.glPushMatrix();
		transform.glApply();
		GL11.glCallList(rotationMarkerDisplayList);
		GL11.glPopMatrix();
	}
	
	private static void compileRotationMarkerDisplayList(int displayList) {
		GL11.glNewList(displayList, GL11.GL_COMPILE);
		GL11.glBegin(GL_LINE_LOOP);
		for (int i = 0; i <= ROTATION_MARKER_RESOLUTION; i++) {
			GL11.glVertex3d(Math.cos(i * Math.PI * 1.75 / ROTATION_MARKER_RESOLUTION) * 0.4, 0, Math.sin(i * Math.PI * 1.75 / ROTATION_MARKER_RESOLUTION) * 0.4);
		}
		for (int i = ROTATION_MARKER_RESOLUTION; i >= 0; i--) {
			GL11.glVertex3d(Math.cos(i * Math.PI * 1.75 / ROTATION_MARKER_RESOLUTION) * 0.24, 0, Math.sin(i * Math.PI * 1.75 / ROTATION_MARKER_RESOLUTION) * 0.24);
		}
		GL11.glVertex3d(0.141114561800, 0, 0);
		GL11.glVertex3d(0.32, 0, -0.178885438199);
		GL11.glVertex3d(0.498885438199, 0, 0);
		GL11.glEnd();
		GL11.glEndList();
	}
	
	private static void drawFlipMarker(Transformation transform) {
		GL11.glPushMatrix();
		transform.glApply();
		final Tessellator t = Tessellator.instance;
		// right shape
		GL11.glLineStipple(4, (short) 0xAAAA);
		GL11.glEnable(GL11.GL_LINE_STIPPLE);
		t.startDrawing(GL11.GL_LINE_STRIP);
		t.addVertex(0.1d, 0d, 0.04d);
		t.addVertex(0.1d, 0d, 0.2d);
		t.addVertex(0.35d, 0d, 0.35d);
		t.addVertex(0.35d, 0d, -0.35d);
		t.addVertex(0.1d, 0d, -0.2d);
		t.addVertex(0.1d, 0d, -0.04d);
		t.draw();
		GL11.glDisable(GL11.GL_LINE_STIPPLE);
		// left shape
		t.startDrawing(GL11.GL_LINE_STRIP);
		t.addVertex(-0.1d, 0d, 0.04d);
		t.addVertex(-0.1d, 0d, 0.2d);
		t.addVertex(-0.35d, 0d, 0.35d);
		t.addVertex(-0.35d, 0d, -0.35d);
		t.addVertex(-0.1d, 0d, -0.2d);
		t.addVertex(-0.1d, 0d, -0.04d);
		t.draw();
		// arrow
		t.startDrawing(GL11.GL_LINE_LOOP);
		t.addVertex(0.15d, 0d, -0.04d);
		t.addVertex(0.15d, 0d, -0.1d);
		t.addVertex(0.25d, 0d, 0.d);
		t.addVertex(0.15d, 0d, 0.1d);
		t.addVertex(0.15d, 0d, 0.04d);
		t.addVertex(-0.15d, 0d, 0.04d);
		t.addVertex(-0.15d, 0d, 0.1d);
		t.addVertex(-0.25d, 0d, 0.d);
		t.addVertex(-0.15d, 0d, -0.1d);
		t.addVertex(-0.15d, 0d, -0.04d);
		t.draw();
		GL11.glPopMatrix();
	}
}
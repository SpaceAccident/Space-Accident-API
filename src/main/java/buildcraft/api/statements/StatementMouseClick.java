package buildcraft.api.statements;

public final class StatementMouseClick {
	private int button;
	private boolean shift;
	
	public StatementMouseClick(int button, boolean shift) {
		this.button = button;
		this.shift = shift;
	}
	
	public boolean isShift() {
		return this.shift;
	}
	
	public int getButton() {
		return this.button;
	}
}

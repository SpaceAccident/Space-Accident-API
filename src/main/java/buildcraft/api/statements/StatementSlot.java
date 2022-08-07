package buildcraft.api.statements;

public class StatementSlot {
	public IStatement statement;
	public IStatementParameter[] parameters;
	
	public StatementSlot() {
	}
	
	public boolean equals(Object o) {
		if (o != null && o instanceof StatementSlot) {
			StatementSlot s = (StatementSlot)o;
			if (s.statement == this.statement && this.parameters.length == s.parameters.length) {
				for(int i = 0; i < this.parameters.length; ++i) {
					IStatementParameter p1 = this.parameters[i];
					IStatementParameter p2 = s.parameters[i];
					if (p1 == null && p2 != null) {
						return false;
					}
					
					if (!p1.equals(p2)) {
						return false;
					}
				}
				
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
}
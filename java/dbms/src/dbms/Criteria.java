package dbms;

public class Criteria {
	String colName;
	String value;
	String operator;
	
	public Criteria() {
		this.colName = new String();
		this.value = new String();
		this.operator = new String();
	}
	
	public Criteria(String colName, String value, String operator) {
		this.colName = colName;
		this.value = value;
		this.operator = operator;
	}
}

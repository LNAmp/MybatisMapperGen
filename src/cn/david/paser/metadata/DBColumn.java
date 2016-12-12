package cn.david.paser.metadata;

class DBColumn {
	private String fieldName;
	private String columnName;
	private String javaType;
	private String jdbcType;
	private String simpleJavaType;
	private boolean id;
	public String getFieldName() {
		return fieldName;
	}
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	public String getColumnName() {
		return columnName;
	}
	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}
	public String getJavaType() {
		return javaType;
	}
	public void setJavaType(String javaType) {
		this.javaType = javaType;
	}
	public String getJdbcType() {
		return jdbcType;
	}
	public void setJdbcType(String jdbcType) {
		this.jdbcType = jdbcType;
	}
	public boolean isId() {
		return id;
	}
	
	public void setId(boolean id) {
		this.id = id;
	}
	
	
	public String getSimpleJavaType() {
		return simpleJavaType;
	}
	public void setSimpleJavaType(String simpleJavaType) {
		this.simpleJavaType = simpleJavaType;
	}
	@Override
	public String toString() {
		return "DBColumn [columnName=" + columnName + ", fieldName="
				+ fieldName + ", id=" + id + ", javaType=" + javaType
				+ ", jdbcType=" + jdbcType + "]";
	}
	
}

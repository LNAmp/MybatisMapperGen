package cn.david.paser.metadata;

class DBTable {
	private String name;
	private String database;
	private String alais;
	private String mapperName;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDatabase() {
		return database;
	}
	public void setDatabase(String database) {
		this.database = database;
	}
	public String getAlais() {
		return alais;
	}
	public void setAlais(String alais) {
		this.alais = alais;
	}
	public String getMapperName() {
		if( mapperName == null || mapperName.trim().equals("") ) {
			mapperName  = ( database.equals("") ? "" : database+"." ) + name;
		}
		return mapperName;
	}
}

package cn.david.paser;

import java.util.HashMap;
import java.util.Map;

import cn.david.util.JdbcType;

public class SimpleTypeNameHandler implements JavaTypeNameHandler {

	private Map<String,String> nameMapper;
	
	{
		nameMapper = new HashMap<String, String>();
		nameMapper.put("int", JdbcType.INTEGER);
		nameMapper.put("boolean", JdbcType.BOOLEAN);
		nameMapper.put("float", JdbcType.FLOAT);
		nameMapper.put("double", JdbcType.DOUBLE);
		nameMapper.put("java.lang.Integer", JdbcType.INTEGER);
		nameMapper.put("java.lang.Boolean", JdbcType.BOOLEAN);
		nameMapper.put("java.lang.Float", JdbcType.FLOAT);
		nameMapper.put("java.lang.Double", JdbcType.DOUBLE);
		nameMapper.put("java.lang.String", JdbcType.VARCHAR);
	}
	
	public String jdbcTypeName(String javaTypeName) {
		return nameMapper.get(javaTypeName);
	}

}

package cn.david.paser.metadata;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import cn.david.annotation.Column;
import cn.david.annotation.Id;
import cn.david.annotation.Mapper;
import cn.david.annotation.Table;
import cn.david.exception.IdNotFoundException;
import cn.david.exception.MapperClassNotFoundException;
import cn.david.exception.MapperNotFoundException;
import cn.david.exception.TableNotFoundException;
import cn.david.paser.FieldNameHandler;
import cn.david.paser.JavaTypeNameHandler;
import cn.david.paser.SimpleFieldNameHandler;
import cn.david.paser.SimpleTypeNameHandler;

//完成一个xml映射文件需要的元数据
public class MapperMeta {
	public static class DocType {
		public static final String name = "mapper";
		public static final String publicId = "-//mybatis.org//DTD Mapper 3.0//EN";
		public static final String systemId = "http://mybatis.org/dtd/mybatis-3-mapper.dtd";
	}
	
	private String qualifiedClassName;
	private Class<?> clazz;
	private DBColumn id;
	private Map<String, DBColumn> columns;
	private DBTable table;
	private String daoName;
	private String paramId;
	private String paramBean;
	
	private JavaTypeNameHandler typeNameHandler;
	private FieldNameHandler fieldNameHandler;
	
	public MapperMeta(String qualifiedClassName) {
		this(qualifiedClassName,new SimpleTypeNameHandler(),new SimpleFieldNameHandler());
		
	}
	
	public MapperMeta(String qualifiedClassName, JavaTypeNameHandler jtnh) {
		this(qualifiedClassName,jtnh,new SimpleFieldNameHandler());
	}
	
	public MapperMeta(String qualifiedClassName, JavaTypeNameHandler jtnh, FieldNameHandler fnh) {
		this.qualifiedClassName = qualifiedClassName;
		this.typeNameHandler = jtnh;
		this.fieldNameHandler = fnh;
	}

	public void fillMetaData() {
		//Class<?> clazz = null;
		try {
			clazz = Class.forName(qualifiedClassName);
		} catch (ClassNotFoundException e) {
			throw new MapperClassNotFoundException("Can't find the qualified MapperClass : "+qualifiedClassName+" ,check the input className.");
		}
		Table tableA = clazz.getAnnotation(Table.class);
		if(tableA == null) {
			throw new TableNotFoundException("Can't find the table defination.");
		}
		table = new DBTable();
		table.setAlais(tableA.alias());
		table.setDatabase(tableA.database());
		table.setName(tableA.name());
		Mapper mapper = clazz.getAnnotation(Mapper.class);
		if(mapper == null) {
			throw new MapperNotFoundException("Can't find the Mapper defination.");
		}
		daoName = mapper.name();
		if(mapper.paramId().equals("")) {
			paramId = "id";
		}else {
			paramId = mapper.paramId();
		}
		if(mapper.paramBean().equals("")) {
			paramBean = clazz.getSimpleName();
		}else {
			paramBean = mapper.paramBean();
		}
		//System.out.println(table.database() + "." + table.name() + " " + table.alias());
		Field[] fields = clazz.getDeclaredFields();
		//List<DBColumn> columns = null; 
		if( fields!=null && fields.length>0 ) {
			columns = new LinkedHashMap<String, DBColumn>();
			for(Field f : fields) {
				Annotation[] as =  f.getAnnotations();
				if(as != null && as.length>0) {
					DBColumn column = new DBColumn();
					for(Annotation a : as) {
						if(a instanceof Id) {
							column.setId(true);
							column.setFieldName(f.getName());
							column.setJavaType(f.getType().getName());
							column.setSimpleJavaType(f.getType().getSimpleName());
							id = column;
							//f.getType().getName();
						}else if(a instanceof Column) {
							Column c = (Column)a;
							column.setFieldName(f.getName());
							column.setJavaType(f.getType().getName());
							column.setSimpleJavaType(f.getType().getSimpleName());
							if(c.name()==null || c.name().trim().equals("")) {
								column.setColumnName(fieldNameHandler.getColumnName(f.getName()));
							}else {
								column.setColumnName(c.name());
							}
							if(c.jdbcType()==null || c.jdbcType().trim().equals("")) {
								column.setJdbcType(typeNameHandler.jdbcTypeName(column.getJavaType()));
							}else {
								column.setJdbcType(c.jdbcType());
							}
						}
					}
					if( !column.isId() )
						columns.put(column.getFieldName(), column);
				}
			}
		}
//		if(id != null) {
//			System.out.println(id);
//		}
//		if(columns != null) {
//			System.out.println(columns);
//		}
		if(id == null) {
			throw new IdNotFoundException("Can't find no id defination.");
		}
	}

	public DBColumn getId() {
		return id;
	}

	public Map<String, DBColumn> getColumns() {
		return columns;
	}

	public DBTable getTable() {
		return table;
	}

	public String getDaoName() {
		return daoName;
	}

	public String getParamId() {
		return paramId;
	}

	public void setParamId(String paramId) {
		this.paramId = paramId;
	}

	public String getParamBean() {
		return paramBean;
	}

	public void setParamBean(String paramBean) {
		this.paramBean = paramBean;
	}

	public Class<?> getQualifiedClass() {
		return clazz;
	}
	
	
	
}

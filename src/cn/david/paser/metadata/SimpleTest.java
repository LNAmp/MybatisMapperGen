package cn.david.paser.metadata;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.junit.Test;

import cn.david.annotation.Column;
import cn.david.annotation.Id;
import cn.david.annotation.Table;
import cn.david.paser.FieldNameHandler;
import cn.david.paser.SimpleFieldNameHandler;

public class SimpleTest {

	public static void main(String[] args) {
		Document document = DocumentHelper.createDocument();
		document.addDocType("mapper", "-//mybatis.org//DTD Mapper 3.0//EN", "http://mybatis.org/dtd/mybatis-3-mapper.dtd");

		Element mapper = document.addElement("mapper");
		mapper.addAttribute("namespace", "com.dao.Dao");
		DocumentFactory factory = new DocumentFactory();
		Element sql = factory.createElement("sql");
		sql.addAttribute("id", "columns");
		sql.setText("tar.id tar_id");
		mapper.add(sql);
		
		System.out.println(document.asXML());
	}
	
	@Test
	public void testParser() throws ClassNotFoundException {
		Class<?> clazz = Class.forName("cn.david.pojo.TUser");
		Table table = clazz.getAnnotation(Table.class);
	
		System.out.println(table.database() + "." + table.name() + " " + table.alias());
		Field[] fields = clazz.getDeclaredFields();
		List<DBColumn> columns = null; 
		if(fields!=null && fields.length>0) {
			columns = new ArrayList<DBColumn>();
			for(Field f : fields) {
				Annotation[] as =  f.getAnnotations();
				if(as != null && as.length>0) {
					DBColumn column = new DBColumn();
					for(Annotation a : as) {
						if(a instanceof Id) {
							column.setId(true);
							column.setFieldName(f.getName());
							column.setJavaType(f.getType().getName());
							//f.getType().getName();
						}else if(a instanceof Column) {
							Column c = (Column)a;
							column.setFieldName(f.getName());
							column.setJavaType(f.getType().getName());
							column.setColumnName(c.name());
							column.setJdbcType(c.jdbcType());
						}
					}
					columns.add(column);
				}
			}
		}
		if(columns != null) {
			System.out.println(columns);
		}
	}
	
	@Test
	public void test1() {
		MapperMeta meta = new MapperMeta("cn.david.pojo.TUser");
		meta.fillMetaData();
		new XmlMapperGenerator(meta).generateXml();
	}
	
	@Test
	public void test2() {
		MapperMeta meta = new MapperMeta("cn.david.pojo.TUmgrRole");
		meta.fillMetaData();
		new XmlMapperGenerator(meta).generateXml();
	}
	
	@Test
	public void test3() {
		FieldNameHandler fnh = new SimpleFieldNameHandler();
		System.out.println(fnh.getColumnName("username"));
		System.out.println(fnh.getColumnName("username2"));
		System.out.println(fnh.getColumnName("userId"));
		System.out.println(fnh.getColumnName("systemUserId"));
	}
}

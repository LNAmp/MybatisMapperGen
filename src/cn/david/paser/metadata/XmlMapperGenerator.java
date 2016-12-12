package cn.david.paser.metadata;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.junit.Test;

public class XmlMapperGenerator {
	private String columns = "columns";
	private String whereSql = "condition_where_sql";
	private String allColumns;
	
	private MapperMeta meta;
	
	private String resultMap;
	
	private static class CommonAttrName {
		public static final String id = "id";
		public static final String  parameterType = "parameterType";
		public static final String resultMap = "resultMap";
		public static final String resultType = "resultType";
	}
	
	private static class InsertAttrName extends CommonAttrName {
		public static final String useGeneratedKeys = "useGeneratedKeys";
		public static final String keyProperty = "keyProperty";
		public static final String prefix = "prefix";
		public static final String suffix = "suffix";
		public static final String suffixOverrides = "suffixOverrides";
	}
	
	private static class InsertAttrValue {
		public static final String prefix = "(";
		public static final String suffix = ")";
		public static final String suffixOverrides = ",";
		public static final String prefixOfValue = "values (";
	}
	
//	public XmlMapperGenerator() {
//		
//	}
	
	public XmlMapperGenerator(MapperMeta meta) {
		this.meta = meta;
	}
	
	public void generateXml() {
		Document document = DocumentHelper.createDocument();
		
		document.addDocType(MapperMeta.DocType.name, MapperMeta.DocType.publicId, MapperMeta.DocType.systemId);
		Element mapper = document.addElement("mapper");
		mapper.addAttribute("namespace", meta.getDaoName());
		
		DocumentFactory factory = new DocumentFactory();
		
		mapper.add(genResultMap(factory));
		mapper.add(genColumnsSql(factory));
		mapper.add(genWhereConditionSql(factory));
		mapper.add(genInsertStatement(factory));
		mapper.add(genGetAll(factory));
		mapper.add(genGetOneById(factory));
		mapper.add(genDeleteById(factory));
		mapper.add(genUpdate(factory));
		
		Writer fileWriter = null;
		XMLWriter xmlWriter = null;
		try {
			fileWriter = new FileWriter("D:\\"+meta.getQualifiedClass().getSimpleName()+".xml");
			OutputFormat format = new OutputFormat();
			format.setEncoding("UTF-8");
			xmlWriter = new XMLWriter(fileWriter, format);
			xmlWriter.write(document);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(fileWriter != null) {
				try {
					fileWriter.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(xmlWriter != null) {
				try {
					xmlWriter.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		System.out.println(document.asXML());
	}

	private Element genUpdate(DocumentFactory factory) {
		Element update = factory.createElement("update");
		update.addAttribute(CommonAttrName.id, "update");
		update.addAttribute(CommonAttrName.parameterType, meta.getQualifiedClass().getSimpleName());
		String sql = "update " + meta.getTable().getMapperName() + " " + meta.getTable().getAlais()+" ";
		update.addText(sql);
		Element set = factory.createElement("set");
		if(meta.getColumns()!=null) {
			for(Map.Entry<String, DBColumn> cEntry : meta.getColumns().entrySet()) {
				Element otherIf = factory.createElement("if");
				otherIf.addAttribute("test", genNotNullCause(null, cEntry.getValue()));
				otherIf.setText(genAssignment(null,cEntry.getValue()));
				set.add(otherIf);
			}
		}
		StringBuilder sb = new StringBuilder();
		sb.append(" where ").append(meta.getId().getColumnName());
		sb.append("=#").append("{").append(meta.getId().getColumnName()).append("}");
		update.add(set);
		update.addText(sb.toString());
		return update;
	}
	
	private Element genDeleteById(DocumentFactory factory) {
		Element delete = factory.createElement("delete");
		delete.addAttribute(CommonAttrName.id, "deleteById");
		delete.addAttribute(CommonAttrName.parameterType, meta.getId().getSimpleJavaType());
		StringBuilder sb = new StringBuilder();
		sb.append("delete from ").append(meta.getTable().getMapperName());
		sb.append(" ").append(meta.getTable().getAlais());
		sb.append(" where ").append(meta.getId().getColumnName());
		sb.append("=#").append("{").append(meta.getId().getColumnName()).append("}");
		delete.addText(sb.toString());
		return delete;
	}
	
	private Element genGetOneById(DocumentFactory factory) {
		Element select = factory.createElement("select");
		select.addAttribute(CommonAttrName.id, "getOneById");
		select.addAttribute(CommonAttrName.parameterType, meta.getId().getSimpleJavaType());
		select.addAttribute(CommonAttrName.resultMap, this.resultMap);
		select.addText("select");
		select.add(genInlucde(factory, this.allColumns));
		StringBuilder sb = new StringBuilder();
		sb.append(" from ").append(meta.getTable().getMapperName());
		sb.append(" ").append(meta.getTable().getAlais());
		sb.append(" where ").append(meta.getId().getColumnName());
		sb.append("=#").append("{").append(meta.getId().getColumnName()).append("}");
		select.addText(sb.toString());
		return select;
	}
	
	private Element genInlucde(DocumentFactory factory,String refId) {
		Element includeColumn = factory.createElement("include");
		includeColumn.addAttribute("refid", refId);
		return includeColumn;
	}
	
	private Element genGetAll(DocumentFactory factory) {
		Element select = factory.createElement("select");
		select.addAttribute(CommonAttrName.id, "getAll");
		select.addAttribute(CommonAttrName.parameterType, "map");
		select.addAttribute(CommonAttrName.resultMap, this.resultMap);
		select.addAttribute(CommonAttrName.resultType, "List");
	
//		if(this.includeColumn == null) {
//			throw new RuntimeException("includeColumn is not inited.");
//		}

		
		
		String selectSql = "select ";
		select.addText(selectSql);
		Element includeColumn = factory.createElement("include");
		includeColumn.addAttribute("refid", this.allColumns);
		select.add(includeColumn);
		select.addText(" from " + meta.getTable().getMapperName() + " " + meta.getTable().getAlais());
	
		
		Element includeWhere = factory.createElement("include");
		includeWhere.addAttribute("refid", this.whereSql);
		select.add(includeWhere);
		
		return select;
	}
	
	private Element genInsertStatement(DocumentFactory factory) {
		Element insert = factory.createElement("insert");
		insert.addAttribute(InsertAttrName.id, "save");
		insert.addAttribute(InsertAttrName.parameterType, meta.getQualifiedClass().getSimpleName());
		insert.addAttribute(InsertAttrName.useGeneratedKeys, "true");
		insert.addAttribute(InsertAttrName.keyProperty, meta.getId().getColumnName());
		String insertSql = "insert into " + meta.getTable().getMapperName() + "  ";
		insert.setText(insertSql);
		Element trim = factory.createElement("trim");
		trim.addAttribute(InsertAttrName.prefix, InsertAttrValue.prefix);
		trim.addAttribute(InsertAttrName.suffix, InsertAttrValue.suffix);
		trim.addAttribute(InsertAttrName.suffixOverrides, InsertAttrValue.suffixOverrides);
		Element idIf = factory.createElement("if");
		idIf.addAttribute("test", genNotNullCause(null, meta.getId()));
		idIf.setText(meta.getId().getColumnName()+",");
		trim.add(idIf);
		if(meta.getColumns()!=null) {
			for(Map.Entry<String, DBColumn> cEntry : meta.getColumns().entrySet()) {
				Element otherIf = factory.createElement("if");
				otherIf.addAttribute("test", genNotNullCause(null, cEntry.getValue()));
				otherIf.setText(cEntry.getValue().getColumnName()+",");
				trim.add(otherIf);
			}
		}
		
		Element trimValue = factory.createElement("trim");
		trimValue.addAttribute(InsertAttrName.prefix, InsertAttrValue.prefixOfValue);
		trimValue.addAttribute(InsertAttrName.suffix, InsertAttrValue.suffix);
		trimValue.addAttribute(InsertAttrName.suffixOverrides, InsertAttrValue.suffixOverrides);
		Element idIfValue	 = factory.createElement("if");
		idIfValue.addAttribute("test", genNotNullCause(null, meta.getId()));
		idIfValue.setText(genInputParam(meta.getId()));
		trimValue.add(idIfValue);
		if(meta.getColumns()!=null) {
			for(Map.Entry<String, DBColumn> cEntry : meta.getColumns().entrySet()) {
				Element otherIf = factory.createElement("if");
				otherIf.addAttribute("test", genNotNullCause(null, cEntry.getValue()));
				otherIf.setText(genInputParam(cEntry.getValue()));
				trimValue.add(otherIf);
			}
		}
		insert.add(trim);
		insert.add(trimValue);
		return insert;
	}
	
	private Element genColumnsSql(DocumentFactory factory) {
		Element sql = factory.createElement("sql");
		String columnsSqlId = meta.getTable().getAlais()+"_"+columns;
		allColumns = columnsSqlId;
		sql.addAttribute("id", columnsSqlId);
		String sqlText = genSqlColumns(meta);
		sql.setText(sqlText);
		return sql;
	}
	
	private Element genResultMap(DocumentFactory factory) {
		String prefix = meta.getTable().getAlais()+"_";
		Element resultMap = factory.createElement("resultMap");
		resultMap.addAttribute("type", meta.getQualifiedClass().getSimpleName());
		this.resultMap = meta.getTable().getAlais()+"Map";
		resultMap.addAttribute("id", meta.getTable().getAlais()+"Map");
		Element id = factory.createElement("id");
		id.addAttribute("column", prefix + meta.getId().getColumnName());
		id.addAttribute("property", meta.getId().getFieldName());
		id.addAttribute("jdbcType", meta.getId().getJdbcType());
		resultMap.add(id);
		if(meta.getColumns()!=null) {
			for(Map.Entry<String, DBColumn> cEntry : meta.getColumns().entrySet()) {
				Element result = factory.createElement("result");
				result.addAttribute("column", prefix + cEntry.getValue().getColumnName());
				result.addAttribute("property", cEntry.getValue().getFieldName());
				result.addAttribute("jdbcType", cEntry.getValue().getJdbcType());
				resultMap.add(result);
				//TODO add list or set:association and collection
			}
		}
		
//		Element includeColumn = factory.createElement("include");
//		includeColumn.addAttribute("refid", this.allColumns);
//		String tmp = includeColumn.asXML();
//		tmp.replace("&lt;", "\\<");
//		tmp.replace("&gt;", "\\>");
		
//		this.includeColumn = tmp;

		return resultMap;
	}
	
	private Element genWhereConditionSql(DocumentFactory factory) {
		Element whereSql = factory.createElement("sql");
		whereSql.addAttribute("id",this.whereSql);
		Element outIf = factory.createElement("if");
		outIf.addAttribute("test", genNotNullCause(meta.getParamBean(), null));
		Element where = factory.createElement("where");
		Element idIf = factory.createElement("if");
		idIf.addAttribute("test", genNotNullCause(meta.getParamBean(), meta.getId()));
		idIf.setText(genAndAssignment(meta.getParamBean(), meta.getId()));
		where.add(idIf);
		if(meta.getColumns()!=null) {
			for(Map.Entry<String, DBColumn> cEntry : meta.getColumns().entrySet()) {
				Element innerIf = factory.createElement("if");
				innerIf.addAttribute("test", genNotNullCause(meta.getParamBean(), cEntry.getValue()));
				innerIf.setText(genAndAssignment(meta.getParamBean(), cEntry.getValue()));
				where.add(innerIf);
			}
		}
		outIf.add(where);
		whereSql.add(outIf);
		return whereSql;
	}
	
	private String genInputParam(DBColumn column) {
		StringBuilder sb = new StringBuilder();
		sb.append("#{");
		sb.append(column.getFieldName());
		sb.append(",");
		sb.append("jdbcType=");
		sb.append(column.getJdbcType());
		sb.append("},");
		return sb.toString();
	}

	private String genSqlColumns(MapperMeta meta) {
		StringBuilder sb = new StringBuilder();
		String alais = meta.getTable().getAlais();
		sb.append(alais).append(".").append(meta.getId().getColumnName()).append(" ");
		sb.append(alais).append("_").append(meta.getId().getColumnName());
		if(meta.getColumns()!=null) {
			for(Map.Entry<String, DBColumn> cEntry : meta.getColumns().entrySet()) {
				sb.append(",");
				DBColumn column = cEntry.getValue();
				sb.append(alais).append(".").append(column.getColumnName()).append(" ");
				sb.append(alais).append("_").append(column.getColumnName());
			}
		}
		return sb.toString();
	}
	
	private String genNotNullCause(String beanName, DBColumn column) {
		StringBuilder sb = new StringBuilder();
		String notNull = " != null ";
		if(beanName != null  ) {
			//throw new RuntimeException("beanName can not be empty.");
			sb.append(beanName);
		}
		
		if(column != null) {
			if(beanName != null ) {
				sb.append(".");
			}
			sb.append(column.getFieldName());
		}
		
		sb.append(notNull);
		return sb.toString();
	}
	
	private String genAndAssignment(String beanName, DBColumn column) {
		StringBuilder sb = new StringBuilder();
		if(!column.isId()) {
			sb.append(" and ");
		}
		sb.append(genAssignment(beanName, column));
		return sb.toString();
	}
	
	private String genAssignment(String beanName, DBColumn column) {
		StringBuilder sb = new StringBuilder();
		sb.append(column.getColumnName()).append("=").append("#");
		//TODO :to add jdbcType
		sb.append("{");
		if(beanName != null) {
			sb.append(beanName).append(".");
		}
		sb.append(column.getFieldName());
		sb.append("}");
		return sb.toString();
	}
	
	@Test
	public void tesGenNotNullCasue() {
		DBColumn column = new DBColumn();
		column.setColumnName("provice_id");
		column.setFieldName("proviceId");
		String beanName = "city";
		//String notNull = genNotNullCause(beanName, column);
		System.out.println(genNotNullCause(beanName, null));
	}
	
	@Test
	public void testGenAndAssignment() {
		DBColumn column = new DBColumn();
		column.setColumnName("provice_id");
		column.setFieldName("proviceId");
		column.setId(true);
		String beanName = "city";
		System.out.println(genAndAssignment(beanName, column));
	}
}

package cn.david.pojo;

import cn.david.annotation.Column;
import cn.david.annotation.Id;
import cn.david.annotation.Mapper;
import cn.david.annotation.Table;

@Mapper(name="com.arpicots.dao.user.ITUmgrRoleDao",paramBean="role")
@Table(alias="tur",database="hdic_user",name="t_umgr_role")
public class TUmgrRole {
	
	@Id
	@Column(name="id",jdbcType="INTEGER")
	private Integer id;
	/**
     * 角色名称
     */
	@Column(name="role_name",jdbcType="VARCHAR")
    private String roleName;

    /**
     * 角色编码
     */
	@Column(name="role_code",jdbcType="VARCHAR")
    private String roleCode;

    /**
     * 角色描述
     */
	@Column(name="description",jdbcType="VARCHAR")
    private String description;

    /**
     * 角色状态
     */
	@Column(name="status",jdbcType="VARCHAR")
    private String status;

    /**
     * 角色对应城市
     */
	@Column(name="city_code",jdbcType="VARCHAR")
    private String cityCode;

    /**
     * 角色类型
     */
	@Column(name="role_level",jdbcType="INTEGER")
    private Integer roleLevel;
    
    /**
     * 子节点数
     */
	@Column(name="child_count",jdbcType="INTEGER")
    private Integer childCount;
    
    /**
     * 子节点数
     */
	@Column(name="parent_id",jdbcType="INTEGER")
    private Integer parentID;

    /**
     * 应用程序模块ID
     */
	@Column(name="app_id",jdbcType="INTEGER")
    private Integer appId;
}

package cn.david.pojo;

import cn.david.annotation.Column;
import cn.david.annotation.Id;
import cn.david.annotation.Mapper;
import cn.david.annotation.Table;

@Mapper(name="cn.david.dao.IUserDao")
@Table(alias="tuser",name="user")
public class TUser {
	@Id
	@Column(name="user_id")
	private int id;
	
	@Column
	private String userName;
	
	@Column(name="enc_pwd")
	private String password;
}

package cn.itrip.dao.itripUser;

import cn.itrip.pojo.*;
import org.apache.ibatis.annotations.Param;
import sun.awt.SunHints;

import java.util.List;
import java.util.Map;

public interface ItripUserMapper {


    public ItripUser getitripuser(@Param(value = "userCode") String userCode) throws Exception;

    // 检查邮箱有没有被使用
    public int ifuserExists(@Param(value = "userCode") String userCode) throws Exception;

    public ItripUser ifLogin(@Param(value = "userCode") String userCode, @Param(value = "userPassword") String userPassword) throws Exception;

    public ItripUser getItripUserById(@Param(value = "id") String id) throws Exception;

    public List<ItripUser> getItripUserListByMap(Map<String, Object> param) throws Exception;

    public Integer getItripUserCountByMap(Map<String, Object> param) throws Exception;

    public Integer insertItripUser(ItripUser itripUser) throws Exception;

    public Integer updateItripUserbycode(@Param(value = "userCode") String userCode) throws Exception;

    public Integer updateItripUser(ItripUser itripUser) throws Exception;

    public Integer deleteItripUserById(@Param(value = "id") Long id) throws Exception;

}

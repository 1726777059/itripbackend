package com.bdqn.controller;

import cn.itrip.common.*;
import cn.itrip.dao.itripUser.ItripUserMapper;

import cn.itrip.pojo.ItripUser;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bdqn.biz.TokenBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import org.apache.ibatis.annotations.Lang;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Calendar;
import java.util.Random;

@Controller
@RequestMapping("/api")
@Api(value = "api", description = "用户模块")
public class UserController {

    @Resource
    ItripUserMapper itripUserMapper;

    @Resource
    TokenBiz biz;

    @Resource
    JredisApi redisAPI;


    @RequestMapping(value = "/dologin", method = RequestMethod.POST)
    @ResponseBody
    @ApiImplicitParams({
            //paramType = "query"-->如果是get请求则为query，如果是post请求则为form,required = true -->值必须传,value = "提示",name = "映射到哪个字段",defaultValue ="如果没写映射字段，给一个默认值"
            @ApiImplicitParam(paramType = "form", required = true, value = "用户名", name = "name"),
            @ApiImplicitParam(paramType = "form", required = true, value = "密码", name = "password")
    })
    public Dto getList(HttpServletRequest request, String name, String password) {
        ItripUser itripUser = null;
        try {
            //判断数据库中是否存在,密码采用了MD5算法进行加密
            itripUser = itripUserMapper.ifLogin(name, MD5.getMd5(password, 32));
            //如果存在，将该标识（手机端登录还是pc端登录）存入到redis中
            if (itripUser != null) {
                //生成一个token
                String token = biz.generateToken(request.getHeader("User-Agent"), itripUser);
                redisAPI.SetRedis(token, 7200, JSONObject.toJSONString(itripUser));

                //将当前时间与过期时间返回到前台
                ItripTokenVO tokenVO = new ItripTokenVO(token, Calendar.getInstance().getTimeInMillis() + 7200, Calendar.getInstance().getTimeInMillis());
                return DtoUtil.returnDataSuccess(tokenVO);
            }
            /*itripUser = itripUserMapper.getItripUserById(new Long(12));*/


        } catch (Exception e) {

        }
        return DtoUtil.returnFail("登录失败", "1000");
    }

    @RequestMapping(value = "/activate")
    @ResponseBody
    public Dto activate(String user, String code) throws Exception {
        String code2 = redisAPI.getRedis("Code:" + user);
        if (code2.equals(code)) {
            itripUserMapper.updateItripUserbycode(user);
            return DtoUtil.returnSuccess("激活成功", itripUserMapper.getitripuser(user));
        }
        return DtoUtil.returnFail("激活失败", "10000");


    }


    @RequestMapping(value = "/ckusr", method = RequestMethod.GET)
    @ResponseBody
    public Dto ckusr(String name) throws Exception {
// 验证是否已存在给用户名
        int result = itripUserMapper.ifuserExists(name);
        if (result == 0) {
            return DtoUtil.returnSuccess("不存在此用户,可以使用");
        } else {
            return DtoUtil.returnFail("此用户已存在", "10000");
        }


    }

    @RequestMapping(value = "/doregister")
    @ResponseBody
    public Dto doregister(@RequestBody ItripUser itripUser) throws Exception {
// 为手机发送验证码 同时将验证码存入 redis , 将 itripuser 存入数据库(如果不存在)
        ItripUser itripUser2 = new ItripUser();
        itripUser2.setUserCode(itripUser.getUserCode());
        itripUser2.setUserName(itripUser.getUserName());
        itripUser2.setUserPassword(itripUser.getUserPassword());
        itripUser2.setActivated(0);
        String verifyCode = String.valueOf(new Random().nextInt(899999) + 100000);
        Sendmail.sendmessage(verifyCode);
        redisAPI.SetRedis("Code:" + itripUser.getUserCode(), 60, verifyCode);


// 发送验证码
        /*sentSSM.SetPhone("13015340450", verifyCode + "");*/
// 将验证码存储
        redisAPI.SetRedis("Code:" + itripUser.getUserCode(), 30, verifyCode);
// 将 itripuser 存入数据库
        int result = itripUserMapper.insertItripUser(itripUser2);

        if (result > 0) {
            return DtoUtil.returnDataSuccess(itripUser2);
        }
        return DtoUtil.returnFail("注册失败", "10000");
    }

    @RequestMapping(value = "/registerbyphone")
    @ResponseBody
    public Dto registerbyphone(@RequestBody ItripUser itripUser) throws Exception {
// 为手机发送验证码 同时将验证码存入 redis , 将 itripuser 存入数据库(如果不存在)
        ItripUser itripUser2 = new ItripUser();
        itripUser2.setUserCode(itripUser.getUserCode());
        itripUser2.setUserName(itripUser.getUserName());
        itripUser2.setUserPassword(itripUser.getUserPassword());
        itripUser2.setActivated(0);
        SentSSM sentSSM = new SentSSM();
        String verifyCode = String.valueOf(new Random().nextInt(899999) + 100000);
// 发送验证码
        /*sentSSM.SetPhone("13015340450", verifyCode + "");*/
// 将验证码存储
        redisAPI.SetRedis("Code:" + itripUser.getUserCode(), 30, verifyCode);
// 将 itripuser 存入数据库
        int result = itripUserMapper.insertItripUser(itripUser2);

        if (result > 0) {
            return DtoUtil.returnDataSuccess(itripUser2);
        }
        return DtoUtil.returnFail("注册失败", "10000");
    }

    @RequestMapping(value = "/validatephone")
    @ResponseBody
    public Dto validatephone(String user, String code) throws Exception {
        // 在redis中的密码是被加密过的 ,需要加密后比对
        String str1 = code;
        String str2 = redisAPI.getRedis("Code:" + user);
        System.out.println(str2 + str1);
        if (str1.equals(str2)) {
            // 修改此手机号对应得数据的激活
            itripUserMapper.updateItripUserbycode(user);
            ItripUser itripUser = itripUserMapper.getitripuser(user);
            return DtoUtil.returnDataSuccess(itripUser);


        } else {
            return DtoUtil.returnFail("激活失败", "10000");
        }


    }
}

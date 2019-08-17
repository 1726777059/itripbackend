package com.bdqn.controller;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Testflt {
    public static void main(String[] args){
        entity entity = new entity(1,"xxz");
        entity entity1 = new entity(2,"ttc");
        List<entity> list = new ArrayList<entity>();
        list.add(entity);
        list.add(entity1);

        Map<String,Object> map = new HashMap<>();
        map.put("li",list);
        /*Map<String,Object> map = new HashMap<>();
        map.put("name","夏小召");*/
        Configuration configuration = new Configuration();
        /* 设置字符编码 */
        configuration.setDefaultEncoding("utf-8");
        try {
            configuration.setDirectoryForTemplateLoading(new File("G:\\Users\\Administrator\\334-iTrip\\itripbackend\\itripauth\\src\\main\\resources"));
            //找到模板
            Template template = configuration.getTemplate("Test.flt");
            //向模板中传递数据
            try {
                template.process(map,new FileWriter("D:\\a.txt"));
            } catch (TemplateException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

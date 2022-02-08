package com.yhr.yygh.cmn.service.impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yhr.yygh.cmn.listener.DictListener;
import com.yhr.yygh.cmn.mapper.DictMapper;
import com.yhr.yygh.cmn.service.DictService;
import com.yhr.yygh.model.cmn.Dict;
import com.yhr.yygh.vo.cmn.DictEeVo;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class DictServiceImpl extends ServiceImpl<DictMapper, Dict> implements DictService {
    @Override
    @Cacheable(value = "dict",keyGenerator = "keyGenerator")
    public List<Dict> findChildData(Long id) {
        QueryWrapper<Dict> wrapper = new QueryWrapper<>();
        wrapper.eq("parent_id",id);
        List<Dict> dictList = baseMapper.selectList(wrapper);
        //向list集合每个Dict对象中设置hasChildren值
        for (Dict dict : dictList) {
            Long dictId = dict.getId();
            boolean hasChildren = this.isChildren(dictId);
            dict.setHasChildren(hasChildren);
        }
        return dictList;
    }

    @Override
    public void exportDictData(HttpServletResponse response) {
        //设置下载信息
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        String fileName = "dict";
        response.setHeader("Content-disposition","attachment;filename=" + fileName + ".xlsx");
        //查询数据库
        List<Dict> dicts = baseMapper.selectList(null);
        //Dict -> DictEeVo
        ArrayList<DictEeVo> dictVoList = new ArrayList<>();
        for (Dict dict : dicts) {
            DictEeVo dictEeVo = new DictEeVo();
            BeanUtils.copyProperties(dict,dictEeVo);
            dictVoList.add(dictEeVo);
        }

        try {
            EasyExcel.write(response.getOutputStream(), DictEeVo.class).sheet("dict")
                    .doWrite(dictVoList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 导入
     * allEntries = true: 方法调用后清空所有缓存
     * @param file
     */
    @Override
    @CacheEvict(value = "dict",allEntries = true)
    public void importDictData(MultipartFile file) {
        try {
            EasyExcel.read(file.getInputStream(),DictEeVo.class,new DictListener(baseMapper)).sheet().doRead();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getDictName(String dictCode, String value) {
        if (StringUtils.isEmpty(dictCode)){
            QueryWrapper<Dict> wrapper = new QueryWrapper<>();
            wrapper.eq("value",value);
            Dict dict = baseMapper.selectOne(wrapper);
            return dict.getName();
        }else {
            //根据dictcode查询dict对象，得到dict的id值
            QueryWrapper<Dict> wrapper = new QueryWrapper<>();
            wrapper.eq("dict_code",dictCode);
            Dict dict = baseMapper.selectOne(wrapper);
            Long parentId = dict.getId();
            //根据parentid和value进行查询
            Dict finalDict = baseMapper.selectOne(new QueryWrapper<Dict>().eq("parent_id", parentId).eq("value", value));
            return finalDict.getName();
        }
    }

    @Override
    public List<Dict> findByDictCode(String dictCode) {
        //根据dictcode获取id，再根据id获取下层子节点
        Dict dict = getDictByDictCode(dictCode);
        List<Dict> list = findChildData(dict.getId());
        return list;
    }

    private Dict getDictByDictCode(String dictCode){
        QueryWrapper<Dict> wrapper = new QueryWrapper<>();
        wrapper.eq("dict_code",dictCode);
        Dict dict = baseMapper.selectOne(wrapper);
        return dict;
    }

    //判断id下面是否有子节点
    private boolean isChildren(Long id){
        QueryWrapper<Dict> wrapper = new QueryWrapper<>();
        wrapper.eq("parent_id",id);
        Integer count = baseMapper.selectCount(wrapper);
        return count > 0;
    }
}
